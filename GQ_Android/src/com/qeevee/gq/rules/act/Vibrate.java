package com.qeevee.gq.rules.act;

import android.content.Context;
import android.os.Vibrator;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class Vibrate extends Action {

	static final long DEFAULT_DURATION = 400;

	@Override
	protected boolean checkInitialization() {
		return true;
	}

	@Override
	public void execute() {
		long duration;

		if (params.containsKey("duration")) {
			try {
				duration = Long.parseLong(params.get("duration"));
			} catch (NumberFormatException e) {
				duration = DEFAULT_DURATION;
			}
		} else
			duration = DEFAULT_DURATION;
		
		((Vibrator) GeoQuestApp.getInstance().getSystemService(
				Context.VIBRATOR_SERVICE)).vibrate(duration);
	}

}
