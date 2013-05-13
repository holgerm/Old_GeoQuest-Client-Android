package edu.bonn.mobilegaming.geoquest;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;
import edu.bonn.mobilegaming.geoquest.ui.MenuMaker;

public abstract class GeoQuestListActivity extends ListActivity {

	private ProgressDialog downloadAndStartGameDialog;
	private ProgressDialog startLocalGameDialog;

	protected MenuMaker menuMaker = new MenuMaker();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((GeoQuestApp) getApplication()).addActivity(this);

		// Init progress dialogs:
		downloadAndStartGameDialog = new ProgressDialog(this);
		downloadAndStartGameDialog
				.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		downloadAndStartGameDialog.setCancelable(false);

		startLocalGameDialog = new ProgressDialog(this);
		startLocalGameDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		startLocalGameDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menuMaker.addMenuItems(MenuMaker.QUIT_MENU_ID,
				MenuMaker.IMPRINT_MENU_ID);
		menuMaker.addMenuItem(MenuMaker.PREFS_MENU_ID,
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						Intent settingsActivity = new Intent(getBaseContext(),
								Preferences.class);
						startActivity(settingsActivity);
						return true;
					}
				});
		menuMaker.setupMenu(menu);
		return true;
	}

	protected void onDestroy() {
		if (isFinishing()) {
			GeoQuestApp.getInstance().removeActivity(this);
		}
		super.onDestroy();
	}

	/*
	 * TODO move to GameListActivity class. Also downloadAndStartGameDialog and
	 * startLocalGameDialog.
	 */
	protected void startGame(final GameItem gameItem, final String repoName) {
		final boolean gameMustBeDownloaded = gameItem.isDownloadNeeded();
		final Handler downloadGameHandler;
		final Handler startGameHandler;

		if (gameMustBeDownloaded) {
			downloadAndStartGameDialog.setProgress(0);
			downloadAndStartGameDialog.setMessage(GeoQuestApp.getContext()
					.getText(R.string.start_downloadGame));
			showDialog(GeoQuestApp.DIALOG_ID_DOWNLOAD_GAME);
			downloadGameHandler = new GeoQuestProgressHandler(
					downloadAndStartGameDialog,
					GeoQuestProgressHandler.FOLLOWED_BY_OTHER_PROGRESS_DIALOG);
			startGameHandler = new GeoQuestProgressHandler(
					downloadAndStartGameDialog,
					GeoQuestProgressHandler.LAST_IN_CHAIN);
		} else {
			startLocalGameDialog.setProgress(0);
			startLocalGameDialog.setMessage(GeoQuestApp.getContext().getText(
					R.string.start_startGame));
			showDialog(GeoQuestApp.DIALOG_ID_START_GAME);
			downloadGameHandler = null; // unused in this case.
			startGameHandler = new GeoQuestProgressHandler(
					startLocalGameDialog, GeoQuestProgressHandler.LAST_IN_CHAIN);
		}

		final String gameFileName = gameItem.getFileName();
		final String gameName = gameItem.getName();
		GeoQuestApp.singleThreadExecutor.execute(new Runnable() {

			@SuppressWarnings("unchecked")
			public void run() {
				if (gameMustBeDownloaded) {
					// if selected game is only on server or newer there:
					// (re-)load it from server:
					GameLoader.downloadGame(downloadGameHandler, repoName,
							gameFileName);
				}
				GameLoader.startGame(startGameHandler,
						GeoQuestApp.getGameXMLFile(repoName, gameFileName));
				GeoQuestApp.setRecentGame(repoName, gameName, gameFileName);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case GeoQuestApp.DIALOG_ID_DOWNLOAD_GAME:
			return downloadAndStartGameDialog;
		case GeoQuestApp.DIALOG_ID_START_GAME:
			return startLocalGameDialog;
		default:
			return null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		GeoQuestApp.setCurrentActivity(this);
	}

}
