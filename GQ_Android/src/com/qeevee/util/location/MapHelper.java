package com.qeevee.util.location;

import java.util.Iterator;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;

import android.location.Location;
import edu.bonn.mobilegaming.geoquest.GeoQuestLocationListener;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
import edu.bonn.mobilegaming.geoquest.mission.MapNavigation;

public class MapHelper {

    private IMapController mapController;
    private GeoQuestLocationListener locationListener;

    /**
     * Constructor for Google Map based Navigation Mission.
     * 
     * @param mapMission
     * @param mapCtrl
     */
    public MapHelper(final MapNavigation mapMission) {
	mapController = mapMission.getMapController();

	// register location changed listener:
	locationListener = new GeoQuestLocationListener(mapMission) {
	    public void onRelevantLocationChanged(Location location) {
		super.onRelevantLocationChanged(location);
		GeoPoint point = location2GP(location);
		mapController.animateTo(point);

		// calculate distance to hotspots
		for (Iterator<HotspotOld> i = mapMission.getHotspots()
			.listIterator(); i.hasNext();) {
		    HotspotOld hotspot = i.next(); // TODO: throws a
						   // ConcurrentModificationException
						   // sometimes (hm)
		    hotspot.inRange(location);
		}
	    }
	};
    }

    public void centerMap() {
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

    public GeoQuestLocationListener getLocationListener() {
	return locationListener;
    }

}
