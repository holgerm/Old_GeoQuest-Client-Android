package com.qeevee.gq.map;

import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import edu.bonn.mobilegaming.geoquest.Hotspot;

public class OSMItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {

	private List<Hotspot> hotspots;

	public OSMItemizedOverlay(List<OverlayItem> itemlist,
			OnItemGestureListener<OverlayItem> itemGestureListener,
			ResourceProxy resourceProxy) {
		super(itemlist, itemGestureListener, resourceProxy);
		hotspots = Hotspot.getListOfHotspots();
	}

	public void updateHotspots() {
		hotspots = Hotspot.getListOfHotspots();
		OSMOverlayItem item;
		for (Hotspot curHotspot : hotspots) {
			item = new OSMOverlayItem(curHotspot);
			item.setMarker(curHotspot.getDrawable());
			this.addItem(item);
		}
	}

	@Override
	protected boolean onSingleTapUpHelper(int index, OverlayItem item,
			MapView mapView) {
		if (item instanceof OSMOverlayItem) {
			Hotspot hotspot = ((OSMOverlayItem) item).getHotspot();
			hotspot.runOnTapEvent();
		}
		return true;
	}
}
