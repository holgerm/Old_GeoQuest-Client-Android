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

import com.qeevee.gq.host.Host;
import com.qeevee.util.IO;

import android.util.Log;

public class GameDescription {

	private static final String TAG = GameDescription.class.getCanonicalName();
	private String name;
	private String id;
	private String zipURL = "none";

	private long filesize = FILESIZE_NOT_YET_RETRIEVED;
	private static long FILESIZE_NOT_YET_RETRIEVED = -1;

	// private long lastUpdate;

	public String getZipURL() {
		return zipURL;
	}

	public GameDescription(JSONObject jsonObject) throws JSONException {
		name = (String) jsonObject.get("name");
		id = ((Integer) jsonObject.get("id")).toString();
		// lastUpdate = Long.parseLong((String) jsonObject.get("lastUpdate"));
		zipURL = (String) jsonObject.get("zip");
		// filesize = getFileSize(id);
	}

	/**
	 * @param gameDir
	 *            the directory that is named after the game id. It contains
	 *            another directory called "game" which in turn contains the
	 *            game.xml and media data used in the game.
	 */
	public GameDescription(File gameDir) {
		id = gameDir.getName();
		// TODO get lastUpdate as stored in persistent storage???
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

	/**
	 * TODO should be replaced with attribute in JSON string as soon as the
	 * server can do that.
	 */
	public long getFileSize() {
		if (this.filesize == FILESIZE_NOT_YET_RETRIEVED) {
			String filesizeAsString = IO.getHTTPResult(Host.GQ_HOST_BASE_URL
					+ "/game/filesize/" + getID());
			filesizeAsString.trim();
			if (filesizeAsString.endsWith("\n"))
				filesizeAsString = filesizeAsString.substring(0,
						filesizeAsString.length() - 2);
			try {
				this.filesize = Long.valueOf(filesizeAsString);
			} catch (NumberFormatException exc) {
				this.filesize = 0;
			}
		}
		return this.filesize;
	}

}
