package edu.bonn.mobilegaming.geoquest.jsinterface;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

import android.util.Log;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk.DialogItem;
import edu.bonn.mobilegaming.geoquest.mission.WebTech;

/**
 * This interface is the connection between the JS-File for the NPCTalk and
 * GeoQuest.
 * 
 * In the JS-File it gives the possibility to access XML values and to pass
 * values back to GeoQuest.
 */
public class NPCTalkJSInterface {

	private Mission mission;
	private File imgDir;
	private WebTech webTechView;

	private LinkedList<DialogItem> dialogItems = new LinkedList<DialogItem>();
	private String[] speakers;
	private String[] dialogs;
	private String[] buttons;

	private final String LOG_TAG = "NPCTalkJSInterface";

	public NPCTalkJSInterface(WebTech webTechView, Mission mission, File imgDir) {
		this.mission = mission;
		this.imgDir = imgDir;
		this.webTechView = webTechView;
	}

	/**
	 * Function that is called by the JS-File, when the mission is finished.
	 */
	public void endMission() {
		webTechView.endWebMission(Globals.STATUS_SUCCEEDED);
	}

	/**
	 * Determines the absolute path of the image.
	 * 
	 * @return absolute path to image
	 */
	public String getNPCImgUrl() {
		String imgUrl = mission.xmlMissionNode.attributeValue("charimage");
		if (imgUrl.startsWith("drawable")) {
			imgUrl = imgUrl.substring(8);
		}
		return "file://" + imgDir.getAbsolutePath() + imgUrl;
	}

	// TODO -- Sabine -- WebTech �bergabe der Dialoge an JS �ndern. Diese
	// L�sung
	// ist schlecht.
	/**
	 * Reads all dialogs for the Mission out of the XML file and saves the
	 * speakers, buttons and texts in arrays.
	 */
	@SuppressWarnings("unchecked")
	public void readDialogs() {

		// Load Dialog Items:
		List<Element> dialogItemList = mission.xmlMissionNode
				.selectNodes("./dialogitem");
		for (Iterator<Element> e = dialogItemList.iterator(); e.hasNext();) {
			dialogItems.addLast(new NPCTalk.DialogItem(e.next()));
		}

		speakers = new String[dialogItems.size()];
		dialogs = new String[dialogItems.size()];
		buttons = new String[dialogItems.size()];

		Iterator<DialogItem> it = dialogItems.iterator();
		int i = 0;

		while (it.hasNext()) {
			DialogItem item = it.next();

			speakers[i] = item.getSpeaker();
			dialogs[i] = item.getText();

			if (it.hasNext()) { // Nextbutton
				if (item.getNextDialogButtonText() != null) {
					buttons[i] = (item.getNextDialogButtonText()).toString();
				} else {
					buttons[i] = mission.xmlMissionNode
							.attributeValue("nextdialogbuttontext");
					if (buttons[i] == null) {
						buttons[i] = webTechView
								.getResourceString(R.string.button_text_proceed);
					}
				}
			} else { // Endbutton
				buttons[i] = mission.xmlMissionNode
						.attributeValue("endbuttontext");
				if (buttons[i] == null) {
					buttons[i] = webTechView
							.getResourceString(R.string.default_endButtonText);
				}
			}
			++i;
		}
	}

	/**
	 * Returns the number of dialogs that the mission contains.
	 * 
	 * @return number of dialogs
	 */
	public int getDialogNumb() {
		return dialogItems.size();
	}

	/**
	 * Returns the text of the i-th dialog or NULL if it doesn't exist.
	 * 
	 * @param i
	 *            i-th dialog
	 * @return Text
	 */
	public String getDialog(int i) {
		if (i < dialogs.length) {
			return dialogs[i];
		}
		return null;
	}

	/**
	 * Returns the speaker of the i-th dialog or NULL if it doesn't exist.
	 * 
	 * @param i
	 *            i-th dialog
	 * @return Speaker
	 */
	public String getSpeaker(int i) {
		if (i < speakers.length) {
			return speakers[i];
		}
		return null;
	}

	/**
	 * Returns the button-text of the i-th dialog or NULL if it doesn't exist.
	 * 
	 * @param i
	 *            i-th dialog
	 * @return Button text
	 */
	public String getButtonText(int i) {
		if (i < buttons.length) {
			return buttons[i];
		}
		return null;
	}

	/**
	 * Prints a debug log message on LogCat. Can be used for logging in
	 * JS-Files.
	 * 
	 * @param str
	 *            debug message printed on LogCat
	 */
	public void logDebug(String str) {
		Log.d(LOG_TAG, str);
	}

	/**
	 * Prints an error log message on LogCat. Can be used for logging in
	 * JS-Files.
	 * 
	 * @param str
	 *            error message printed on LogCat
	 */
	public void logError(String str) {
		Log.e(LOG_TAG, str);
	}

	/**
	 * Prints an info log message on LogCat. Can be used for logging in
	 * JS-Files.
	 * 
	 * @param str
	 *            info message printed on LogCat
	 */
	public void logInfo(String str) {
		Log.i(LOG_TAG, str);
	}

}
