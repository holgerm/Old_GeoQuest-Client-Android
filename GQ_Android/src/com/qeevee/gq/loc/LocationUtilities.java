package com.qeevee.gq.loc;


import android.content.Context;
import android.location.Location;

public class LocationUtilities {
	
	public static Location getCurrentLocation(Context context) {
		SmartLocationListener locationListener = new SmartLocationListener(context) {

			@Override
			protected void onRelevantLocationChanged(
					Location newLocation) {
				// TODO Auto-generated method stub
				
			}
			
		};
		locationListener.connect();
		Location lastLocation = locationListener.getLastLocation();
		locationListener.disconnect();
		return lastLocation;
	}
	
}
