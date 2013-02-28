package com.qeevee.util.location;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;

import android.location.Location;
import edu.bonn.mobilegaming.geoquest.GeoQuestLocationListener;

public class MapHelper {

    private IMapController mapController;

    public
	    void
	    setGoogleMapController(com.google.android.maps.MapController mapCtrl) {
	mapController = new org.osmdroid.google.wrapper.MapController(mapCtrl);
    }

    public void setOSMapController(MapController mapCtrl) {
	mapController = mapCtrl;
    }

    public void centerMap(GeoQuestLocationListener locationListener) {
	Location lastLoc = locationListener.getLastLocation();
	if (lastLoc != null)
	    mapController.animateTo(location2GP(locationListener
		    .getLastLocation()));
    }

    private GeoPoint location2GP(Location location) {
	if (location == null)
	    return null;
	GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
		(int) (location.getLongitude() * 1E6));
	return point;
    }

}
