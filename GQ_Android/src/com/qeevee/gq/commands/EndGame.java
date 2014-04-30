package com.qeevee.gq.commands;

import java.util.List;

import android.app.Activity;

import com.qeevee.gq.loc.Hotspot;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;

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
		gqApp.setInGame(false);
		GeoQuestApp.setRunningGameDir(null);
		GeoQuestApp.cleanMediaPlayer();

		if (gqApp.isUsingAutostart())
			gqApp.terminateApp();
		return true;
	}
}
