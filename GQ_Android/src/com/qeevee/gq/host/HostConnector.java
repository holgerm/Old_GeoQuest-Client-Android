package com.qeevee.gq.host;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.qeevee.gq.game.GameDescription;

public class HostConnector {

	private static final String TAG = HostConnector.class.getCanonicalName();

	final static String GQ_HOST_BASE_URL = "http://www.qeevee.org:9091";

	private ConnectionStrategy connectionStrategy;

	public void setConnectionStrategy(ConnectionStrategy connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}

	public HostConnector() {
		setConnectionStrategy(new DefaultConnectionStrategy());
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
				gameList.add(new GameDescription(jsonArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return gameList;
	}
}
