package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import edu.bonn.mobilegaming.geoquest.mission.NFCTag;

public abstract class NFCTagUI extends MissionUI {

    /**
     * Initializes the UI for an NPCTalk mission.
     * 
     * @param activity
     */
    public NFCTagUI(NFCTag activity) {
	super(activity);
    }

    protected NFCTag getNFCTag() {
	return (NFCTag) activity;
    }

    /**
     * Starts the interaction.
     */
    public abstract void init();

}