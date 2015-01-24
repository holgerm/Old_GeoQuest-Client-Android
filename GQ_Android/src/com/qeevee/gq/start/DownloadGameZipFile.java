package com.qeevee.gq.start;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.host.Host;
import com.qeevee.gqdefault.R;
import com.qeevee.util.FileOperations;

public class DownloadGameZipFile extends
		AsyncTask<GameDescription, Integer, File> {

	static final String TAG = DownloadGameZipFile.class.getCanonicalName();
	private final static int BYTE_SIZE = 1024;
	private GameDescription game;
	private GamesInCloud activity;
	private ProgressDialog progressDialog;

	public DownloadGameZipFile(GamesInCloud activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(activity);
	}

	protected File doInBackground(GameDescription... games) {
		this.game = games[0];
		game.getFileSize(); // just to ensure that filesize is loaded on non-ui
							// thread. TODO remove as soon as server JSON
							// contains filesize.

		// create game directory - if needed:
		String gameName = Integer.valueOf(game.getID()).toString();

		activity.runOnUiThread(initProgressBar4Download);

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
				return null;
			}
		}

		activity.runOnUiThread(initProgressBar4Unzip);

		if (!isCancelled() && gameZipFile != null) {
			GameDataManager.unzipGameArchive(gameZipFile);

			gameZipFile.delete();
		}

		return gameZipFile;
	}

	private File downloadZipFile(File gameDir, GameDescription game)
			throws IOException {
		File gameZipFile = new File(gameDir, "game.zip");
		InputStream in;
		FileOutputStream fOutLocal = null;
		fOutLocal = new FileOutputStream(gameZipFile);

		URL url = new URL(Host.getDownloadURL(game.getID()));

		Log.d(TAG, "starting download: " + url);
		in = new BufferedInputStream(url.openStream(), BYTE_SIZE);

		byte by[] = new byte[BYTE_SIZE];
		int readThisTime;
		int alreadyRead = 0;

		long fileSize = game.getFileSize();

		while ((readThisTime = in.read(by, 0, BYTE_SIZE)) != -1
				&& !isCancelled()) {
			fOutLocal.write(by, 0, readThisTime);
			alreadyRead += readThisTime;
			if (fileSize != 0) {
				int percent = (int) ((alreadyRead / (float) fileSize) * 10);

				publishProgress(percent);
			}
		}

		in.close();
		fOutLocal.close();
		Log.d(TAG, "completed download: " + url);
		return gameZipFile;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (values != null && values.length == 1 && progressDialog != null) {
			progressDialog.setProgress(values[0]);
		}
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
	protected void onPostExecute(File gameZip) {
		if (isCancelled()) {
			doCancel();
			return;
		}
		progressDialog.dismiss();
		reenableGamesInCloud();
		CharSequence toastText = null;
		if (gameZip != null) {
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
			activity.finish();
		} else {
			toastText = GeoQuestApp.getContext().getText(
					R.string.messageDownloadErrorPrefix)
					+ " " + game.getName();
			Toast.makeText(GeoQuestApp.getContext(), toastText,
					Toast.LENGTH_LONG).show();
		}
	}

	protected GameDescription getGame() {
		return game;
	}

	public void reenableGamesInCloud() {
		activity.reenable();
	}

	private void doCancel() {
		// Delete already loaded parts:
		File questsDir = GameDataManager.getQuestsDir();
		File deleteDir = new File(questsDir, DownloadGameZipFile.this.getGame()
				.getID());
		FileOperations.deleteDirectory(deleteDir);

		// reenable listview:
		reenableGamesInCloud();
	}

	private Runnable initProgressBar4Download = new Runnable() {
		public void run() {
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					DownloadGameZipFile.this.cancel(true);
					reenableGamesInCloud();
				}

			});

			progressDialog.setTitle(game.getName());
			progressDialog.setMessage(GeoQuestApp.getContext().getText(
					R.string.dialogMessageDownloading));
			progressDialog.setIcon(R.drawable.app_item_icon);

			if (game.getFileSize() != 0) {
				progressDialog.setIndeterminate(false);
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setProgress(0);
				progressDialog.setMax(100);
			} else {
				progressDialog.setIndeterminate(true);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			}

			progressDialog.show();
		}
	};

	private Runnable initProgressBar4Unzip = new Runnable() {
		public void run() {
			progressDialog.dismiss();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					DownloadGameZipFile.this.cancel(true);
					reenableGamesInCloud();
				}

			});

			progressDialog.setTitle(game.getName());
			progressDialog.setMessage(GeoQuestApp.getContext().getText(
					R.string.dialogMessageExtracting));
			progressDialog.setIcon(R.drawable.app_item_icon);

			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			progressDialog.show();
		}
	};

}
