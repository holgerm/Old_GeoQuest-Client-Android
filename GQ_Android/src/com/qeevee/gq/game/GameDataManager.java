package com.qeevee.gq.game;

import java.io.File;

import android.content.Context;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class GameDataManager {

	private static final String QUEST_DIR_NAME = "quests";
	static private Context ctx = GeoQuestApp.getContext();
	static private File questsDir = null;

	public static File getQuestDir(String questName) {
		return getOrCreateSubDir(getQuestsDir(), questName);

	}

	/**
	 * @return the app-private directory where quests are stored to and read
	 *         from. This data is the extracted zip files not data that is
	 *         created during the quest are played.
	 */
	public static File getQuestsDir() {
		if (questsDir == null) {
			questsDir = getQDir();
		}
		return questsDir;
	}

	private static File getQDir() {
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

}
