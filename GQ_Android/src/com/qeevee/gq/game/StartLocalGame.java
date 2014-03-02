package com.qeevee.gq.game;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import edu.bonn.mobilegaming.geoquest.GameSessionManager;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
import edu.bonn.mobilegaming.geoquest.Imprint;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;

public class StartLocalGame extends
		AsyncTask<GameDescription, Integer, Boolean> {

	private static final String TAG = StartLocalGame.class.getCanonicalName();
	private GameDescription game;
	private Class<? extends UIFactory> predefinedUIFactory = null;

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
		String gameName = Integer.valueOf(game.getID()).toString();
		File gameDir = new File(GameDataManager.getQuestsDir(), gameName);
		gameDir = GameDataManager.getQuestDir(gameName);

		File gameXMLFile = new File(gameDir, GameDataManager.GAME_XML_FILENAME);

		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(gameXMLFile);
			Mission.documentRoot = document.getRootElement();
			setGlobalMissionLayout();
			GeoQuestApp.setRunningGameDir(gameXMLFile.getParentFile());

			Mission firstMission = createMissions();
			createHotspots(Mission.documentRoot);

			// Only from now on we can access game ressources.

			GeoQuestApp.setImprint(new Imprint(Mission.documentRoot
					.element("imprint")));

			if (firstMission != null) {
				// TODO ReportingService muss jetzt schon gestartet sein!
				GameSessionManager.setSessionID(Mission.documentRoot
						.attributeValue("name"));
				firstMission.startMission();
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
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
	 */
	@SuppressWarnings("unchecked")
	static private void createHotspots(Element document)
			throws DocumentException {
		List<Element> list = document.selectNodes("//hotspot");

		for (Iterator<Element> i = list.iterator(); i.hasNext();) {
			Element hotspot = i.next();
			try {
				HotspotOld.create(null, hotspot);
			} catch (HotspotOld.IllegalHotspotNodeException exception) {
				Log.e(TAG, exception.toString());
			}
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		CharSequence toastText = null;
		if (!success) {
			toastText = "Error startimng game " + game.getName();
			Toast.makeText(GeoQuestApp.getContext(), toastText,
					Toast.LENGTH_SHORT).show();
		}
	}

}