package com.qeevee.gq.rules.act;

import com.qeevee.gq.base.GeoQuestApp;


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
