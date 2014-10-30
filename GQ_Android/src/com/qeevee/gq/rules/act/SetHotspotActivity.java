package com.qeevee.gq.rules.act;

import static com.qeevee.gq.xml.XMLUtilities.stringToBool;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;


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
		hotspot.setActive(stringToBool(params.get("mode")));

		GeoQuestApp.getInstance().refreshMapDisplay();
	}
}
