package com.qeevee.gq.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.qeevee.util.Dialogs;

import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;

public class Start extends GeoQuestActivity {

	private ProgressDialog startLocalGameDialog;
	private ProgressDialog downloadRepoDataDialog;

	private static final String TAG = "Start";

	static final int FIRST_LOCAL_MENU_ID = GeoQuestActivity.MENU_ID_OFFSET;

	static final String RELOAD_REPO_DATA = "edu.bonn.mobilegaming.geoquest.start.reload_repo_data";
	private static final String ASSET_FILE_FOR_AUTOSTART_ID = "autostart_id";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		Mission.setMainActivity(this);

		// extract included games asynchronously:
		new ExtractGamesFromAssets().execute();

		GameDataManager.getQuestsDir();
	}

	private void startQuestFromQRCodeScan() {
		Intent startingIntent = getIntent();
		String startString = null;
		if (startingIntent == null)
			return;

		startString = startingIntent.getDataString();
		if (startString == null)
			return;

		Log.d(TAG, "started with intent. Received: " + startString);
		int indexOfGameID = startString.lastIndexOf('/');
		if (indexOfGameID == -1)
			return;

		String gameID = startString.substring(indexOfGameID);
		if (GameDataManager.existsLocalQuest(gameID)) {
			File gameDir = GameDataManager.getQuestDir(gameID);
			GameDescription gameDescr = new GameDescription(gameDir);
			new StartLocalGame().execute(gameDescr);
		}
	}

	/**
	 * This method calls {@link GeoQuestActivity#startGame(GameItem, String)} in
	 * case you are in AutoStart mode.
	 */
	@Override
	protected void onResume() {
		checkAndPerformAutostart();

		super.onResume();

		startQuestFromQRCodeScan();

	}

	/**
	 * If an autostart quest is defined in assets, i.e. precompiled with the
	 * app, this is called and no other quest can be started with this app.
	 * 
	 * If the user has set an autostart quest in the preferences (which is only
	 * possible if no autostart quest is set in the assets), it will be started.
	 * This setting can only after the start be changed again.
	 */
	private void checkAndPerformAutostart() {
		boolean isUsingAutoStart = checkAndPerformAutostartByAssets();
		GeoQuestApp.getInstance().setUsingAutostart(isUsingAutoStart);
	}

	private boolean checkAndPerformAutostartByAssets() {
		boolean autostart = false;
		AssetManager assetManager = GeoQuestApp.getContext().getAssets();
		String autostartGameID = null;
		try {
			InputStream is = assetManager.open(ASSET_FILE_FOR_AUTOSTART_ID);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			autostartGameID = reader.readLine();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (autostartGameID != null) {
			if (GameDataManager.existsLocalQuest(autostartGameID)) {
				autostart = true;
				File gameDir = GameDataManager.getQuestDir(autostartGameID);
				GameDescription gameDescr = new GameDescription(gameDir);
				new StartLocalGame().execute(gameDescr);
			}
		}
		return autostart;
	}

	/**
	 * Called when the activity's options menu needs to be created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_quit:
			showDialog(Dialogs.DIALOG_TERMINATE_APP);
			return true;
		case R.id.menu_showLocalGames:
			Intent loadActivity = new Intent(getBaseContext(), LocalGames.class);
			startActivity(loadActivity);
			return true;
		case R.id.menu_cloudsearch:
			Intent searchActivity = new Intent(getBaseContext(),
					GamesInCloud.class);
			startActivity(searchActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called right before your activity's option menu is displayed.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		updateCloudSearchMenuItem(menu);
		return true;
	}

	private void updateCloudSearchMenuItem(Menu menu) {
		menu.findItem(R.id.menu_cloudsearch).setVisible(areWeOnline());
	}

	private boolean areWeOnline() {
		final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Dialogs.DIALOG_TERMINATE_APP:
			Dialog dialog = Dialogs.createYesNoDialog(this,
					R.string.dialogTerminateAppTitle,
					Dialogs.terminateAppOnClickListener,
					Dialogs.cancelOnClickListener);
			dialog.show();
			break;
		case GeoQuestApp.DIALOG_ID_DOWNLOAD_REPO_DATA:
			return downloadRepoDataDialog;
		case GeoQuestApp.DIALOG_ID_START_GAME:
			return startLocalGameDialog;
		default:
			return null;
		}
		return super.onCreateDialog(id);
	}

}
