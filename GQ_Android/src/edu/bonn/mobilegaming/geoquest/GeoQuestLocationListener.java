package edu.bonn.mobilegaming.geoquest;

import android.content.Context;
import android.location.Location;

import com.qeevee.gq.loc.SmartLocationListener;

public class GeoQuestLocationListener extends SmartLocationListener {

	public GeoQuestLocationListener(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onRelevantLocationChanged(Location newLocation) {
		//GeoQuestApp.getInstance().setLastKnownLocation(newLocation);
	}

}
