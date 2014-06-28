package com.qeevee.gq.comm;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class EEGSensorBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String emotion = intent.getStringExtra("Emotion");
		Toast.makeText(GeoQuestApp.getContext(),
				"Emotion received: " + emotion, Toast.LENGTH_LONG).show();
	}
}
