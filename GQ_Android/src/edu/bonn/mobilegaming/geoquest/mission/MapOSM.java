package edu.bonn.mobilegaming.geoquest.mission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osmdroid.api.IMapView;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.loc.MapHelper;
import com.qeevee.gq.loc.Route;
import com.qeevee.gq.loc.RouteManager;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;

/**
 * OpenStreetMap-based Map Navigation.
 */
public class MapOSM extends MapMissionActivity {

	private static String TAG = MapOSM.class.getCanonicalName();

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

		mapHelper = new MapHelper(this); // activates Hotspots

		initZoom();
		initGPSMock();

		((MapView) mapView).getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					public void onGlobalLayout() {
						mission.applyOnStartRules();
						// do this only once, since onGlobalLayout might be
						// called often:
						((MapView) mapView).getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		mission.applyOnStartRules();

		// create overlay of all existing routes
		addAllRoutesToMap();
	}

	private void addAllRoutesToMap() {
		List<Route> routes = RouteManager.getInstance().getListOfRoutes();
		for (Route route : routes) {
			addRouteToMap(route);
		}
	}

	public void addRouteToMap(Route route) {
		if (route.getRoad() == null)
			return;
		MapView mapView = (MapView) getMapView();
		route.setRoadOverlay(RoadManager.buildRoadOverlay(route.getRoad(),
				route.getColor(), route.getWidth(), this));
		mapView.getOverlays().add(route.getRoadOverlay());
		mapView.postInvalidate();
	}

	public void removeRouteFromMap(Route route) {
		MapView mapView = (MapView) getMapView();
		mapView.getOverlays().remove(route.getRoadOverlay());
		mapView.postInvalidate();
	}

	public MapHelper getMapHelper() {
		return mapHelper;
	}

	private void initMapTileAccess() {
		MapView mapView = (MapView) getMapView();
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
		GeoQuestApp.getInstance().setOSMap(null);

		// delete customTile.zip in osmdroid folder
		File tileFileDest = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/osmdroid/customTiles.zip");
		if (tileFileDest.exists())
			tileFileDest.delete();

		IMapView iMapView = getMapView();
		if (iMapView instanceof MapView) {
			((MapView) getMapView()).getTileProvider().clearTileCache();
			Log.d(TAG, "tile cache cleared");
		}
		System.gc();

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ui.enable();

		((MapView) mapView).getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					public void onGlobalLayout() {
						updateZoom();
						// do this only once, since onGlobalLayout might be
						// called often:
						((MapView) mapView).getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	public void updateZoom() {
		ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
		if (Variables.getValue(Variables.CENTER_MAP_POSITION).equals("true")
				|| Variables.getValue(Variables.CENTER_MAP_POSITION)
						.equals("1")) {
			LocationManager mLocationManager = (LocationManager) GeoQuestApp
					.getContext().getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = mLocationManager.getBestProvider(crit, true);
			Location location = mLocationManager.getLastKnownLocation(provider);
			if (location != null)
				points.add(new GeoPoint(location));
		}
		if (Variables.getValue(Variables.CENTER_MAP_ACTIVE_HOTSPOTS).equals(
				"true")
				|| Variables.getValue(Variables.CENTER_MAP_ACTIVE_HOTSPOTS)
						.equals("1")) {
			points.addAll(HotspotManager.getInstance()
					.getGeoPointsOfActiveHotspots());
		}
		if (Variables.getValue(Variables.CENTER_MAP_VISIBLE_HOTSPOTS).equals(
				"true")
				|| Variables.getValue(Variables.CENTER_MAP_VISIBLE_HOTSPOTS)
						.equals("1")) {
			Collection<GeoPoint> visiblePoints = HotspotManager.getInstance()
					.getGeoPointsOfVisibleHotspots();
			for (GeoPoint curPoint : visiblePoints) {
				if (!points.contains(curPoint))
					points.add(curPoint);
			}
		}
		zoomToQuestArea(points);
	}

	private void zoomToQuestArea() {
		ArrayList<GeoPoint> hotspotPoints = new ArrayList<GeoPoint>();
		com.google.android.maps.GeoPoint curHotspotGP;
		for (int i = 0; i < getHotspots().size(); i++) {
			curHotspotGP = getHotspots().get(i).getGeoPoint();
			hotspotPoints.add(new GeoPoint(curHotspotGP.getLatitudeE6(),
					curHotspotGP.getLongitudeE6()));
		}
		zoomToQuestArea(hotspotPoints);
	}

	private void zoomToQuestArea(ArrayList<GeoPoint> hotspotPoints) {
		if (hotspotPoints == null || hotspotPoints.size() == 0)
			return;
		MapView mapView = (MapView) getMapView();
		if (hotspotPoints.size() == 1) {
			mapView.getController().setZoom(zoomLevelInt);
			mapView.getController().animateTo(hotspotPoints.get(0));
			// setCenter(hotspotPoints.get(0));
		} else {
			BoundingBoxE6 boundingBox = BoundingBoxE6
					.fromGeoPoints(hotspotPoints);
			mapView.zoomToBoundingBox(boundingBox);
		}
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