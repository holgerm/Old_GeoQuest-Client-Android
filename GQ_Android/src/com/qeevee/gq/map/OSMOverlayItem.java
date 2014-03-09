package com.qeevee.gq.map;

import org.osmdroid.views.overlay.OverlayItem;

import edu.bonn.mobilegaming.geoquest.HotspotOld;

public class OSMOverlayItem extends OverlayItem {

	private HotspotOld hotspot;

	public OSMOverlayItem(HotspotOld hotspot) {
		super(hotspot.id, hotspot.name, hotspot.getOSMGeoPoint());
		this.hotspot = hotspot;
	}

	public HotspotOld getHotspot() {
		return hotspot;
	}

}
