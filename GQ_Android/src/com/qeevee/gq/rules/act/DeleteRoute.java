package com.qeevee.gq.rules.act;

import com.qeevee.gq.loc.RouteManager;

public class DeleteRoute extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("from") && params.containsKey("to");
	}

	@Override
	public void execute() {
		
		String id = params.get("from") + "/" + params.get("to");
		RouteManager.getInstance().remove(id);
	}
}
