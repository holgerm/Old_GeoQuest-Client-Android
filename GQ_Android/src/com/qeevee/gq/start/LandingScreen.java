package com.qeevee.gq.start;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qeevee.util.Dialogs;
import com.qeevee.gq.GeoQuestActivity;
import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Mission;
import com.qeevee.gq.Preferences;
import com.qeevee.gq.R;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;

public class LandingScreen extends GeoQuestActivity {

	private ProgressDialog startLocalGameDialog;
	private ProgressDialog downloadRepoDataDialog;
	private Button showCloudGamesButton;
	private Button showLocalGamesButton;

	@SuppressWarnings("unused")
	private static final String TAG = LandingScreen.class.getCanonicalName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing_screen);
		Mission.setMainActivity(this);

		// set button listeners:
		setButtonListeners();

		GameDataManager.getQuestsDir();
	}

	private void setButtonListeners() {
		showCloudGamesButton = (Button) findViewById(R.id.showCloudQuests);
		showCloudGamesButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showCloudGames();
			}
		});

		showLocalGamesButton = (Button) findViewById(R.id.showLocalQuests);
		showLocalGamesButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showLocalGames();
			}
		});
	}

	/**
	 * This method calls {@link GeoQuestActivity#startGame(GameItem, String)} in
	 * case you are in AutoStart mode.
	 */
	@Override
	protected void onResume() {
		super.onResume();

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
		int itemId = item.getItemId();
		if (itemId == R.id.menu_quit) {
			showDialog(Dialogs.DIALOG_TERMINATE_APP);
			return true;
		} else if (itemId == R.id.menu_showLocalGames) {
			showLocalGames();
			return true;
		} else if (itemId == R.id.menu_showCloudGames) {
			showCloudGames();
			return true;
		} else if (itemId == R.id.menu_preferences) {
			showPreferences();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void showPreferences() {
		Intent i = new Intent(this, Preferences.class);
		startActivity(i);
	}

	private void showCloudGames() {
		Intent searchActivity = new Intent(getBaseContext(), GamesInCloud.class);
		startActivity(searchActivity);
	}

	private void showLocalGames() {
		Intent loadActivity = new Intent(getBaseContext(), LocalGames.class);
		startActivity(loadActivity);
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
		menu.findItem(R.id.menu_showCloudGames).setVisible(areWeOnline());
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
