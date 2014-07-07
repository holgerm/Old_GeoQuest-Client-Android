package com.qeevee.gq.ui.abstrakt;

import org.dom4j.Element;

import com.qeevee.gq.mission.NPCTalk;


public abstract class NPCTalkUI extends MissionUI {

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

	@Override
	protected Element getMissionXML() {
		return getNPCTalk().getXML();
	}

}
