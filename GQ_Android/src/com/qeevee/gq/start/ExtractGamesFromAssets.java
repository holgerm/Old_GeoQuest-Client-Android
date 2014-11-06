package com.qeevee.gq.start;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.qeevee.gq.base.GeoQuestApp;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

public class ExtractGamesFromAssets extends AsyncTask<Void, Integer, Void> {

	private static final String TAG = ExtractGamesFromAssets.class
			.getCanonicalName();
	private final static int BYTE_SIZE = 1024;
	private static final String ASSET_DIR_FOR_INCLUDED_QUESTS = "included";
	private AssetManager assetManager;

	protected Void doInBackground(Void... nothing) {
		assetManager = GeoQuestApp.getContext().getAssets();
		String[] assetRelPaths = null;
		try {
			assetRelPaths = assetManager.list(ASSET_DIR_FOR_INCLUDED_QUESTS);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		for (int i = 0; i < assetRelPaths.length; i++) {
			extractGame(assetRelPaths[i]);
		}
		return null;
	}

	private void extractGame(String assetRelPath) {
		InputStream is;
		try {
			String gameName = assetRelPath.substring(
					assetRelPath.lastIndexOf(File.separator) + 1,
					assetRelPath.lastIndexOf(".zip"));
			// create game directory - if needed - otherwise do not extract:
			File gameDir = new File(GameDataManager.getQuestsDir(), gameName);
			if (gameDir.exists()) {
				Log.d(TAG,
						"Game "
								+ gameName
								+ " already found on device. Same game in assets ignored.");
				return;
			}
			gameDir = GameDataManager.getQuestDir(gameName);

			is = new BufferedInputStream(assetManager.open(
					ASSET_DIR_FOR_INCLUDED_QUESTS + File.separator
							+ assetRelPath, AssetManager.ACCESS_BUFFER),
					BYTE_SIZE);
			File newGameZipFile = new File(gameDir, gameName + ".zip");
			FileOutputStream fOutLocal = new FileOutputStream(newGameZipFile);

			// TODO: care about lenght == -1, i.e. if info not
			// available,
			// send
			// other msg to handler.
			// Message msg = handler.obtainMessage();
			// msg.what =
			// GeoQuestProgressHandler.MSG_TELL_MAX_AND_TITLE;
			// msg.arg1 = lenght / BYTE_SIZE;
			// msg.arg2 = R.string.start_downloadGame;
			// handler.sendMessage(msg);

			byte by[] = new byte[BYTE_SIZE];
			int c;

			try {
				is.available();
			} catch (IOException e) {
				fOutLocal.close();
				Log.w(TAG, "could not read from assets file)");
				return;

			}

			while ((c = is.read(by, 0, BYTE_SIZE)) != -1) {
				// TODO check access to SDCard!
				fOutLocal.write(by, 0, c);
				// trigger progress bar to proceed:
				// handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_PROGRESS);
			}

			is.close();
			fOutLocal.close();
			GameDataManager.unzipGameArchive(newGameZipFile);

			Log.d(TAG, "completed extraction: " + gameName);
			// handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_FINISHED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
