package com.qeevee.gq.loc.map;

import org.osmdroid.views.overlay.OverlayItem;

import com.qeevee.gq.loc.Hotspot;


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
