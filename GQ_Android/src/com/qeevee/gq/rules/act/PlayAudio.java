package com.qeevee.gq.rules.act;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class PlayAudio extends Action {

	@Override
	protected boolean checkInitialization() {
		return (params.containsKey("file"));
	}

	@Override
	public void execute() {
		if (params.containsKey("file"))
			GeoQuestApp.playAudio(params.get("file"), false);
    }

}
