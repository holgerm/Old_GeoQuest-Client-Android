package edu.bonn.mobilegaming.geoquest.mission;

import java.util.Iterator;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.MapHelper;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

/**
 * MapOverview mission. Based on the google map view a map view is shown in the
 * background. On the mapview Hotspots are drawn and there is a simple score
 * view, a button to change the navigation type and buttons to start the
 * missions that are in range of the current location.
 * 
 * @author Krischan Udelhoven
 * @author Folker Hoffmann
 * @author Holger Mügge
 */
public class MapGoogle extends MapNavigation {

	private MapView myMapView;
	private MyLocationOverlay myLocationOverlay;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Called when the activity is first created. Setups google mapView, the map
	 * overlays and the listeners
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Setup Google MapView
		myMapView = (MapView) findViewById(R.id.mapview);
		myMapView.setBuiltInZoomControls(false);
		myMapView.displayZoomControls(false);

		String mapKind = mission.xmlMissionNode.attributeValue("mapkind");
		if (mapKind == null || mapKind.equals("map"))
			myMapView.setSatellite(false);
		else
			myMapView.setSatellite(true);

		mapHelper = new MapHelper(this);
		mapHelper.centerMap();

		initZoom();
		initGPSMock();

		// Players Location Overlay
		List<Overlay> mapOverlays = myMapView.getOverlays();
		myLocationOverlay = new MyLocationOverlay(this, myMapView);
		myLocationOverlay.enableCompass(); // doesn't work in the emulator?
		myLocationOverlay.enableMyLocation();
		mapOverlays.add(myLocationOverlay);

		for (Iterator<Hotspot> iterator = getHotspots().iterator(); iterator
				.hasNext();) {
			Hotspot hotspot = (Hotspot) iterator.next();
			mapOverlays.add(hotspot.getGoogleOverlay());
		}

		GeoQuestApp.getInstance().setGoogleMap(myMapView);
		mission.applyOnStartRules();

	}

	/**
	 * called by the android framework when the activity gets inactive. Disables
	 * the myLocationOverlay listeners.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
	}

	/**
	 * called by the android framework when the activity gets active. Registers
	 * the myLocationOverlay listeners.
	 */
	@Override
	protected void onDestroy() {
		if (myLocationManager != null)
			myLocationManager.removeUpdates(mapHelper.getLocationListener());
		GeoQuestApp.getInstance().setGoogleMap(null);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
	}

	/**
	 * On click listener to start the mission from a hotspot when the user taps
	 * on the corresponding button
	 */
	public class StartMissionOnClickListener implements OnClickListener {

		public void onClick(View v) {
			Hotspot h = (Hotspot) v.getTag();
			h.runOnTapEvent();
		}

	}

	/** Intent used to return values to the parent mission */
	protected Intent result;

	@Override
	public IMapView getMapView() {
		return new org.osmdroid.google.wrapper.MapView(myMapView);
	}

	@Override
	public IMapController getMapController() {
		return new org.osmdroid.google.wrapper.MapController(
				myMapView.getController());
	}

}