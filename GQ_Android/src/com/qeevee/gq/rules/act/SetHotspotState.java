package com.qeevee.gq.rules.act;

import static com.qeevee.gq.xml.XMLUtilities.stringToBool;

import java.util.ArrayList;
import java.util.List;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;

public class SetHotspotState extends Action {

	@Override
	protected boolean checkInitialization() {
		boolean valid = true;
		valid &= eitherApplyToAllOrHotspotIdIsGiven();
		valid &= params.containsKey("activity")
				|| params.containsKey("visibility");
		return valid;
	}

	private boolean eitherApplyToAllOrHotspotIdIsGiven() {
		return (params.containsKey("hotspot") || params
				.containsKey("applyToAll")
				&& stringToBool(params.get("applyToAll")));
	}

	@Override
	public void execute() {
		List<Hotspot> hsList;
		if (stringToBool(params.get("applyToAll"))) {
			hsList = HotspotManager.getInstance().getListOfHotspots();
		} else {
			hsList = new ArrayList<Hotspot>(1);
			Hotspot hotspot = HotspotManager.getInstance().getExisting(
					params.get("hotspot"));
			if (hotspot == null)
				return;
			else
				hsList.add(hotspot);
		}

		for (Hotspot curHS : hsList) {
			if ("aktiv".equals(params.get("activity")))
				curHS.setActive(true);
			else if ("inaktiv".equals(params.get("activity")))
				curHS.setActive(false);
			// in other cases (like "unverändert") we do not change the value
			// here!
			if ("sichtbar".equals(params.get("visibility")))
				curHS.setVisible(true);
			else if ("unsichtbar".equals(params.get("visibility")))
				curHS.setVisible(false);
		}

		GeoQuestApp.getInstance().refreshMapDisplay();
	}
}
