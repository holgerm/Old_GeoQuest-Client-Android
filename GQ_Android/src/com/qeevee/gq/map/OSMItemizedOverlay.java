package com.qeevee.gq.map;

import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import edu.bonn.mobilegaming.geoquest.HotspotOld;

public class OSMItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {

	private List<HotspotOld> hotspots;

	public OSMItemizedOverlay(List<OverlayItem> itemlist,
			OnItemGestureListener<OverlayItem> itemGestureListener,
			ResourceProxy resourceProxy) {
		super(itemlist, itemGestureListener, resourceProxy);
		hotspots = HotspotOld.getListOfHotspots();
	}

	public void updateHotspots() {
		hotspots = HotspotOld.getListOfHotspots();
		OSMOverlayItem item;
		for (HotspotOld curHotspot : hotspots) {
			item = new OSMOverlayItem(curHotspot);
			item.setMarker(curHotspot.getDrawable());
			this.addItem(item);
		}
	}

	@Override
	protected boolean onSingleTapUpHelper(int index, OverlayItem item,
			MapView mapView) {
		if (item instanceof OSMOverlayItem) {
			HotspotOld hotspot = ((OSMOverlayItem) item).getHotspot();
			hotspot.runOnTapEvent();
		}
		return true;
	}
}
