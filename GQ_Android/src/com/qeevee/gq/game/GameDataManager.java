package com.qeevee.gq.game;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

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
			return ctx.getDir(QUEST_DIR_NAME, Context.MODE_PRIVATE);
		} else {
			return getOrCreateSubDir(dir, QUEST_DIR_NAME);
		}
	}

	private static File getOrCreateSubDir(File dir, String subDirName) {
		File qDir = new File(dir, subDirName);
		if (qDir.exists() && qDir.canWrite())
			return qDir;
		else {
			boolean created = qDir.mkdirs();
			if (!created)
				return null;
			else
				return qDir;
		}
	}

	public static List<GameDescription> getGameDescriptions() {
		List<GameDescription> games = new ArrayList<GameDescription>();
		GameDirFilter gameDirFilter = new GameDirFilter();
		File questsDir = getQuestsDir();
		File[] gameDirs = questsDir.listFiles(gameDirFilter);
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

}
