package edu.bonn.mobilegaming.geoquest;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.uni.bonn.nfc4mg.NFCEventManager;

import edu.bonn.mobilegaming.geoquest.capability.NeedsNFCCapability;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManager;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;

public abstract class GeoQuestActivity extends Activity {

	private NFCEventManager mNFCEventManager = null;

	@Override
	protected void onResume() {
		super.onResume();
		attachNFCEventListener();
		GeoQuestApp.setCurrentActivity(this);
	}

	private void attachNFCEventListener() {
		if (this instanceof NeedsNFCCapability && null != mNFCEventManager) {
			mNFCEventManager.attachNFCListener(GeoQuestActivity.this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		detachNFCEventListener();
	}

	private void detachNFCEventListener() {
		if (this instanceof NeedsNFCCapability && null != mNFCEventManager) {
			mNFCEventManager.removeNFCListener(GeoQuestActivity.this);
		}
	}

	protected static final int MENU_ID_OFFSET = Menu.FIRST + 4;
	public static ContextManager contextManager = null;

	static final String TAG = "GeoQuestActivity";

	private ProgressDialog downloadAndStartGameDialog;
	private ProgressDialog startLocalGameDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GeoQuestApp.setCurrentActivity(this);

		checkAndInitializeNFCEventManager();

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

	private void checkAndInitializeNFCEventManager() {
		if (this instanceof NeedsNFCCapability) {
			try {
				mNFCEventManager = new NFCEventManager(this);
				mNFCEventManager.initialize(this, GeoQuestActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_basic, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_imprint:
			GeoQuestApp.getInstance().showImprint();
			return true;
		case R.id.menu_preferences:
			Intent settingsActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(settingsActivity);
			return true;
		case R.id.menu_info:
			String infotext = GeoQuestApp.getContext().getString(
					R.string.info_text)
					+ "" + GeoQuestApp.getContext().getString(R.string.version);
			GeoQuestApp.showMessage(infotext);
			return true;
		case R.id.menu_debugMode:
			item.setChecked(!item.isChecked());
			GeoQuestApp.getInstance().setDebugMode(item.isChecked());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startContextManager() {
		if (contextManager == null) {
			contextManager = new ContextManager(this.getApplicationContext());
		}

	}

	protected void onDestroy() {
		if (isFinishing()) {
			GeoQuestApp.getInstance().removeActivity(this);
		}
		super.onDestroy();
	}

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

}
