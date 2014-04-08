package com.qeevee.gq.rules.act;

import android.util.Log;

import com.qeevee.util.StringTools;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.MissionOrToolActivity;

public class StartMission extends Action {

	private static final String TAG = StartMission.class.getCanonicalName();

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("id");
	}

	@Override
	public void _execute() {
		boolean keepActivity = StringTools
				.asBoolean(params.get("keepActivity"));
		Mission mission = Mission.get(params.get("id"));
		try {
			((MissionOrToolActivity) GeoQuestApp.getCurrentActivity())
					.setKeepActivity(keepActivity);
		} catch (ClassCastException e) {
			Log.e(TAG,
					"Tried to execute StartMission action within a non-mission activity.");
		}
		mission.startMission(keepActivity);
	}
}
