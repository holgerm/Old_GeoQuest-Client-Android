package com.qeevee.gq.game;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.qeevee.gq.loc.Hotspot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import edu.bonn.mobilegaming.geoquest.GameSessionManager;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Imprint;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;

public class LocalGames extends Activity {

	private static final String TAG = LocalGames.class.getCanonicalName();
	private ListView listView;
	private TextView titleView;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localgames);

		listView = (ListView) findViewById(R.id.listview);
		titleView = (TextView) findViewById(R.id.titleGamesList);
		titleView.setText(R.string.titleLocalGames);

		List<GameDescription> games = GameDataManager.getGameDescriptions();
		GameListAdapter listAdapter = new GameListAdapter(this,
				android.R.layout.simple_list_item_1, games);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				// new StartLocalGame().execute((GameDescription) parent
				// .getItemAtPosition(position));
				startGame((GameDescription) parent.getItemAtPosition(position));
			}

		});
	}

	private void startGame(GameDescription game) {
		File gameDir = GameDataManager.getQuestDir(game.getID());
		File gameXMLFile = new File(gameDir, GameDataManager.GAME_XML_FILENAME);
		GeoQuestApp.getInstance().clean();

		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(gameXMLFile);
			Mission.documentRoot = document.getRootElement();
			setGlobalMissionLayout();
			GeoQuestApp.setRunningGameDir(gameXMLFile.getParentFile());

			firstMission = createMissions();
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
				new Hotspot(null, hotspot);
			} catch (Hotspot.IllegalHotspotNodeException exception) {
				Log.e(TAG, exception.toString());
			}
		}
	}

}
