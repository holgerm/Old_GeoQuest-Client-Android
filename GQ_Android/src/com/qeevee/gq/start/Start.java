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
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qeevee.util.Dialogs;

import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.GeoQuestProgressHandler;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.Preferences;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;
import edu.bonn.mobilegaming.geoquest.views.GeoquestButton;

public class Start extends GeoQuestActivity {

	private ProgressDialog startLocalGameDialog;
	private ProgressDialog downloadRepoDataDialog;

	private static final String TAG = "Start";

	private ProgressBar downloadRepoDataProgress;
	private TextView gameListProgressDescr;
	private GeoquestButton gameListButton;
	private GeoquestButton lastGameButton;
	// private TextView lastGameNameText;

	static final int FIRST_LOCAL_MENU_ID = GeoQuestActivity.MENU_ID_OFFSET;

	static final String RELOAD_REPO_DATA = "edu.bonn.mobilegaming.geoquest.start.reload_repo_data";
	private static final String ASSET_FILE_FOR_AUTOSTART_ID = "autostart_id";
	private final int repoListRequestCode = 101;

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
		// final String recentRepo = GeoQuestApp.getRecentRepo();
		// final String recentGame = GeoQuestApp.getRecentGame();
		// final String recentGameFileName =
		// GeoQuestApp.getRecentGameFileName();
		// if (recentRepo != null && recentGame != null) {
		// // there has been played a game recently
		// if (GameLoader.existsGameOnClient(recentRepo, recentGameFileName)) {
		// // game exists locally and can be offered to start again:
		//
		// // overwrite displayed message
		// Button.OnClickListener restartRecentGameListener =
		// createGameButtonClickListener(
		// recentRepo, recentGameFileName);
		//
		// lastGameButton.setOnClickListener(restartRecentGameListener);
		// lastGameButton.setEnabled(true);
		// lastGameButton.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		// lastGameButton.setText(recentGame);
		// } else {
		// disableLastGameButton(R.string.start_gameNotFound);
		// }
		// } else {
		// disableLastGameButton(R.string.start_text_last_game_text_no_game);
		// }
		//
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
		boolean isUsingAutoStart = checkAndPerformAutostartByAssets()
				|| checkAndPerformAutostartFormPrefs();
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
	 * @return true iff the user has specified autostart quest in preferences.
	 */
	private boolean checkAndPerformAutostartFormPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean(Preferences.PREF_KEY_AUTO_START_GAME_CHECK, false)) {
			if (prefs.contains(Preferences.PREF_KEY_AUTO_START_REPO)
					&& prefs.contains(Preferences.PREF_KEY_AUTO_START_GAME)) {

				lastGameButton.setEnabled(false);
				gameListButton.setEnabled(false);

				final GeoQuestProgressHandler downloadRepoDataHandler;

				downloadRepoDataHandler = new GeoQuestProgressHandler(
						downloadRepoDataProgress, gameListProgressDescr,
						GeoQuestProgressHandler.LAST_IN_CHAIN);

				if (!GeoQuestApp.getInstance().isRepoDataLoaded()) {
					GeoQuestApp.loadRepoData(downloadRepoDataHandler);
				}

				String repoName = prefs.getString(
						Preferences.PREF_KEY_AUTO_START_REPO, "");
				String gameName = prefs.getString(
						Preferences.PREF_KEY_AUTO_START_GAME, "");
				GameItem gameItem = GeoQuestApp.getGameItem(repoName, gameName);
				if (gameItem != null) {
					startGame(gameItem, repoName);
					return true;
				} else {
					Toast.makeText(this, "Error loading game info!",
							Toast.LENGTH_SHORT).show();
					return false;
				}
			} else {
				/*
				 * TODO: this shouldn't happen! Prevent at Preferences.java! For
				 * the first application start we could also initialize these
				 * preference settings in GeoQuestApp.onCreate()
				 */
				Toast.makeText(
						this,
						"Auto-start quest at application was activated, but no quest choosen! Please choose a startup quest!",
						Toast.LENGTH_LONG).show();
				Log.i("Geoquest#" + TAG,
						"AutoStartGame is checked but no quest was choosen!");
				return false;
			}
		} else
			return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case repoListRequestCode:
			if (data != null && data.getBooleanExtra(RELOAD_REPO_DATA, false)) {
				loadRepoData(true);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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

	/**
	 * This method reloads all repo data from the server. Additionally it takes
	 * care of all the needed GUI download indicators.
	 * 
	 * @param force
	 *            if <code>true</code>, the repo data will be reloaded
	 *            regardless whether the data has already been downloaded
	 */
	private void loadRepoData(final boolean force) {
		runOnUiThread(new Runnable() {
			public void run() {
				downloadRepoDataProgress.setProgress(0);
				downloadRepoDataProgress.setVisibility(View.VISIBLE);
				gameListButton.setVisibility(View.GONE);
				gameListProgressDescr.setVisibility(View.VISIBLE);
			}
		});
		final GeoQuestProgressHandler loadRepoDataHandler = new GeoQuestProgressHandler(
				downloadRepoDataProgress, gameListProgressDescr,
				GeoQuestProgressHandler.LAST_IN_CHAIN);
		GeoQuestApp.singleThreadExecutor.execute(new Runnable() {

			public void run() {
				if ((!force && GeoQuestApp.getInstance().isRepoDataLoaded())
						|| GeoQuestApp.loadRepoData(loadRepoDataHandler)) {
					// TODO: add fade in/out animations
					runOnUiThread(new Runnable() {
						public void run() {
							downloadRepoDataProgress
									.setVisibility(View.INVISIBLE);
							gameListButton.setVisibility(View.VISIBLE);
							gameListProgressDescr.setVisibility(View.GONE);
						}
					});

				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(),
									R.string.start_toast_download_failed,
									Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		});
	}
}
