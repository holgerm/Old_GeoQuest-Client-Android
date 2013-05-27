package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import edu.bonn.mobilegaming.geoquest.mission.NFCTagReadingProduct;

public abstract class NFCTagReadingProductUI extends MissionUI {

	/**
	 * Initializes the UI for an NPCTalk mission.
	 * 
	 * @param activity
	 */
	public NFCTagReadingProductUI(NFCTagReadingProduct activity) {
		super(activity);
	}

	protected NFCTagReadingProduct getNFCMission() {
		return (NFCTagReadingProduct) activity;
	}

	/**
	 * Starts the interaction.
	 */
	public abstract void init(String data);
}
