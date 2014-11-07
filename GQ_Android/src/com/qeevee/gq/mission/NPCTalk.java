package com.qeevee.gq.mission;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.os.Bundle;
import android.util.Log;

import com.qeevee.gqphka.R;
import com.qeevee.gq.base.Globals;
import com.qeevee.gq.history.Actor;
import com.qeevee.gq.history.TextItem;
import com.qeevee.gq.history.TransitionItem;
import com.qeevee.gq.ui.UIFactory;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.ui.abstrakt.NPCTalkUI;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.util.StringTools;

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
	private static final String TAG = "NPCTalk";

	private NPCTalkUI ui;
	List<DialogItem> dialogItems;
	private int nrOfDialogItems;
	private int indexOfNextDialogItem;
	private CharSequence nextDialogButtonTextDefault;

	/**
	 * Called by the android framework when the mission is created. Setups the
	 * View and calls the readXML method to get the dialogItems. The dialog
	 * starts with the first dialogItem.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mission == null || mission.xmlMissionNode == null) {
			Log.e(TAG,
					"Mission null or MissionXML missing for an NPCTalk mission.");
			return;
		}

		dialogItems = new ArrayList<DialogItem>();
		// take only non-empty dialogItems:
		List<Element> dialogItemElementList = mission.xmlMissionNode
				.selectNodes("./dialogitem[text()!=\"\"]");
		if (dialogItemElementList == null || dialogItemElementList.size() == 0) {
			CharSequence text = XMLUtilities.getStringAttribute("text",
					XMLUtilities.OPTIONAL_ATTRIBUTE, mission.xmlMissionNode);
			CharSequence soundFile = XMLUtilities.getStringAttribute("sound",
					XMLUtilities.OPTIONAL_ATTRIBUTE, mission.xmlMissionNode);
			if (text != null)
				dialogItems.add(new DialogItem(text, soundFile));
		} else
			for (Element dialogItemElement : dialogItemElementList) {
				dialogItems.add(new DialogItem(dialogItemElement));
			}
		nrOfDialogItems = dialogItems.size();
		indexOfNextDialogItem = 0;
		setNextDialogButtonText(getMissionAttribute("nextdialogbuttontext",
				R.string.button_text_next));
		ui = UIFactory.getInstance().createUI(this);
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
		return indexOfNextDialogItem < nrOfDialogItems;
	}

	/**
	 * @return the index of the last dialog item returned on a call to
	 *         {@link #getNextDialogItem()}. Index starts with 1 and will not
	 *         exceed the number of items - even if you call
	 *         {@link #getNextDialogItem()} too often.
	 */
	public int getIndexOfCurrentDialogItem() {
		return indexOfNextDialogItem;
	}

	public int getNumberOfDialogItems() {
		return nrOfDialogItems;
	}

	public DialogItem getNextDialogItem() {
		if (indexOfNextDialogItem >= nrOfDialogItems) {
			throw new IndexOutOfBoundsException(
					"No more Dialog Items in NPCTalk page (id:" + this.id + ")");
		}
		return dialogItems.get(indexOfNextDialogItem++);
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

		// private Element xml = null;

		/**
		 * Constructor gets the information from the passed xmlElement
		 * 
		 * @param xmlElement
		 *            the dialogItem element from the XML file
		 */
		public DialogItem(Element xmlElement) {
			Element xml = xmlElement;
			speaker = xml.attributeValue("speaker");

			// read nextdialogbuttontext:
			nextDialogButtonText = XMLUtilities.getStringAttribute(
					"nextdialogbuttontext", XMLUtilities.OPTIONAL_ATTRIBUTE,
					xmlElement);

			// read sound (might be an audiofile for listenig to the text):
			Attribute a = (Attribute) xml.selectSingleNode("@sound");
			if (a != null) {
				audioFilePath = a.getText();
			}

			// read blocking attribute:
			a = (Attribute) xml.selectSingleNode("@blocking");
			if (a != null && a.getText().equals("false"))
				blocking = false;
			else
				blocking = true;

			text = XMLUtilities.getXMLContent(xml).replaceAll("\\s+", " ")
					.trim();
			processText();
		}

		private void processText() {
			text = StringTools.replaceVariables(text);
			if (text.startsWith(" ")) {
				text = text.substring(1);
			}

			textElements = text.split("\\s+"); // Split anhand der Whitespaces
		}

		/**
		 * Alternative constructor for TextWithImage variant, which does not
		 * have dialogItem elemnts but only text and sound attribute in the
		 * mission tag.
		 * 
		 * @param text
		 * @param soundFile
		 */
		public DialogItem(CharSequence text, CharSequence soundFile) {
			speaker = null;
			if (soundFile != null)
				audioFilePath = soundFile.toString();
			if (text != null)
				this.text = text.toString();
			else
				this.text = "";
			processText();
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
		public int getNumberOfTextTokens() {
			return textElements.length;
		}

		public boolean hasNextPart() {
			return textElements.length > counter;
		}

		/**
		 * @return returns each word as a CharSquence or null of there is no
		 *         word left.
		 */
		public CharSequence getNextTextToken() {
			if (counter >= textElements.length)
				return null;
			else
				return textElements[counter++];
		}
	}

	public MissionOrToolUI getUI() {
		return ui;
	}

	public CharSequence getNextDialogButtonTextDefault() {
		return nextDialogButtonTextDefault;
	}

	private void setNextDialogButtonText(
			CharSequence nextDialogButtonTextDefault) {
		this.nextDialogButtonTextDefault = nextDialogButtonTextDefault;
	}

}
