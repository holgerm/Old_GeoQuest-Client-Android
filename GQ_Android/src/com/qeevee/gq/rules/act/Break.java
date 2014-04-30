package com.qeevee.gq.rules.act;

import edu.bonn.mobilegaming.geoquest.Variables;

public class Break extends Action {

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		return initOK;
	}

	@Override
	public void execute() {
		Variables.setValue(Variables.BREAK_WHILE, Boolean.valueOf("true"));
	}
}
