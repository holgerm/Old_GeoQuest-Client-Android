package com.qeevee.gq.host;

public abstract class AbstractConnectionStrategy implements ConnectionStrategy {

	protected int portalID;

	public String getDownloadURL(String gameID) {
		return Host.GQ_HOST_BASE_URL + Host.GQ_HOST_GAMEPATH + gameID;
	}

	public String getPortalID() {
		return Integer.valueOf(portalID).toString();
	}

}
