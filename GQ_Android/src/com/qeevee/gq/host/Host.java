package com.qeevee.gq.host;

public class Host {

	public static final int GEOQUEST_PUBLIC_PORTAL_ID = 61;
	public static final String GQ_HOST_BASE_URL = "http://www.qeevee.org:9091";
	// public static final String GQ_HOST_BASE_URL =
	// "http://www.quest-mill.com:9000";
	public static final String GQ_HOST_GAMEPATH = "/game/download/";

	public static String getDownloadURL(String gameID) {
		return GQ_HOST_BASE_URL + GQ_HOST_GAMEPATH + gameID;
	}

	public static String getURLForPersonalGamesOnPortal(int portalID) {
		return GQ_HOST_BASE_URL + "/json/" + portalID + "/privategames";
	}

	public static String getURLForLoginOnPortal(int portalID) {
		return GQ_HOST_BASE_URL + "/" + portalID + "/login";
	}
}
