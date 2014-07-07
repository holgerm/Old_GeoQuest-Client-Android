package com.qeevee.gq.ui.standard;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.loc.HotspotVisbilityListener;
import com.qeevee.gq.loc.map.OSMItemizedOverlay;
import com.qeevee.gq.mission.MapOSM;
import com.qeevee.gq.ui.abstrakt.MapOSM_UI;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;

public class MapOSM_UIDefault extends MapOSM_UI implements
		HotspotVisbilityListener {

	private static final int LAYOUT_RESOURCE = R.layout.m_default_osmap;
	private static final String TAG = MapOSM_UIDefault.class.getCanonicalName();
	private OSMItemizedOverlay itemizedOverlay;

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

		HotspotManager hm = HotspotManager.getInstance();
		hm.registerHotspotVisbilityListener(this);

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
		HotspotManager.getInstance().unregisterHotspotVisbilityListener(this);
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

	public void updateHotspotVisibility() {
		mapView.getOverlays().remove(itemizedOverlay);
		itemizedOverlay = new OSMItemizedOverlay(HotspotManager.getInstance()
				.getHotspotOverlayItems(), gestureListener,
				new DefaultResourceProxyImpl(getOSMap()));
		mapView.getOverlays().add(itemizedOverlay);
		mapView.postInvalidate();
	}

	public void hideHotspot(Hotspot hotspot) {
		if (mapView == null || mapView.getOverlays() == null) {
			Log.e(TAG, "MapView not initialized correctly.");
			return;
		}
		int i = mapView.getOverlays().indexOf(itemizedOverlay);
		if (i < 0) {
			Log.e(TAG, "ItemizedOverlay not found in OSMMap overlay list.");
			return;
		}
		OSMItemizedOverlay curItemizedOverlay = (OSMItemizedOverlay) mapView
				.getOverlays().get(i);
		curItemizedOverlay.removeItem(hotspot.getOverlayItem());
	}

	public void unveilHotspot(Hotspot hotspot) {
		if (mapView == null || mapView.getOverlays() == null) {
			Log.e(TAG, "MapView not initialized correctly.");
			return;
		}
		int i = mapView.getOverlays().indexOf(itemizedOverlay);
		if (i < 0) {
			Log.e(TAG, "ItemizedOverlay not found in OSMMap overlay list.");
			return;
		}
		OSMItemizedOverlay curItemizedOverlay = (OSMItemizedOverlay) mapView
				.getOverlays().get(i);
		curItemizedOverlay.addItem(hotspot.getOverlayItem());
	}

	public void initializeVisibleHotspots() {
		itemizedOverlay = new OSMItemizedOverlay(HotspotManager.getInstance()
				.getHotspotOverlayItems(), gestureListener,
				new DefaultResourceProxyImpl(getOSMap()));
		mapView.getOverlays().add(itemizedOverlay);
	}
}
