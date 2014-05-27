package com.qeevee.gq.start;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.qeevee.util.FileOperations;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class DownloadGame extends AsyncTask<GameDescription, Integer, Boolean> {

	static final String TAG = DownloadGame.class.getCanonicalName();
	private final static int BYTE_SIZE = 1024;
	private GameDescription game;
	private GamesInCloud callBackToReenable;
	private ProgressDialog progressDialog;

	public DownloadGame(GamesInCloud activity) {
		callBackToReenable = activity;
	}

	protected Boolean doInBackground(GameDescription... games) {
		this.game = games[0];

		// create game directory - if needed:
		String gameName = Integer.valueOf(game.getID()).toString();
		progressDialog.setTitle(game.getName()
				+ " "
				+ GeoQuestApp.getContext().getText(
						R.string.downloadDialogTitleSuffix));
		progressDialog.setMessage(GeoQuestApp.getContext().getText(
				R.string.downloadDialogMessage));
		progressDialog.setIcon(R.drawable.gqlogo_solo_trans); // TODO use game
																// icon instead.
		File gameDir = new File(GameDataManager.getQuestsDir(), gameName);
		if (gameDir.exists())
			deleteDir(gameDir);
		gameDir = GameDataManager.getQuestDir(gameName);

		File gameZipFile = null;
		if (!isCancelled()) {
			try {
				gameZipFile = downloadZipFile(gameDir, game);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
		}

		if (!isCancelled() && gameZipFile != null) {
			GameDataManager.unzipGameArchive(gameZipFile);

			gameZipFile.delete();
		}

		return true;
	}

	private File downloadZipFile(File gameDir, GameDescription game)
			throws IOException {
		File gameZipFile = new File(gameDir, "game.zip");
		InputStream in;
		FileOutputStream fOutLocal = null;
		fOutLocal = new FileOutputStream(gameZipFile);

		URL url = new URL(GeoQuestApp.getHostConnector().getDownloadURL(
				game.getID()));

		Log.d(TAG, "starting download: " + url);
		in = new BufferedInputStream(url.openStream(), BYTE_SIZE);
		int lenght = url.openConnection().getContentLength();

		byte by[] = new byte[BYTE_SIZE];
		int readThisTime;
		int alreadyRead = 0;

		while ((readThisTime = in.read(by, 0, BYTE_SIZE)) != -1) {
			fOutLocal.write(by, 0, readThisTime);
			alreadyRead += readThisTime;
			publishProgress((int) ((alreadyRead / (float) lenght) * 100));
		}

		in.close();
		fOutLocal.close();
		Log.d(TAG, "completed download: " + url);
		return gameZipFile;
	}

	private void deleteDir(File dir) {
		File[] files = dir.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDir(file);
				} else {
					file.delete();
				}
			}
		}
		dir.delete();
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (isCancelled()) {
			doCancel();
			return;
		}
		progressDialog.dismiss();
		reenableGamesInCloud();
		CharSequence toastText = null;
		if (success) {
			toastText = GeoQuestApp.getContext().getText(
					R.string.messageDownloadFinishedPrefix)
					+ " "
					+ game.getName()
					+ " "
					+ GeoQuestApp.getContext().getText(
							R.string.messageDownloadFinishedSuffix);

			// Start quest:
			AsyncTask<GameDescription, Integer, Boolean> startLocalGame = new StartLocalGame();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				startLocalGame.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, game);
			else
				startLocalGame.execute(game);
		} else {
			toastText = GeoQuestApp.getContext().getText(
					R.string.messageDownloadErrorPrefix)
					+ " " + game.getName();
			Toast.makeText(GeoQuestApp.getContext(), toastText,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// TODO: make cancellable
		progressDialog = ProgressDialog.show(callBackToReenable,
				"Downloading ...", "Please wait.", true, false,
				new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// cancel:
						DownloadGame.this.cancel(true);
					}

				});
	}

	protected GameDescription getGame() {
		return game;
	}

	public void reenableGamesInCloud() {
		callBackToReenable.reenable();
	}

	private void doCancel() {
		// Delete already loaded parts:
		File questsDir = GameDataManager.getQuestsDir();
		File deleteDir = new File(questsDir, DownloadGame.this.getGame()
				.getID());
		FileOperations.deleteDirectory(deleteDir);

		// reenable listview:
		reenableGamesInCloud();
	}
}
