package com.qeevee.gq.rules.act;

import com.qeevee.gq.GeoQuestApp;

public class EndGame extends Action implements LeavesMission {

	@Override
	protected boolean checkInitialization() {
		return true;
	}

	@Override
	public void execute() {
		GeoQuestApp.getInstance().endGame();
	}

}
