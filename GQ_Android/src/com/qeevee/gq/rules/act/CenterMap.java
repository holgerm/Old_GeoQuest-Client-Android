package com.qeevee.gq.rules.act;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Variables;
import com.qeevee.gq.mission.MapOSM;

import android.app.Activity;

public class CenterMap extends Action {

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		if (params.containsKey("position")) {
			initOK &= isBooleanLiteral(params.get("position"));
		}
		if (params.containsKey("activeHotspots")) {
			initOK &= isBooleanLiteral(params.get("activeHotspots"));
		}
		if (params.containsKey("visibleHotspots")) {
			initOK &= isBooleanLiteral(params.get("visibleHotspots"));
		}
		return initOK;
	}

	@Override
	public void execute() {
		if (params.containsKey("position"))
			Variables.setValue(Variables.CENTER_MAP_POSITION,
					params.get("position"));
		if (params.containsKey("activeHotspots"))
			Variables.setValue(Variables.CENTER_MAP_ACTIVE_HOTSPOTS,
					params.get("activeHotspots"));
		if (params.containsKey("visibleHotspots"))
			Variables.setValue(Variables.CENTER_MAP_VISIBLE_HOTSPOTS,
					params.get("visibleHotspots"));
		Activity currentActivity = GeoQuestApp.getCurrentActivity();
		if (currentActivity instanceof MapOSM) {
			((MapOSM) currentActivity).updateZoom();
		}
		currentActivity = null; // allow gc
	}

	private boolean isBooleanLiteral(String literal) {
		if (literal == null)
			return false;
		return literal.equals("true") || literal.equals("false")
				|| literal.equals("1") || literal.equals("0");
	}
}
