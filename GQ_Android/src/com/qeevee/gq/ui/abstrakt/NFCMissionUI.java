package com.qeevee.gq.ui.abstrakt;

import com.qeevee.gq.mission.NFCMission;

import android.nfc.Tag;

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
