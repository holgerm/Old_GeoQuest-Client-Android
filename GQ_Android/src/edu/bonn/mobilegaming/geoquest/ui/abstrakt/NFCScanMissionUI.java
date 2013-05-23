package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import edu.bonn.mobilegaming.geoquest.mission.NFCScanMission;

public abstract class NFCScanMissionUI extends MissionUI {

	/**
	 * Initializes the UI for an NPCTalk mission.
	 * 
	 * @param activity
	 */
	public NFCScanMissionUI(NFCScanMission activity) {
		super(activity);
	}

	protected NFCScanMission getNFCScanMission() {
		return (NFCScanMission) activity;
	}

	/**
	 * Starts the interaction.
	 */
	public abstract void init();
	
	public abstract void success();
	
	public abstract void success(String data);
	public abstract boolean verifyUserInput(String data);
	
	
	public abstract void enableButton(boolean enable);
	
	
}