package com.qeevee.gq.start;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.qeevee.gq.GeoQuestApp;

import android.content.Context;
import android.util.Log;

/**
 * This class gives access to the locally stored games.
 * 
 * @author muegge
 * 
 */
public class GameDataManager {

	public static final String QUEST_DIR_NAME = "quests";
	public static final String GAME_XML_FILENAME = "game.xml";
	static private Context ctx = GeoQuestApp.getContext();
	static private File questsDir = null;

	static public final GameDirFilter GAMEDIRFILTER = new GameDirFilter();
	private static final String TAG = GameDataManager.class.getCanonicalName();

	public static boolean existsLocalQuest(String questName) {
		File gameDir = new File(getQuestsDir(), questName);
		return (new GameDirFilter().accept(gameDir));
	}

	/**
	 * @param questName
	 * @return directory that contains the game.xml and game media.
	 */
	public static File getQuestDir(String questName) {
		return getOrCreateSubDir(getQuestsDir(), questName);
	}

	/**
	 * @return the app-private directory where quests are stored to and read
	 *         from. This data is the extracted zip files not data that is
	 *         created during the quest are played.
	 */
	public static File getQuestsDir() {
		if (questsDir != null) {
			return questsDir;
		}
		File dir = ctx.getExternalFilesDir(null);
		if (dir == null) {
			questsDir = ctx.getDir(QUEST_DIR_NAME, Context.MODE_PRIVATE);
		} else {
			questsDir = getOrCreateSubDir(dir, QUEST_DIR_NAME);
		}
		return questsDir;
	}

	private static File getOrCreateSubDir(File dir, String subDirName) {
		File qDir = new File(dir, subDirName);

		if (qDir.exists() && qDir.canWrite()) {
			return qDir;
		}

		Log.i(TAG, "1");
		boolean created = qDir.mkdirs();
		if (created) {
			Log.i(TAG, "2");
			return qDir;
		} else {
			Log.i(TAG, "3");
			return null;
		}

	}

	public static List<GameDescription> getGameDescriptions() {
		List<GameDescription> games = new ArrayList<GameDescription>();
		GameDirFilter gameDirFilter = new GameDirFilter();
		File questsDir = getQuestsDir();
		File[] gameDirs = questsDir.listFiles(gameDirFilter);
		if (gameDirs != null)
			for (int i = 0; i < gameDirs.length; i++) {
				games.add(new GameDescription(gameDirs[i]));
			}
		return games;
	}

	public static class GameDirFilter implements FileFilter {

		public boolean accept(File dir) {
			if (!dir.isDirectory())
				return false;
			File gameSpec = new File(dir, GAME_XML_FILENAME);
			if (gameSpec.exists() && gameSpec.isFile() && gameSpec.canRead())
				return true;
			return false;
		}
	}

	/**
	 * Unzips all files from the locally stored archive ({@code newGameZipFile}
	 * and stores it in a new directory named after the zip archive name at the
	 * same place.
	 * 
	 * @param gameZipFile
	 * @return the directory where the game files have been stored
	 */
	static void unzipGameArchive(File gameZipFile) {

		// TODO Publish progress
		String newGameDirName = gameZipFile.getParent();

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
			Log.d(DownloadGame.TAG, "ZipException creating zipfile from "
					+ gameZipFile);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(DownloadGame.TAG, "IOException creating zipfile from "
					+ gameZipFile);
			e.printStackTrace();
		}
	}

}
