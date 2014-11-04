package com.qeevee.util;

import org.osmdroid.util.GeoPoint;

import android.location.Location;

public class Geo {

	public static GeoPoint location2GP(Location location) {
		if (location == null)
			return null;
		GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		return point;
	}

}
