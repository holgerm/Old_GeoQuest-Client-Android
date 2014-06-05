package com.qeevee.gq.rules.act;

import android.util.Log;

import com.qeevee.util.StringTools;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.MissionOrToolActivity;
import edu.bonn.mobilegaming.geoquest.mission.MapMissionActivity;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;

public class StartMission extends Action implements LeavesMission {

	private static final String TAG = StartMission.class.getCanonicalName();

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("id");
	}

	@Override
	public void execute() {
		boolean keepActivity = StringTools
				.asBoolean(params.get("keepActivity"));
		Mission mission = Mission.get(params.get("id"));
		MissionOrToolActivity currentMissionActivity;
		try {
			currentMissionActivity = ((MissionOrToolActivity) GeoQuestApp
					.getCurrentActivity());
			currentMissionActivity.setKeepActivity(keepActivity);
			mission.startMission(keepActivity);
			if (!keepActivity) {
				Mission currentMission = Mission.get(currentMissionActivity
						.getMissionID());
				currentMission.setStatus(Globals.STATUS_SUCCEEDED);
				GeoQuestApp.getInstance().removeMissionActivity(
						currentMission.id);
				if (currentMissionActivity instanceof MissionActivity) {
					((MissionActivity) currentMissionActivity).finish();
				}
				if (currentMissionActivity instanceof MapMissionActivity) {
					((MapMissionActivity) currentMissionActivity).finish();
				}
			}
		} catch (ClassCastException e) {
			Log.e(TAG,
					"Tried to execute StartMission action within a non-mission activity.");
		}
	}
}
