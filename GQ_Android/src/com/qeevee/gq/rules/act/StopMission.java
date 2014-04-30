package com.qeevee.gq.rules.act;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;

public class StopMission extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("id");
	}

	@Override
	public void execute() {
		GeoQuestApp.stopMission(params.get("id"), Globals.STATUS_SUCCEEDED);
	}

}
