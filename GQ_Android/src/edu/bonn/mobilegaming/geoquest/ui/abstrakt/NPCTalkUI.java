package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;

public abstract class NPCTalkUI extends MissionUI {

	/**
	 * Initializes the UI for an NPCTalk mission.
	 * 
	 * @param activity
	 */
	public NPCTalkUI(NPCTalk activity) {
		super(activity);
	}

	protected NPCTalk getNPCTalk() {
		return (NPCTalk) activity;
	}

	/**
	 * Starts the interaction.
	 */
	public void init() {
		showNextDialogItem();
	}

	/**
	 * Shows the next dialog item of this mission, if there is yet another one.
	 */
	public abstract void showNextDialogItem();

	/**
	 * Ends this mission if the UI allows that at this moment.
	 */
	public abstract void finishMission();

}
