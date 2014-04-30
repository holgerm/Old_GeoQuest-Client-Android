package com.qeevee.gq.rules.act;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class EndGame extends Action {

	@Override
	protected boolean checkInitialization() {
		return true;
	}

	@Override
	public void execute() {
		GeoQuestApp.getInstance().endGame();
	}

}
