package edu.bonn.mobilegaming.geoquest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;
import edu.bonn.mobilegaming.geoquest.ui.MenuMaker;
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

		initProgressDialogs();

		downloadRepoDataProgress = (ProgressBar) findViewById(R.id.start_progress_game_list);
		gameListProgressDescr = (TextView) findViewById(R.id.start_game_list_progress_descr);
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

	private void initProgressDialogs() {
		downloadRepoDataDialog = new ProgressDialog(this);
		downloadRepoDataDialog
				.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		downloadRepoDataDialog.setCancelable(false);
		startLocalGameDialog = new ProgressDialog(this);
		startLocalGameDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		startLocalGameDialog.setCancelable(false);
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
				} else {
					Toast.makeText(this, "Error loading game info!",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				/*
				 * TODO: this shouldn't happen! Prevent at Preferences.java! For
				 * the first application start we could also initialize this
				 * these preference settings in GeoQuestApp.onCreate()
				 */
				Toast.makeText(
						this,
						"Auto-start quest at application was activated, but no quest choosen! Please choose a startup quest!",
						Toast.LENGTH_LONG).show();
				Log.i("Geoquest#" + TAG,
						"AutoStartGame is checked but no quest was choosen!");
			}
		} else {
			loadRepoData(false);
		}

		super.onResume();
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
		menuMaker.removeMenuItems(MenuMaker.END_GAME_MENU_ID,
				MenuMaker.HISTORY_MENU_ID);
		menuMaker.addMenuItem(MenuMaker.RELOAD_GAMES_MENU_ID,
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						loadRepoData(true);
						return true;
					}
				});
		menuMaker.setupMenu(menu);
		return true;
	}

	/**
	 * Called right before your activity's option menu is displayed.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
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
