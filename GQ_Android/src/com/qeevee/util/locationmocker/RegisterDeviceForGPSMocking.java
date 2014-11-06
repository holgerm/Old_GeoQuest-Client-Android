package com.qeevee.util.locationmocker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.qeevee.gq.base.GeoQuestApp;

import android.os.AsyncTask;

public class RegisterDeviceForGPSMocking extends
		AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... params) {
		if (params == null || params.length < 1)
			return null;
		String deviceName = params[0];
		String deviceID = LocationSource.getDeviceID(GeoQuestApp.getContext());
		StringBuilder responseString = new StringBuilder();
		try {
			URL url = new URL(LocationSource.LOCATION_MOCK_SERVER_URL + "?set=1&id="
					+ deviceID + "&name=" + deviceName);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String response = responseString.toString();
		return (response.startsWith("ok " + deviceID) && response
				.endsWith(deviceName));
	}

}
