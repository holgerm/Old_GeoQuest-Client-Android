package edu.bonn.mobilegaming.geoquest.ui.standard;

import java.util.Iterator;
import java.util.List;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
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
		myLocationOverlay = new MyLocationOverlay(getOSMap(), mapView);
		myLocationOverlay.enableCompass(); // doesn't work in the emulator?
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.setCompassCenter(60L, 60L);
		mapView.getOverlays().add(myLocationOverlay);
		List<Overlay> mapOverlays = mapView.getOverlays();
		for (Iterator<HotspotOld> iterator = HotspotOld.getListOfHotspots()
				.iterator(); iterator.hasNext();) {
			HotspotOld hotspot = (HotspotOld) iterator.next();
			mapOverlays.add(hotspot.getOSMOverlay());
		}

		GeoQuestApp.getInstance().setOSMap(mapView);
	}

	private MyLocationOverlay myLocationOverlay;

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
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
	}

	public void enable() {
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
		getOSMap().getMapHelper().setCenter();
	}

}
