package com.qeevee.gq.rules.act;

import static com.qeevee.gq.xml.XMLUtilities.stringToBool;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;

public class SetHotspotVisibility extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("id")
				&& (params.containsKey("visible") || params.containsKey("mode"));
	}

	@Override
	public void execute() {
		Hotspot hotspot = HotspotManager.getInstance().getExisting(
				params.get("id"));
		if (hotspot == null)
			return;
		boolean newVisbility = stringToBool(params.get("mode"))
				|| stringToBool(params.get("visible"));
		hotspot.setVisible(newVisbility);

		GeoQuestApp.getInstance().refreshMapDisplay();
	}

}
