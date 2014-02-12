package com.qeevee.gq.game;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.AsyncTask;
import android.util.Log;


import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class DownloadGame extends AsyncTask<GameDescription, Integer, Void> {

	private static final String TAG = DownloadGame.class.getCanonicalName();
	private final static int BYTE_SIZE = 1024;

	protected Void doInBackground(GameDescription... games) {
		// create game directory - if needed:
		String gameName = games[0].getName();
		File gameDir = new File(GameDataManager.getQuestsDir(), gameName);
		if (gameDir.exists())
			deleteDir(gameDir);
		gameDir = GameDataManager.getQuestDir(gameName);

		File gameZipFile;
		try {
			gameZipFile = downloadZipFile(gameDir, games[0]);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}

		unzipGameArchive(gameZipFile);

		// TODO delete local zipfile

		return null;
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

	/**
	 * Unzips all files from the locally stored archive ({@code newGameZipFile}
	 * and stores it in a new directory named after the zip archive name at the
	 * same place.
	 * 
	 * @param gameZipFile
	 * @return the directory where the game files have been stored
	 */
	void unzipGameArchive(File gameZipFile) {
		String newGameDirName = gameZipFile.getAbsolutePath().replace(".zip",
				"");
		File newGameDir = new File(newGameDirName);
		if (!newGameDir.exists() || newGameDir.isFile()) {
			if (newGameDir.isFile())
				// just in the awkward case that there is a file with the same
				// name ...
				newGameDir.delete();
			newGameDir.mkdir();
		} else {
			// clean directory:
			deleteDir(newGameDir);
		}

		try {
			ZipFile zipFile = new ZipFile(gameZipFile);
			ZipEntry zipEntry;
			File entryFile;
			FileOutputStream fos;
			InputStream entryStream;

			for (Enumeration<? extends ZipEntry> enumeration = zipFile
					.entries(); enumeration.hasMoreElements();) {
				zipEntry = enumeration.nextElement();

				// skip files starting with ".":
				String zipEntryName = zipEntry.getName();
				String[] zipEntryNameParts = zipEntryName.split("/");
				if (zipEntryNameParts[zipEntryNameParts.length - 1]
						.startsWith("."))
					continue;

				entryFile = new File(newGameDirName + "/" + zipEntry.getName());

				// in case the entry is a directory:
				if (zipEntryName.endsWith("/")) {
					if (!entryFile.exists() || !entryFile.isDirectory())
						entryFile.mkdir();
					continue; // now it exists that's enough for directories ...
				}

				File parentDir = entryFile.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdir();
				}

				fos = new FileOutputStream(entryFile);
				entryStream = zipFile.getInputStream(zipEntry);
				byte content[] = new byte[1024];
				int bytesRead;

				do {
					bytesRead = entryStream.read(content);
					if (bytesRead > 0)
						fos.write(content, 0, bytesRead);
				} while (bytesRead > 0);

				fos.flush();
				fos.close();
			}
		} catch (ZipException e) {
			Log.d(TAG, "ZipException creating zipfile from " + gameZipFile);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG, "IOException creating zipfile from " + gameZipFile);
			e.printStackTrace();
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
}
