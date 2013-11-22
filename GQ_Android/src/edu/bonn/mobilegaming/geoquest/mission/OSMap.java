package edu.bonn.mobilegaming.geoquest.mission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.qeevee.util.location.MapHelper;
import com.qeevee.util.locationmocker.LocationSource;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
import edu.bonn.mobilegaming.geoquest.HotspotListener;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.UIFactory;

/**
 * OpenStreetMap-based Map Navigation.
 */
public class OSMap extends MapNavigation implements HotspotListener {

	private static String TAG = "OSMap";

	// set these two parameters to use Cloudmade Style
	private static String APIKey = null; // eg
	// "6f218baf0ee44fdc9a9563c37e55851e"
	private static String CmStyleId = null; // eg "63694"

	// Menu IDs:
	static final private int FIRST_LOCAL_MENU_ID = GeoQuestMapActivity.MENU_ID_OFFSET;
	static final private int LOCATION_MOCKUP_SWITCH_ID = FIRST_LOCAL_MENU_ID;
	static final private int ZOOM_TO_BOUNDING_BOX = FIRST_LOCAL_MENU_ID + 1;
	static final private int CENTER_MAP_ON_CURRENT_LOCATION_ID = FIRST_LOCAL_MENU_ID + 2;

	private TilesOverlay tilesOverlay;

	/**
	 * used by the android framework
	 * 
	 * @return false
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public TilesOverlay getCustomTilesOverlay() {
		return this.tilesOverlay;
	}

	/**
	 * Called when the activity is first created. Setups google mapView, the map
	 * overlays and the listeners
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup Layout:
		ui = UIFactory.getInstance().createUI(this);

		initMapTileAccess();

		mapHelper = new MapHelper(this);

		initZoom();
		initGPSMock();

		mission.applyOnStartRules();
	}

	public MapHelper getMapHelper() {
		return mapHelper;
	}

	private void initMapTileAccess() {
		MapView mapView = (MapView) getMapView();
		if (APIKey != null && CmStyleId != null) {
			mapView.setTileSource(new XYTileSource("cmMap", null, 0, 15, 256,
					".png", "http://tile.cloudmade.com/" + APIKey + "/"
							+ CmStyleId + "/256/"));
		}
		// handling local custom maptiles here
		String localTilePath = GeoQuestApp.getRunningGameDir()
				.getAbsolutePath() + "/customTiles/";
		File tileFileSrc = new File(localTilePath + "customTiles.zip");
		if (tileFileSrc.exists()) {
			File tileFileDest = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/osmdroid/customTiles.zip");
			File tileFolder = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/osmdroid/");
			tileFolder.mkdirs();

			try {
				copyFile(tileFileSrc, tileFileDest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			XYTileSource myTileSource = new XYTileSource("customTiles", null,
					1, 18, 256, ".png");
			final MapTileProviderBasic tileProvider = new MapTileProviderBasic(
					getApplicationContext());
			tileProvider.setTileSource(myTileSource);
			tilesOverlay = new TilesOverlay(tileProvider, this);
			tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
			tilesOverlay.setUseDataConnection(false);
			mapView.getOverlays().add(tilesOverlay);
		}
	}

	private void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * called by the android framework when the activity gets inactive. Disables
	 * the myLocationOverlay listeners.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		ui.disable();
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

		// delete customTile.zip in osmdroid folder
		File tileFileDest = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/osmdroid/customTiles.zip");
		if (tileFileDest.exists())
			tileFileDest.delete();

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ui.enable();
	}

	private void zoomToBoundingBox() {
		MapView mapView = (MapView) getMapView();

		// TODO check if good; evtl. filter inactive hotspots ...
		ArrayList<GeoPoint> hotspotPoints = new ArrayList<GeoPoint>();
		com.google.android.maps.GeoPoint curHotspotGP;
		for (int i = 0; i < getHotspots().size(); i++) {
			curHotspotGP = getHotspots().get(i).getPosition();
			hotspotPoints.add(new GeoPoint(curHotspotGP.getLatitudeE6(),
					curHotspotGP.getLongitudeE6()));
		}
		BoundingBoxE6 boundingBox = BoundingBoxE6.fromGeoPoints(hotspotPoints);
		mapView.zoomToBoundingBox(boundingBox);
	}

	/**
	 * Called when the activity's options menu needs to be created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, LOCATION_MOCKUP_SWITCH_ID, 0, R.string.map_menu_mockGPS);
		if (getHotspots().size() > 0)
			menu.add(0, ZOOM_TO_BOUNDING_BOX, 0, R.string.map_menu_bounding_box);
		menu.add(0, CENTER_MAP_ON_CURRENT_LOCATION_ID, 0,
				R.string.map_menu_centerMap);
		return true;
	}

	/**
	 * Called right before your activity's option menu is displayed.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.getItem(LOCATION_MOCKUP_SWITCH_ID).setEnabled(
				locationSource != null
						&& LocationSource.canBeUsed(getApplicationContext()));
		return true;
	}

	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case LOCATION_MOCKUP_SWITCH_ID:
			if (locationSource.getMode() == LocationSource.REAL_MODE) {
				// From REAL mode to MOCK mode:
				locationSource.setMode(LocationSource.MOCK_MODE);
				item.setTitle(R.string.map_menu_realGPS);
			} else {
				// From MOCK mode to REAL mode:
				locationSource.setMode(LocationSource.REAL_MODE);
				item.setTitle(R.string.map_menu_mockGPS);
			}
			break;
		case CENTER_MAP_ON_CURRENT_LOCATION_ID:
			mapHelper.centerMap();
			break;
		case ZOOM_TO_BOUNDING_BOX:
			zoomToBoundingBox();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * On click listener to start the mission from a hotspot when the user taps
	 * on the corresponding button
	 */
	public class StartMissionOnClickListener implements OnClickListener {

		public void onClick(View v) {
			HotspotOld h = (HotspotOld) v.getTag();
			h.runOnTapEvent();
		}

	}

	/**
	 * Hotspot listener method. Is called when the player enters a hotspots
	 * interaction circle. A button to start the mission from the hotspot is
	 * shown.
	 */
	public void onEnterRange(HotspotOld h) {
		Log.d(TAG, "Enter Hotspot with id: " + h.id);
	}

	/**
	 * Hotspot listener method. Is called when the player leaves a hotspots
	 * interaction circle. The button to start the mission of the hotspot
	 * dislodged from the view.
	 */
	public void onLeaveRange(HotspotOld h) {
		Log.d(TAG, "Leave Hotspot with id: " + h.id);
	}

	/** Intent used to return values to the parent mission */
	protected Intent result;

}