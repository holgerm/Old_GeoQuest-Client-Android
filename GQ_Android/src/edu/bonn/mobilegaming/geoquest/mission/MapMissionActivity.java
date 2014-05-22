package edu.bonn.mobilegaming.geoquest.mission;

import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.loc.MapHelper;
import com.qeevee.util.locationmocker.LocationSource;

import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public abstract class MapMissionActivity extends GeoQuestMapActivity {

	protected MissionOrToolUI ui;

	public MissionOrToolUI getUI() {
		return ui;
	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO move into UI

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (getUI() != null)
			getUI().release();
	}

	private IMapView mapView;
	private IMapController mapController;

	public IMapView getMapView() {
		return mapView;
	}

	public void setMapView(IMapView mapView) {
		this.mapView = mapView;
	}

	public IMapController getMapController() {
		return this.mapController;
	}

	public void setMapController(IMapController mapController) {
		this.mapController = mapController;
	}

	/**
	 * list of hotspots, inited in readxml. main thread may not access this
	 * until readxml_completed is true
	 * */
	protected MapHelper mapHelper;
	protected LocationManager myLocationManager;
	protected Handler handler = new Handler();
	protected LocationSource locationSource;
	protected Mission mission;
	protected int zoomLevelInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(this.getClass().getName(), "creating activity");
		super.onCreate(savedInstanceState);
		initMission();
	}

	private void initMission() {
		mission = Mission.get(id);
		mission.setStatus(Globals.STATUS_RUNNING);
	}

	public List<Hotspot> getHotspots() {
		return HotspotManager.getInstance().getListOfHotspots();
	}

	protected void initZoom() {
		getMapController().setZoom(18);
		String zoomLevel = mission.xmlMissionNode.attributeValue("zoomlevel");
		if (zoomLevel != null) {
			zoomLevelInt = Integer.parseInt(zoomLevel);
			if (zoomLevelInt > 0 && zoomLevelInt < 24)
				getMapController().setZoom(zoomLevelInt);
		}

		// Setup Zoom Controls:
		Button zoomIn = (Button) findViewById(R.id.zoom_in);
		zoomIn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMapController().zoomIn();
			}
		});

		Button zoomOut = (Button) findViewById(R.id.zoom_out);
		zoomOut.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMapController().zoomOut();
			}
		});

	}

	protected void initGPSMock() {
		try {
			long timeStepMockMode = Long.parseLong(getText(
					R.string.map_mockGPSTimeInterval).toString());
			locationSource = new LocationSource(getApplicationContext(),
					mapHelper.getLocationListener(), handler, timeStepMockMode);
			locationSource.setMode(LocationSource.REAL_MODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finishes the mission activity and sets the result code
	 * 
	 * @param status
	 *            Mission.STATUS_SUCCESS or FAIL or NEW
	 */
	public void finish(Double status) {
		mission.setStatus(status);
		mission.applyOnEndRules();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_navigation, menu);
		// TODO add mocking menu only when attribute set in prefs
		inflater.inflate(R.menu.menu_navigation_mocking, menu);
		return true;
	}

	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_mockGPS:
			if (locationSource.getMode() == LocationSource.REAL_MODE) {
				// From REAL mode to MOCK mode:
				locationSource.setMode(LocationSource.MOCK_MODE);
				item.setTitle(R.string.menu_unmockGPS);
			} else {
				// From MOCK mode to REAL mode:
				locationSource.setMode(LocationSource.REAL_MODE);
				item.setTitle(R.string.menu_mockGPS);
			}
			break;
		case R.id.menu_center:
			mapHelper.centerMap();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Called right before your activity's option menu is displayed.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_mockGPS).setEnabled(
				locationSource != null
						&& LocationSource.canBeUsed(getApplicationContext()));
		return true;
	}

}
