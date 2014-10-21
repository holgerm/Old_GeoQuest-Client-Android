package com.qeevee.gq.host;

import java.util.concurrent.ExecutionException;

import android.util.Log;

public class DefaultConnectionStrategy implements ConnectionStrategy {

	private int hostID;

	static final String TAG = DefaultConnectionStrategy.class
			.getCanonicalName();

	final static String GQ_HOST_BASE_URL = "http://www.qeevee.org:9091";

	private static final String GQ_HOST_GAMEPATH = "/game/download/";

	public DefaultConnectionStrategy(int portalID) {
		this.hostID = portalID;
	}

	public String getGamesJSONString() {
		try {
			String result = new RetrieveJSONforPublicGames().execute(this)
					.get();
			return result;
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public String getDownloadURL(String gameID) {
		return GQ_HOST_BASE_URL + GQ_HOST_GAMEPATH + gameID;
	}

	public String getPortalID() {
		return Integer.valueOf(hostID).toString();
	}
}
