package com.qeevee.gq.map;

import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.widget.Toast;
import edu.bonn.mobilegaming.geoquest.HotspotOld;

public class OSMItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {

	private List<HotspotOld> hotspots;
	private Context context;

	public OSMItemizedOverlay(Context ctx, List<OverlayItem> itemlist,
			OnItemGestureListener<OverlayItem> itemGestureListener,
			ResourceProxy resourceProxy) {
		super(itemlist, itemGestureListener, resourceProxy);
		hotspots = HotspotOld.getListOfHotspots();
		this.context = ctx;
	}

	public void updateHotspots() {
		hotspots = HotspotOld.getListOfHotspots();
		OverlayItem item;
		for (HotspotOld curHotspot : hotspots) {
			item = new OverlayItem(curHotspot.getName(),
					curHotspot.getDescription(), curHotspot.getOSMGeoPoint());
			item.setMarker(curHotspot.getDrawable());
			this.addItem(item);
		}
	}

	@Override
	protected boolean onSingleTapUpHelper(int index, OverlayItem item,
			MapView mapView) {
		Toast.makeText(context, "tapped", Toast.LENGTH_SHORT).show();
		return true;
	}
}
