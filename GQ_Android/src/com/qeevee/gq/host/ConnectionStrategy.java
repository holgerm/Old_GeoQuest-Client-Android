package com.qeevee.gq.host;

public interface ConnectionStrategy {

	String getGamesJSONString();

	public abstract String getDownloadURL(String id);

	public String getPortalID();

}
