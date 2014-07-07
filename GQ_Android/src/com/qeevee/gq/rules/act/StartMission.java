package com.qeevee.gq.rules.act;

import android.util.Log;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Globals;
import com.qeevee.gq.Mission;
import com.qeevee.gq.MissionOrToolActivity;
import com.qeevee.gq.mission.MapMissionActivity;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.util.StringTools;


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
