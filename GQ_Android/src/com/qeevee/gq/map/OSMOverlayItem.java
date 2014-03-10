package com.qeevee.gq.map;

import org.osmdroid.views.overlay.OverlayItem;

import edu.bonn.mobilegaming.geoquest.Hotspot;

public class OSMOverlayItem extends OverlayItem {

	private Hotspot hotspot;

	public OSMOverlayItem(Hotspot hotspot) {
		super(hotspot.id, hotspot.name, hotspot.getOSMGeoPoint());
		this.hotspot = hotspot;
	}

	public Hotspot getHotspot() {
		return hotspot;
	}

}
