package com.qeevee.gq.loc;

import java.util.Iterator;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;

import android.location.Location;
import edu.bonn.mobilegaming.geoquest.GeoQuestLocationListener;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.MapNavigation;

public class MapHelper {

	private IMapController mapController;
	private GeoQuestLocationListener locationListener;
	private MapNavigation mapMission;

	/**
	 * Constructor for Google Map based Navigation Mission.
	 * 
	 * @param mapMission
	 * @param mapCtrl
	 */
	public MapHelper(final MapNavigation mapMission) {
		this.mapMission = mapMission;
		mapController = mapMission.getMapController();

		// register location changed listener:
		locationListener = new GeoQuestLocationListener(mapMission) {
			public void onRelevantLocationChanged(Location location) {
				super.onRelevantLocationChanged(location);
				GeoPoint point = location2GP(location);
				if (!locatingIsManual())
					mapController.animateTo(point);

				// calculate distance to hotspots
				for (Iterator<Hotspot> i = mapMission.getHotspots()
						.listIterator(); i.hasNext();) {
					Hotspot hotspot = i.next();
					// TODO: throws a
					// ConcurrentModificationException
					// sometimes (hm)
					if (hotspot.isActive())
						hotspot.inRange(location);
				}
			}
		};
	}

	public IMapController getController() {
		return mapController;
	}

	public void centerMap() {
		Location lastLoc = locationListener.getLastLocation();
		if (lastLoc != null)
			mapController.animateTo(location2GP(locationListener
					.getLastLocation()));
	}

	public void setCenter() {
		if (locatingIsManual())
			return;
		Location lastLoc = locationListener.getLastLocation();
		if (lastLoc != null) {
			mapController.setCenter(location2GP(locationListener
					.getLastLocation()));
			mapController.animateTo(location2GP(locationListener
					.getLastLocation()));
		} else {
			List<Hotspot> hotspots = this.mapMission.getHotspots();
			if (hotspots.size() > 0) {
				com.google.android.maps.GeoPoint firstAGP = hotspots.get(0)
						.getGeoPoint();
				GeoPoint centerGP = new GeoPoint(firstAGP.getLatitudeE6(),
						firstAGP.getLongitudeE6());
				mapController.setCenter(centerGP);
				mapController.animateTo(centerGP);
			}
		}
	}

	private boolean locatingIsManual() {
		boolean positionDefined = Variables
				.isDefined(Variables.CENTER_MAP_POSITION)
				&& (Variables.getValue(Variables.CENTER_MAP_POSITION).equals(
						"true") || Variables.getValue(
						Variables.CENTER_MAP_POSITION).equals("1"));
		boolean visbileHSDefined = Variables
				.isDefined(Variables.CENTER_MAP_VISIBLE_HOTSPOTS)
				&& (Variables.getValue(Variables.CENTER_MAP_VISIBLE_HOTSPOTS)
						.equals("true") || Variables.getValue(
						Variables.CENTER_MAP_VISIBLE_HOTSPOTS).equals("1"));
		boolean activeHSDefined = Variables
				.isDefined(Variables.CENTER_MAP_ACTIVE_HOTSPOTS)
				&& (Variables.getValue(Variables.CENTER_MAP_ACTIVE_HOTSPOTS)
						.equals("true") || Variables.getValue(
						Variables.CENTER_MAP_ACTIVE_HOTSPOTS).equals("1"));
		return positionDefined || visbileHSDefined || activeHSDefined;
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
