package com.qeevee.gq.rules.act;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Globals;

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
