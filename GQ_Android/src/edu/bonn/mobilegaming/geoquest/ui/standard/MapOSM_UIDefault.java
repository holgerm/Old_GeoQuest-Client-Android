package edu.bonn.mobilegaming.geoquest.ui.standard;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.loc.map.OSMItemizedOverlay;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.MapOSM;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MapOSM_UI;

public class MapOSM_UIDefault extends MapOSM_UI {

	private static final int LAYOUT_RESOURCE = R.layout.m_default_osmap;

	public MapOSM_UIDefault(MapOSM activity) {
		super(activity);
		initMap();
	}

	private void initMap() {
		// Players Location Overlay
		myLocationOverlay = new MyLocationNewOverlay(getOSMap(), mapView);
		// myLocationOverlay.enableCompass(); // doesn't work in the emulator?
		myLocationOverlay.enableMyLocation();
		// myLocationOverlay.setCompassCenter(60L, 60L);
		mapView.getOverlays().add(myLocationOverlay);
		// List<Overlay> mapOverlays = mapView.getOverlays();

		// OverlayItem currentItem;
		// List<OverlayItem> itemList = new ArrayList<OverlayItem>();
		// for (Iterator<Hotspot> iterator = HotspotManager.getInstance()
		// .getListOfVisibleHotspots().iterator(); iterator.hasNext();) {
		// Hotspot hotspot = (Hotspot) iterator.next();
		// currentItem = new OSMOverlayItem(hotspot);
		// currentItem.setMarker(hotspot.getDrawable());
		// itemList.add(currentItem);
		// }
		HotspotManager hm = HotspotManager.getInstance();
		OSMItemizedOverlay itemizedOverlay = new OSMItemizedOverlay(
				hm.getHotspotOverlayItems(), gestureListener,
				new DefaultResourceProxyImpl(getOSMap()));
		mapView.getOverlays().add(itemizedOverlay);

		GeoQuestApp.getInstance().setOSMap(mapView);

	}

	private MyLocationNewOverlay myLocationOverlay;

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishMission() {
		// TODO Auto-generated method stub

	}

	@Override
	public View createContentView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		contentView = inflater.inflate(LAYOUT_RESOURCE, null);
		outerView = (View) contentView.findViewById(R.id.outerview);
		mapView = (MapView) contentView.findViewById(R.id.osmapview);
		mapView.setBuiltInZoomControls(false);
		mapView.setMultiTouchControls(true);
		return contentView;
	}

	public void disable() {
		// myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
	}

	public void enable() {
		// myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
		getOSMap().getMapHelper().setCenter();
	}

	private OnItemGestureListener<OverlayItem> gestureListener = new OnItemGestureListener<OverlayItem>() {

		public boolean onItemLongPress(int index, OverlayItem item) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean onItemSingleTapUp(int index, OverlayItem item) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	public void release() {
		contentView.destroyDrawingCache();
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).removeAllViews();
		}
		mapView.destroyDrawingCache();
		mapView.removeAllViews();

		contentView = null;
		mapView = null;
		myLocationOverlay = null;
	}

}
