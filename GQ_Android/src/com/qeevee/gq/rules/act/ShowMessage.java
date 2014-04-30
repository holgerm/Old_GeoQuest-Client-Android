package com.qeevee.gq.rules.act;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;


public class ShowMessage extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("message");
	}

	@Override
	public void execute() {
		GeoQuestApp.showMessage(params.get("message"));
	}

}
