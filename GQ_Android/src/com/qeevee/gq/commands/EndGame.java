package com.qeevee.gq.commands;

import java.util.List;

import android.app.Activity;

import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.mission.MissionActivity;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Mission;
import com.qeevee.gq.R;
import com.qeevee.gq.Variables;
import com.qeevee.ui.BitmapUtil;

public class EndGame extends GQCommand {

	public EndGame() {
		super(GeoQuestApp.getContext().getString(R.string.alert_endGameTitel),
				GeoQuestApp.getContext().getString(
						R.string.alert_endGameMessage),
				R.string.alert_endGameButtonOK,
				R.string.alert_endGameButtonCancel);
	}

	@Override
	public boolean doIt() {
		GeoQuestApp.stopAudio();
		Mission.clean();
		Hotspot.clean();
		Variables.clean();
		BitmapUtil.clearBitmapCache();
		GeoQuestApp gqApp = GeoQuestApp.getInstance();
		List<Activity> gqActivities = gqApp.getActivities();
		Activity[] allActivities = new Activity[gqActivities.size()];
		gqActivities.toArray(allActivities);
		for (int i = 0; i < allActivities.length; i++) {
			if (gqApp.isGameActivity(allActivities[i])) {
				gqActivities.remove(allActivities[i]);
				allActivities[i].finish();
			}
			if (allActivities[i] instanceof MissionActivity) {
				MissionActivity missionActivity = (MissionActivity) allActivities[i];
				gqApp.removeMissionActivity(missionActivity.getMission().id);
			}
		}

		if (gqApp.isUsingAutostart())
			gqApp.terminateApp();
		else {
			GeoQuestApp.setRunningGameDir(null);
			gqApp.setInGame(false);
			GeoQuestApp.cleanMediaPlayer();
		}
		return true;
	}
}
