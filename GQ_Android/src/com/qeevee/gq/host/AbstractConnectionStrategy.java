package com.qeevee.gq.host;

public abstract class AbstractConnectionStrategy implements ConnectionStrategy {

	int portalID;

	public String getPortalID() {
		return Integer.valueOf(portalID).toString();
	}

}
