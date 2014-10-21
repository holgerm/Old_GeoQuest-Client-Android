package com.qeevee.gq.host;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.qeevee.gq.start.GameDescription;

public class HostConnector {

	private static final String TAG = HostConnector.class.getCanonicalName();

	private ConnectionStrategy connectionStrategy;

	public void setConnectionStrategy(ConnectionStrategy connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}

	public ConnectionStrategy getConnectionStrategy() {
		return connectionStrategy;
	}

	public HostConnector() {
		setConnectionStrategy(new PublicGamesConnectionStrategy(
				Host.GEOQUEST_PUBLIC_PORTAL_ID));
	}

	public HostConnector(int portalID) {
		setConnectionStrategy(new PublicGamesConnectionStrategy(portalID));
	}

	public ArrayList<GameDescription> getGameList() {
		String gamesJSONString = connectionStrategy.getGamesJSONString();
		return createList(gamesJSONString);
	}

	private ArrayList<GameDescription> createList(String gamesJSONString) {
		ArrayList<GameDescription> gameList = new ArrayList<GameDescription>();
		if (gamesJSONString == null)
			return gameList;
		try {
			JSONArray jsonArray = new JSONArray(gamesJSONString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				if (!jsonObj.isNull("zip")
						&& !jsonObj.get("zip").equals("none"))
					gameList.add(new GameDescription(jsonObj,
							connectionStrategy.getPortalID()));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return gameList;
	}

	public String getDownloadURL(String id) {
		return connectionStrategy.getDownloadURL(id);
	}
}
