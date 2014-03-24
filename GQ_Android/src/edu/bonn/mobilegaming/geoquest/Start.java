package edu.bonn.mobilegaming.geoquest;

import java.io.File;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qeevee.gq.start.GameDataManager;
import com.qeevee.gq.start.GameDescription;
import com.qeevee.gq.start.GamesInCloud;
import com.qeevee.gq.start.LocalGames;
import com.qeevee.gq.start.StartLocalGame;

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

	private final int repoListRequestCode = 101;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webupdate);
		Mission.setMainActivity(this);

		GameDataManager.getQuestsDir();

		gameListButton = (GeoquestButton) findViewById(R.id.start_button_game_list);

		gameListButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(GeoQuestApp.getContext(),
						edu.bonn.mobilegaming.geoquest.RepoListActivity.class);
				startActivityForResult(intent, repoListRequestCode);
			}
		});

		lastGameButton = (GeoquestButton) findViewById(R.id.start_button_last_game);
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
		final String recentRepo = GeoQuestApp.getRecentRepo();
		final String recentGame = GeoQuestApp.getRecentGame();
		final String recentGameFileName = GeoQuestApp.getRecentGameFileName();
		if (recentRepo != null && recentGame != null) {
			// there has been played a game recently
			if (GameLoader.existsGameOnClient(recentRepo, recentGameFileName)) {
				// game exists locally and can be offered to start again:

				// overwrite displayed message
				Button.OnClickListener restartRecentGameListener = createGameButtonClickListener(
						recentRepo, recentGameFileName);

				lastGameButton.setOnClickListener(restartRecentGameListener);
				lastGameButton.setEnabled(true);
				lastGameButton.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				lastGameButton.setText(recentGame);
			} else {
				disableLastGameButton(R.string.start_gameNotFound);
			}
		} else {
			disableLastGameButton(R.string.start_text_last_game_text_no_game);
		}

		GeoQuestApp.getInstance().setUsingAutostart(checkAndPerformAutostart());

		super.onResume();

		startQuestFromQRCodeScan();

	}

	/**
	 * @return true iff autostart is applied.
	 */
	private boolean checkAndPerformAutostart() {
		return checkAndPerformAutostartByAssets()
				&& checkAndPerformAutostartFormPrefs();
	}

	private boolean checkAndPerformAutostartByAssets() {
		// TODO Auto-generated method stub
		return false;
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

	private void disableLastGameButton(int messageStringID) {
		lastGameButton.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		lastGameButton.setText(messageStringID);
		lastGameButton.setEnabled(false);
	}

	private OnClickListener createGameButtonClickListener(final String repo,
			final String gameFileName) {
		OnClickListener gameButtonClickListener = new OnClickListener() {
			public void onClick(View v) {
				final Handler startGameHandler;

				startLocalGameDialog.setProgress(0);
				startLocalGameDialog.setMessage(GeoQuestApp.getContext()
						.getText(R.string.start_startGame));
				showDialog(GeoQuestApp.DIALOG_ID_START_GAME);
				startGameHandler = new GeoQuestProgressHandler(
						startLocalGameDialog,
						GeoQuestProgressHandler.LAST_IN_CHAIN);
				GeoQuestApp.singleThreadExecutor.execute(new Runnable() {

					public void run() {
						GameLoader.startGame(startGameHandler,
								GeoQuestApp.getGameXMLFile(repo, gameFileName));
					}

				});
			}

		};
		return gameButtonClickListener;
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
			GeoQuestApp.getInstance().terminateApp();
			return true;
		case R.id.menu_reload:
			// loadRepoData(true);
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
		case GeoQuestApp.DIALOG_ID_DOWNLOAD_REPO_DATA:
			return downloadRepoDataDialog;
		case GeoQuestApp.DIALOG_ID_START_GAME:
			return startLocalGameDialog;
		default:
			return null;
		}
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
