package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import android.content.res.AssetManager;
import android.util.Log;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class StaticRepository extends RepositoryItem {
	protected boolean initialized = false;

	public StaticRepository(String repoName) {
		super(repoName);
		this.setOnClient();
	}

	public List<GameItem> getGames() {
		if (!initialized) {
			initializeGames();
		}

		return games;
	}

	protected void initializeGames() {
		games = new ArrayList<GameItem>();
		AssetManager assetManager = GeoQuestApp.getContext().getAssets();
		try {
			String[] questDirNames = assetManager
					.list(StaticallyDeployedGames.REPOSITORIES_BASE_DIR
							+ File.separator + getName());
			for (int i = 0; i < questDirNames.length; i++) {
				// get Game File:
				InputStream afd = assetManager
						.open(StaticallyDeployedGames.REPOSITORIES_BASE_DIR
								+ File.separator + getName() + File.separator
								+ questDirNames[i] + File.separator
								+ StaticallyDeployedGames.GAME_XML_FILE_NAME);
				SAXReader xmlReader = new SAXReader();
				Document questDoc = null;
				try {
					questDoc = xmlReader.read(afd);
				} catch (DocumentException e) {
					Log.e(StaticRepository.class.getCanonicalName(),
							e.getMessage());
					e.printStackTrace();
				}
				// Retrieve data from game file, like name, location, icon,
				// author, etc.)
				XPath xpathSelector = DocumentHelper.createXPath("//game");
				Element gameNode = (Element) xpathSelector
						.selectSingleNode(questDoc);
				games.add(GameItem.createFromGameFileGameNode(gameNode, 0,
						questDirNames[i], this));
			}
		} catch (IOException e) {
			Log.e(StaticRepository.class.getCanonicalName(), e.getMessage());
			e.printStackTrace();
		}
		initialized = true;
	}

}
