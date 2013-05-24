package edu.bonn.mobilegaming.geoquest.mission;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qeevee.util.location.MapHelper;
import com.qeevee.util.locationmocker.LocationSource;

import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.HotspotListener;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public abstract class MapNavigation extends GeoQuestMapActivity implements
		HotspotListener {

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO move into UI

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public abstract IMapView getMapView();

	public abstract IMapController getMapController();

	/**
	 * list of hotspots, inited in readxml. main thread may not access this
	 * until readxml_completed is true
	 * */
	private List<HotspotOld> hotspots = new ArrayList<HotspotOld>();
	protected MapHelper mapHelper;
	protected LocationManager myLocationManager;
	protected Handler handler = new Handler();
	protected LocationSource locationSource;
	protected Mission mission;

	public void onEnterRange(HotspotOld h) {
		// TODO obsolete?
	}

	public void onLeaveRange(HotspotOld h) {
		// TODO obsolete?
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(this.getClass().getName(), "creating activity");
		super.onCreate(savedInstanceState);
		initMission();
	}

	private void initMission() {
		Bundle extras = getIntent().getExtras();
		String id = extras.getString("missionID");
		mission = Mission.get(id);
		mission.setStatus(Globals.STATUS_RUNNING);
	}

	public List<HotspotOld> getHotspots() {
		return hotspots;
	}

	protected void initZoom() {
		getMapController().setZoom(18);
		String zoomLevel = mission.xmlMissionNode.attributeValue("zoomlevel");
		if (zoomLevel != null) {
			int zoomLevelInt = Integer.parseInt(zoomLevel);
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

	/**
	 * Back button Handler quits the Mission, when back button is hit.
	 */
	@Override
	public boolean onKeyDown(final int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK: // Back => Cancel
			if (mission.cancelStatus == 0) {
				Log.d(this.getClass().getName(),
						"Back Button was pressed, but mission may not be cancelled.");
				return true;
			} else {
				finish(mission.cancelStatus);
				return true;
			}
		case KeyEvent.KEYCODE_SEARCH:
			// ignore search button
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
