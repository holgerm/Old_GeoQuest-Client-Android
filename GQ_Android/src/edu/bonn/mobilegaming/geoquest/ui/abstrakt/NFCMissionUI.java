package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import android.nfc.Tag;
import edu.bonn.mobilegaming.geoquest.mission.NFCMission;

public abstract class NFCMissionUI extends MissionUI {

	/**
	 * Initializes the UI for an NPCTalk mission.
	 * 
	 * @param activity
	 */
	public NFCMissionUI(NFCMission activity) {
		super(activity);
	}

	protected NFCMission getNFCMission() {
		return (NFCMission) activity;
	}

	/**
	 * Starts the interaction.
	 */
	public abstract void init(Tag tag);
}
