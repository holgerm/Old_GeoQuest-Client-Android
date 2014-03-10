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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.qeevee.util.location.MapHelper;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Hotspot;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;

/**
 * OpenStreetMap-based Map Navigation.
 */
public class MapOSM extends MapNavigation {

	private static String TAG = MapOSM.class.getCanonicalName();

	// set these two parameters to use Cloudmade Style
	private static String APIKey = null; // eg
	// "6f218baf0ee44fdc9a9563c37e55851e"
	private static String CmStyleId = null; // eg "63694"

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
					".png", new String[] { "http://tile.cloudmade.com/"
							+ APIKey + "/" + CmStyleId + "/256/" }));
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
			XYTileSource myTileSource = new XYTileSource(
					"customTiles",
					null,
					1,
					18,
					256,
					".png",
					new String[] { "http://a.tile.openstreetmap.org/{z}/{x}/{y}.png" });
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

	private void zoomToQuestArea() {
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_map_osm, menu);
		return true;
	}

	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_ZoomToQuestArea:
			zoomToQuestArea();
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
			Hotspot h = (Hotspot) v.getTag();
			h.runOnTapEvent();
		}

	}

	/** Intent used to return values to the parent mission */
	protected Intent result;

}