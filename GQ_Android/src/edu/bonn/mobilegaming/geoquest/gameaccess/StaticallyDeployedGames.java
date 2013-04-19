package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.res.AssetManager;
import android.util.Log;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class StaticallyDeployedGames {

	public static final String GAME_XML_FILE_NAME = "game.xml";
	public static final String REPOSITORIES_BASE_DIR = "repositories";
	private static final String TAG = StaticallyDeployedGames.class
			.getCanonicalName();
	private static AssetManager assetManager;

	private static void initAssetManager() {
		assetManager = GeoQuestApp.getContext().getAssets();
	}

	static Collection<? extends RepositoryItem> getRepositories() {
		initAssetManager();
		List<RepositoryItem> staticRepositories = new ArrayList<RepositoryItem>();
		try {
			String[] repoNames = assetManager.list(REPOSITORIES_BASE_DIR);
			for (int i = 0; i < repoNames.length; i++) {
				if (isRepository(repoNames[i]))
					staticRepositories.add(new StaticRepository(repoNames[i]));
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return staticRepositories;
	}

	private static boolean isRepository(String repoName) {
		initAssetManager();
		boolean isRepo = false;
		try {
			String[] questDirNames = assetManager.list(REPOSITORIES_BASE_DIR
					+ File.separator + repoName);
			for (int i = 0; i < questDirNames.length && !isRepo; i++) {
				isRepo |= isQuestDir(repoName, questDirNames[i]);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return isRepo;
	}

	private static boolean isQuestDir(String repoName, String questDirName) {
		initAssetManager();
		boolean isQuestDir = false;
		String[] questFiles;
		try {
			questFiles = assetManager
					.list(REPOSITORIES_BASE_DIR + File.separator + repoName
							+ File.separator + questDirName);
			for (int j = 0; j < questFiles.length && !isQuestDir; j++) {
				isQuestDir |= questFiles[j].equals(GAME_XML_FILE_NAME);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return isQuestDir;
	}

}
