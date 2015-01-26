package com.qeevee.gq.start;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.qeevee.gq.base.GameSessionManager;
import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.base.Imprint;
import com.qeevee.gq.base.Mission;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.ui.UIFactory;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class StartLocalGame extends
		AsyncTask<GameDescription, Integer, Boolean> {

	private static final String TAG = StartLocalGame.class.getCanonicalName();
	private GameDescription game;
	private Class<? extends UIFactory> predefinedUIFactory = null;
	private Mission firstMission;

	/**
	 * Used for test in order to set a test ui factory.
	 * 
	 * @param uiFactories
	 */
	public void setPredefinedUIFactories(
			Class<? extends UIFactory>... uiFactories) {
		predefinedUIFactory = uiFactories[0];
	}

	protected Boolean doInBackground(GameDescription... games) {
		this.game = games[0];
		File gameDir = GameDataManager.getQuestDir(game.getID());
		File gameXMLFile = new File(gameDir, GameDataManager.GAME_XML_FILENAME);
		GeoQuestApp.getInstance().clean();

		try {
			return readGameFromFile(gameXMLFile);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private Boolean readGameFromFile(File gameXMLFile) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(gameXMLFile);
		Mission.documentRoot = document.getRootElement();
		setGlobalMissionLayout();
		GeoQuestApp.setRunningGameDir(gameXMLFile.getParentFile());
		GeoQuestApp.setRunningGameID(Mission.documentRoot.attributeValue("id"));

		unzipFilesIfNeeded(new File(gameXMLFile.getParentFile(), "files"));

		firstMission = createMissions();
		createHotspots(Mission.documentRoot);

		// Only from now on we can access game ressources.

		GeoQuestApp.setImprint(new Imprint(Mission.documentRoot
				.element("imprint")));
		return true;
	}

	private void unzipFilesIfNeeded(File gameFilesDir) {
		String[] zipFileNames = gameFilesDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".zip");
			}
		});

		for (int i = 0; i < zipFileNames.length; i++) {
			File zipFile = new File(gameFilesDir, zipFileNames[i]);
			File targetDir = new File(gameFilesDir, zipFileNames[i].substring(
					0, zipFileNames[i].length() - ".zip".length()));
			targetDir.mkdir();
			GameDataManager.unzipFile(zipFile, targetDir);
			// TODO check since we changed the gamezipfile location

			zipFile.delete();
		}

	}

	private void setGlobalMissionLayout() {
		if (predefinedUIFactory == null)
			UIFactory.selectUIStyle(Mission.documentRoot
					.attributeValue("uistyle"));
		else
			UIFactory.setFactory(predefinedUIFactory);
	}

	private static Mission createMissions() {
		@SuppressWarnings("rawtypes")
		List missionNodes = Mission.documentRoot.selectNodes("child::mission");
		Mission firstMission = null;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = missionNodes.iterator(); iterator.hasNext();) {
			Element missionNode = (Element) iterator.next();
			String idOfMission = missionNode.attributeValue("id");
			Mission curMission = Mission.create(idOfMission, null, missionNode);
			if (firstMission == null) {
				firstMission = curMission;
			}
		}
		return firstMission;
	}

	/**
	 * Gets the Hotspots data from the XML file.
	 * 
	 * TODO move into a new HotspotFactory class.
	 */
	@SuppressWarnings("unchecked")
	static private void createHotspots(Element document)
			throws DocumentException {
		List<Element> list = document.selectNodes("//hotspot");

		for (Iterator<Element> i = list.iterator(); i.hasNext();) {
			Element hotspot = i.next();
			try {
				new Hotspot(hotspot);
			} catch (Hotspot.IllegalHotspotNodeException exception) {
				Log.e(TAG, exception.toString());
			}
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		CharSequence toastText = null;
		if (!success) {
			toastText = "Error starting game " + game.getName();
			Toast.makeText(GeoQuestApp.getContext(), toastText,
					Toast.LENGTH_SHORT).show();
		} else {
			if (firstMission != null) {
				// TODO ReportingService muss jetzt schon gestartet sein!
				GameSessionManager.setSessionID(Mission.documentRoot
						.attributeValue("name"));
				firstMission.startMission();
			}

		}
	}

}
