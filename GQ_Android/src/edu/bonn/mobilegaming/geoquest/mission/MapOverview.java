package edu.bonn.mobilegaming.geoquest.mission;

import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.qeevee.util.location.MapHelper;
import com.qeevee.util.locationmocker.LocationSource;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.HotspotListener;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
import edu.bonn.mobilegaming.geoquest.R;

/**
 * MapOverview mission. Based on the google map view a map view is shown in the
 * background. On the mapview Hotspots are drawn and there is a simple score
 * view, a button to change the navigation type and buttons to start the
 * missions that are in range of the current location.
 * 
 * @author Krischan Udelhoven
 * @author Folker Hoffmann
 */
public class MapOverview extends MapNavigation implements HotspotListener {

	private static String TAG = "MapOverview";

	private MapView myMapView;
	private MyLocationOverlay myLocationOverlay;

	private LinearLayout startMissionPanel;

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

		// startMissionsList
		startMissionPanel = (LinearLayout) findViewById(R.id.startMissionPanel);

		// Players Location Overlay
		myLocationOverlay = new MyLocationOverlay(this, myMapView);
		myLocationOverlay.enableCompass(); // doesn't work in the emulator?
		myLocationOverlay.enableMyLocation();
		myMapView.getOverlays().add(myLocationOverlay);

		GeoQuestApp.getInstance().setGoogleMap(myMapView);

		// Show loading screen to Parse the Game XML File
		// indirectly calls onCreateDialog() and initializes hotspots
		showDialog(READXML_DIALOG);

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

		/*
		 * TODO: remove buttons ? //TODO: also add button when visible conditons
		 * are fulfilled and player was already in range
		 * if(h.visibleConditionsFulfilled()){ Button b = new Button(this);
		 * 
		 * b.setText(h.id);// TODO: text on button should not be the id //
		 * b.setBackgroundColor(Color.argb(180, 0, 0, 0)); //Teiltransparentes
		 * Schwarz b.setTextColor(Color.BLACK); b.setTextSize(18);
		 * b.setGravity(Gravity.CENTER); b.setTag(h); // Hotspot speichern
		 * b.setOnClickListener(myMissionOnClickListener);
		 * b.setVisibility(View.VISIBLE); startMissionPanel.addView(b); }
		 */
	}

	/**
	 * Hotspot listener method. Is called when the player leaves a hotspots
	 * interaction circle. The button to start the mission of the hotspot
	 * dislodged from the view.
	 */
	public void onLeaveRange(HotspotOld h) {
		Log.d(TAG, "Leave Hotspot with id: " + h.id);
		// Find the Child, which equals the given hotspot:
		int numButtons = startMissionPanel.getChildCount();
		View childView = null;
		for (int i = 0; i < numButtons; i++) {
			if (startMissionPanel.getChildAt(i).getTag().equals(h)) {
				childView = startMissionPanel.getChildAt(i);
				break;
			}
		}
		// Remove this child:
		if (childView != null) {
			startMissionPanel.removeView(childView);
		}

	}

	static final int READXML_DIALOG = 0;
	ProgressDialog readxmlDialog;
	ReadxmlThread readxmlThread;
	boolean readxml_completed = false;// true when xml is parsed completely.

	// while false main thread may not
	// access 'hotspots'

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case READXML_DIALOG:
			readxmlDialog = new ProgressDialog(this);
			readxmlDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			readxmlDialog.setMessage(getString(R.string.map_loading));
			readxmlThread = new ReadxmlThread(readxmlHandler);
			readxmlThread.start();
			return readxmlDialog;
		default:
			return null;
		}
	}

	/**
	 * Define the Handler that receives messages from the thread and update the
	 * progressbar
	 */
	final Handler readxmlHandler = new Handler() {
		public void handleMessage(Message msg) {
			int progress = msg.getData().getInt("progress");
			int max = msg.getData().getInt("max");
			boolean finish = msg.getData().getBoolean("finish");

			if (progress != 0)
				readxmlDialog.setProgress(progress);
			if (max != 0)
				readxmlDialog.setMax(max);

			if (finish) {
				// new hotspots were not added to myMapView.getOverlays();
				// this would cause a crash in nonmain thread; so this is done
				// here
				List<Overlay> mapOverlays = myMapView.getOverlays();
				for (Iterator<HotspotOld> iterator = getHotspots().iterator(); iterator
						.hasNext();) {
					HotspotOld hotspot = (HotspotOld) iterator.next();
					mapOverlays.add(hotspot.getGoogleOverlay());
				}
				// mapOverlays.addAll(hotspots);

				dismissDialog(READXML_DIALOG);
				readxml_completed = true;
			}
		}
	};

	/** Nested class that performs reading xml */
	private class ReadxmlThread extends Thread {
		Handler mHandler;

		ReadxmlThread(Handler h) {
			mHandler = h;
		}

		public void run() {

			try {
				readXML();
			} catch (DocumentException e) {
				e.printStackTrace();
				Log.e("Error", "XML Error");
			}

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putBoolean("finish", true);
			msg.setData(b);
			mHandler.sendMessage(msg);

			if (locationSource != null)
				locationSource.setMode(LocationSource.REAL_MODE);

		}

		/**
		 * Gets the child Hotspots data from the XML file.
		 */
		@SuppressWarnings("unchecked")
		private synchronized void readXML() throws DocumentException {
			List<Element> list = mission.xmlMissionNode
					.selectNodes("hotspots/hotspot");

			int j = 0;
			for (Iterator<Element> i = list.iterator(); i.hasNext();) {
				Element hotspot = i.next();
				try {
					HotspotOld newHotspot = HotspotOld.create(mission, hotspot);
					newHotspot.addHotspotListener(MapOverview.this);
					getHotspots().add(newHotspot);
					// new hotspots are not added to myMapView.getOverlays();
					// this would course a crash in nonmain thread;
					// readxmlHandler will add them later
				} catch (HotspotOld.IllegalHotspotNodeException exception) {
					Log.e("MapOverview.readXML", exception.toString());
				}

				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("progress", ++j);
				b.putInt("max", list.size());
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
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