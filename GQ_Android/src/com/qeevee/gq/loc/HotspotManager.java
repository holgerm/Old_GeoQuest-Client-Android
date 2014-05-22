package com.qeevee.gq.loc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import com.qeevee.gq.loc.map.OSMOverlayItem;

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

	public void add(Hotspot hotspot) {
		allHotspots.put(hotspot.id, hotspot);
		if (hotspot.isVisible()) {
			overlayItemList.add(new OSMOverlayItem(hotspot));
		}
	}

	public void clear() {
		allHotspots.clear();
		overlayItemList.clear();
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

	private List<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();

	public List<OverlayItem> getHotspotOverlayItems() {
		return overlayItemList;
	}

	public void hideHotspot(Hotspot hotspot) {
		if (allHotspots.containsKey(hotspot.id)) {
			overlayItemList.remove(hotspot.getOverlayItem());
			for (Iterator<HotspotVisbilityListener> iterator = hotspotVisbilityListeners
					.iterator(); iterator.hasNext();) {
				HotspotVisbilityListener listener = (HotspotVisbilityListener) iterator
						.next();
				listener.hideHotspot(hotspot);
			}
		}
	}

	public void unveilHotspot(Hotspot hotspot) {
		if (allHotspots.containsKey(hotspot.id)) {
			overlayItemList.add(hotspot.getOverlayItem());
			for (Iterator<HotspotVisbilityListener> iterator = hotspotVisbilityListeners
					.iterator(); iterator.hasNext();) {
				HotspotVisbilityListener listener = (HotspotVisbilityListener) iterator
						.next();
				listener.unveilHotspot(hotspot);
			}
		}
	}

	private List<HotspotVisbilityListener> hotspotVisbilityListeners = new ArrayList<HotspotVisbilityListener>();

	public void registerHotspotVisbilityListener(
			HotspotVisbilityListener listener) {
		hotspotVisbilityListeners.add(listener);
		listener.initializeVisibleHotspots();
	}

	public boolean unregisterHotspotVisbilityListener(
			HotspotVisbilityListener listener) {
		return hotspotVisbilityListeners.remove(listener);
	}

}
