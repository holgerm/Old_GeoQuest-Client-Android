package com.qeevee.gq.start;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GameDescription {

	private static final String TAG = GameDescription.class.getCanonicalName();
	private String name;
	private String id;
	private String zipURL = "none";
	private String portalID;

	public String getZipURL() {
		return zipURL;
	}

	public GameDescription(JSONObject jsonObject, String portalID)
			throws JSONException {
		name = (String) jsonObject.get("name");
		id = ((Integer) jsonObject.get("id")).toString();
		zipURL = (String) jsonObject.get("zip");
		this.setPortalID(portalID);
	}

	/**
	 * @param gameDir
	 *            the directory that is named after the game id. It contains
	 *            another directory called "game" which in turn contains the
	 *            game.xml and media data used in the game.
	 */
	public GameDescription(File gameDir) {
		id = gameDir.getName();
		File gameXML = new File(gameDir, GameDataManager.GAME_XML_FILENAME);
		name = extractNameFromXML(gameXML);
	}

	private String extractNameFromXML(File gameXML) {
		SAXReader xmlReader = new SAXReader();
		Document doc = null;
		try {
			doc = xmlReader.read(gameXML);
		} catch (DocumentException e) {
			Log.e(TAG, e.toString());
		}
		XPath xpathSelector = DocumentHelper.createXPath("//game");
		Element gameNode = (Element) xpathSelector.selectSingleNode(doc);
		return gameNode.attributeValue("name");
	}

	public String toString() {
		return name;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPortalID() {
		return portalID;
	}

	private void setPortalID(String portalID) {
		this.portalID = portalID;
	}

}
