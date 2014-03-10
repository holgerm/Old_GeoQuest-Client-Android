package com.qeevee.gq.rules.act;

import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

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
		if (params.get("visible").equals("true")) {
			hotspot.setVisible(true);
		}
		if (params.get("visible").equals("false")) {
			hotspot.setVisible(false);
		}

		GeoQuestApp.getInstance().refreshMapDisplay();
	}

}
