package com.qeevee.gq.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.qeevee.gq.Configuration;
import com.qeevee.gq.start.GameDescription;

public class HostConnector {

	private static final String TAG = HostConnector.class.getCanonicalName();

	public static Collection<GameDescription> getGamesList() {
		List<ConnectionStrategy> connStrats = new ArrayList<ConnectionStrategy>();
		int[] hostIDs = Configuration.getPortalIDs();
		if (hostIDs.length == 0) {
			connStrats.add(new PublicGamesConnectionStrategy(
					Host.GEOQUEST_PUBLIC_PORTAL_ID));
			connStrats.add(new PersonalGamesConnectionStrategy(
					Host.GEOQUEST_PUBLIC_PORTAL_ID));
		}
		for (int i = 0; i < hostIDs.length; i++) {
			connStrats.add(new PublicGamesConnectionStrategy(hostIDs[i]));
			connStrats.add(new PersonalGamesConnectionStrategy(hostIDs[i]));
		}
		Map<String, GameDescription> gameMap = getGamesMap(connStrats);
		return gameMap.values();
	}

	private static Map<String, GameDescription> getGamesMap(
			List<ConnectionStrategy> connectionStrategies) {
		Map<String, GameDescription> gameMap = new HashMap<String, GameDescription>();
		for (ConnectionStrategy curConnStrat : connectionStrategies) {
			List<GameDescription> curGameDescList = createList(curConnStrat
					.getGamesJSONString());
			for (GameDescription curGameDesc : curGameDescList) {
				if (!gameMap.containsKey(curGameDesc.getID())) {
					gameMap.put(curGameDesc.getID(), curGameDesc);
				}
			}
		}
		return gameMap;
	}

	private static ArrayList<GameDescription> createList(String gamesJSONString) {
		ArrayList<GameDescription> gameList = new ArrayList<GameDescription>();
		if (gamesJSONString == null)
			return gameList;
		try {
			JSONArray jsonArray = new JSONArray(gamesJSONString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				if (!jsonObj.isNull("zip")
						&& !jsonObj.get("zip").equals("none"))
					gameList.add(new GameDescription(jsonObj));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return gameList;
	}

}
