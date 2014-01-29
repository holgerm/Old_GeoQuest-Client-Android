package com.qeevee.gq.game;

import org.json.JSONException;
import org.json.JSONObject;

public class GameDescription {

	private String name;
	private int ID;
	private String zipURL;

	public String getZipURL() {
		return zipURL;
	}

	public GameDescription(JSONObject jsonObject) throws JSONException {
		name = (String) jsonObject.get("name");
		ID = (Integer) jsonObject.get("id");
		zipURL = (String) jsonObject.get("zip");
	}

	public String toString() {
		return name;
	}

	public int getID() {
		return ID;
	}

}
