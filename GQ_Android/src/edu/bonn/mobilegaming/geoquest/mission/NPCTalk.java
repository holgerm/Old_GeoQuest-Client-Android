package edu.bonn.mobilegaming.geoquest.mission;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.os.Bundle;

import com.qeevee.gq.history.Actor;
import com.qeevee.gq.history.TextItem;
import com.qeevee.gq.history.TransitionItem;
import com.qeevee.util.StringTools;

import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.UIFactory;

/**
 * Just a talking NPC. The NPC has a Image and text is based on dialogItems. The
 * text scrolls down on the screen and after each dialogItem the player have to
 * press a button to proceed.
 * 
 * @author Holger Muegge
 * @author Folker Hoffmann
 * @author Krischan Udelhoven
 */

public class NPCTalk extends MissionActivity {
	@SuppressWarnings("unused")
	private static final String TAG = "NPCTalk";

	private Iterator<Element> dialogItemIterator;

	private NPCTalkUI ui;

	private int nrOfDialogItems;

	private int indexOfCurrentDialogItem;

	/**
	 * Called by the android framework when the mission is created. Setups the
	 * View and calls the readXML method to get the dialogItems. The dialog
	 * starts with the first dialogItem.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Element> dialogItemList = mission.xmlMissionNode
				.selectNodes("./dialogitem");
		dialogItemIterator = dialogItemList.iterator();
		nrOfDialogItems = dialogItemList.size();
		indexOfCurrentDialogItem = 0;
		ui = UIFactory.getInstance().createUI(this);
		ui.init();
	}

	public void finishMission() {
		new TransitionItem(this);
		if (hasMoreDialogItems())
			super.finish(Globals.STATUS_FAIL);
		else
			super.finish(Globals.STATUS_SUCCEEDED);
	}

	/**
	 * @return true if this mission still has at least one more dialogs item to
	 *         show.
	 */
	public boolean hasMoreDialogItems() {
		return dialogItemIterator.hasNext();
	}

	/**
	 * @return the index of the last dialog item returned on a call to
	 *         {@link #getNextDialogItem()}. Index starts with 1 and will not
	 *         exceed the number of items - even if you call
	 *         {@link #getNextDialogItem()} too often.
	 */
	public int getIndexOfCurrentDialogItem() {
		return indexOfCurrentDialogItem;
	}

	public int getNumberOfDialogItems() {
		return nrOfDialogItems;
	}

	public DialogItem getNextDialogItem() {
		DialogItem result = new DialogItem(dialogItemIterator.next());
		if (result != null)
			indexOfCurrentDialogItem++;
		return result;
	}

	public void hasShownDialogItem(DialogItem shownDialogItem) {
		/*
		 * Store history item. TODO: add more arguments for image, audio and
		 * thumbnail.
		 */
		new TextItem(shownDialogItem.getText(), this, Actor.NPC);

	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO stop letting MissionOrToolActivity implement
		// BlockableAndReleasable!

	}

	/**
	 * Dialog Items are needed by the NPC talk mission. A dialog item is for
	 * example a sentence the NPC will say.
	 * 
	 * @author Folker Hoffmann
	 * @author Krischan Udelhoven
	 */
	public static class DialogItem {

		/** name of the speaker */
		private String speaker;
		/** DialogItems text */
		private String text;
		/** Array of words */
		private String[] textElements;
		/** used for iterating through the textElements */
		private int counter = 0;
		private CharSequence nextDialogButtonText = null;

		public CharSequence getNextDialogButtonText() {
			return nextDialogButtonText;
		}

		private String audioFilePath;
		public boolean blocking;

		private Element xml = null;

		/**
		 * Constructor gets the information from the passed xmlElement
		 * 
		 * @param xmlElement
		 *            the dialogItem element from the XML file
		 */
		public DialogItem(Element xmlElement) {
			xml = xmlElement;
			speaker = xml.attributeValue("speaker");

			// read nextdialogbuttontext:
			Attribute a = (Attribute) xml
					.selectSingleNode("@nextdialogbuttontext");
			if (a != null) {
				nextDialogButtonText = a.getText();
			}

			// read sound (might be an audiofile for listenig to the text):
			a = (Attribute) xml.selectSingleNode("@sound");
			if (a != null) {
				audioFilePath = a.getText();
			}

			// read blocking attribute:
			a = (Attribute) xml.selectSingleNode("@blocking");
			if (a != null && a.getText().equals("false"))
				blocking = false;
			else
				blocking = true;

			text = xml.getText().replaceAll("\\s+", " ").trim();
			text = StringTools.replaceVariables(text);
			if (text.startsWith(" ")) {
				text = text.substring(1);
			}

			textElements = text.split("\\s+"); // Split anhand der Whitespaces
		}

		/**
		 * @return the speaker's name or <code>null</code> if no speaker given.
		 */
		public String getSpeaker() {
			return speaker;
		}

		public String getText() {
			return text;
		}

		public String getAudioFilePath() {
			return audioFilePath;
		}

		/**
		 * @return returns the number of words
		 */
		public int getNumParts() {
			return textElements.length;
		}

		public boolean hasNextPart() {
			return textElements.length > counter;
		}

		/**
		 * @return returns each word as a CharSquence or null of there is no
		 *         word left.
		 */
		public CharSequence getNextPart() {
			if (counter >= textElements.length)
				return null;
			else
				return textElements[counter++];
		}
	}

	public MissionOrToolUI getUI() {
		return ui;
	}
}
