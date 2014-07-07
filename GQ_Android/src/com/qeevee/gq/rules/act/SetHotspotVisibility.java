package com.qeevee.gq.rules.act;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;


public class SetHotspotVisibility extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("id") && params.containsKey("visible");
	}

	@Override
	public void execute() {
		Hotspot hotspot = HotspotManager.getInstance().getExisting(
				params.get("id"));
		if (hotspot == null)
			return;
		if (params.get("visible").equals("true")
				|| params.get("visible").equals("1")) {
			hotspot.setVisible(true);
		}
		if (params.get("visible").equals("false")
				|| params.get("visible").equals("0")) {
			hotspot.setVisible(false);
		}

		GeoQuestApp.getInstance().refreshMapDisplay();
	}

}
