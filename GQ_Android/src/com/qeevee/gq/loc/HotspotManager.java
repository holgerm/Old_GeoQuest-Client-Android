package com.qeevee.gq.loc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.osmdroid.util.GeoPoint;

public class HotspotManager {

	private static HotspotManager instance;

	public static HotspotManager getInstance() {
		if (instance == null)
			instance = new HotspotManager();
		return instance;
	}

	/**
	 * Hashtable mapping id to Hotspot
	 */
	private Hashtable<String, Hotspot> allHotspots = new Hashtable<String, Hotspot>();

	public List<Hotspot> getListOfHotspots() {
		List<Hotspot> list = new ArrayList<Hotspot>();
		list.addAll(allHotspots.values());
		return list;
	}

	/**
	 * @param id
	 *            the identifier of the hotspot as specified in game.xml.
	 * @return the Hotspot object or null if no hotspot with ID id exists.
	 */
	public Hotspot getExisting(String id) {
		return (allHotspots.get(id));
	}

	public boolean existsHostpot(String id) {
		return allHotspots.containsKey(id);
	}

	public void add(String id, Hotspot hotspot) {
		allHotspots.put(id, hotspot);
	}

	public void clear() {
		allHotspots.clear();
	}

	public Collection<GeoPoint> getGeoPointsOfActiveHotspots() {
		ArrayList<GeoPoint> activePoints = new ArrayList<GeoPoint>();
		for (Hotspot curHotspot : allHotspots.values()) {
			if (curHotspot.isActive())
				activePoints.add(curHotspot.getOSMGeoPoint());
		}
		return activePoints;
	}

	public Collection<GeoPoint> getGeoPointsOfVisibleHotspots() {
		ArrayList<GeoPoint> visiblePoints = new ArrayList<GeoPoint>();
		for (Hotspot curHotspot : allHotspots.values()) {
			if (curHotspot.isVisible())
				visiblePoints.add(curHotspot.getOSMGeoPoint());
		}
		return visiblePoints;
	}

	public List<Hotspot> getListOfVisibleHotspots() {
		List<Hotspot> visibleHotspots = new ArrayList<Hotspot>();
		for (Hotspot hotspot : allHotspots.values()) {
			if (hotspot.isVisible())
				visibleHotspots.add(hotspot);
		}
		return visibleHotspots;
	}
}
