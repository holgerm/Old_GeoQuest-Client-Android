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

	private static final String GAME_XML_FILE_NAME = "game.xml";
	private static final String REPOSITORIES_BASE_DIR = "repositories";
	private static final String TAG = StaticallyDeployedGames.class
			.getCanonicalName();

	static Collection<? extends RepositoryItem> getRepositories() {
		List<RepositoryItem> staticRepositories = new ArrayList<RepositoryItem>();
		AssetManager assetManager = GeoQuestApp.getContext().getAssets();
		try {
			String[] repoNames = assetManager.list(REPOSITORIES_BASE_DIR);
			for (int i = 0; i < repoNames.length; i++) {
				if (isRepository(repoNames[i]))
					staticRepositories.add(new RepositoryItem(repoNames[i]));
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return staticRepositories;
	}

	private static boolean isRepository(String repoName) {
		boolean isRepo = false;
		AssetManager assetManager = GeoQuestApp.getContext().getAssets();
		try {
			String[] questDirNames = assetManager.list(REPOSITORIES_BASE_DIR
					+ File.separator + repoName);
			for (int i = 0; i < questDirNames.length; i++) {
				String[] questFiles = assetManager.list(REPOSITORIES_BASE_DIR
						+ File.separator + repoName + File.separator
						+ questDirNames[i]);
				for (int j = 0; j < questFiles.length && !isRepo; j++) {
					isRepo |= questFiles[j].equals(GAME_XML_FILE_NAME);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return isRepo;
	}

}
