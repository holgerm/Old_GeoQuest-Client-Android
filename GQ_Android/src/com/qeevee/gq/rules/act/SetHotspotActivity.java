package com.qeevee.gq.rules.act;

import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class SetHotspotActivity extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("id") && params.containsKey("mode");
	}

	@Override
	public void execute() {
		Hotspot hotspot = HotspotManager.getInstance().getExisting(
				params.get("id"));
		if (hotspot == null)
			return;
		if (params.get("mode").equals("true")) {
			hotspot.setActive(true);
		}
		if (params.get("mode").equals("false")) {
			hotspot.setActive(false);
		}

		GeoQuestApp.getInstance().refreshMapDisplay();
	}

}
