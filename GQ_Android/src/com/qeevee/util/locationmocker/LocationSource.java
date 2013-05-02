package com.qeevee.util.locationmocker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.qeevee.util.location.SmartLocationListener;

/**
 * @author hm
 * 
 */
public class LocationSource {

	private static final String TAG = "com.qeevee.util.locationmocker.LocationSource";

	public static final int REAL_MODE = 0;
	public static final int MOCK_MODE = 1;

	private static final String LOCATION_MOCK_SERVER_URL = "http://geoquest.qeevee.org/control/";

	private static URL makeURLForGetPosition(Context context) {
		URL url = null;
		try {
			url = new URL(LOCATION_MOCK_SERVER_URL + "?get=1&id="
					+ getDeviceID(context));
		} catch (MalformedURLException e) {
			Log.e(TAG, "makeURLForGetPosition() constructed this url: "
					+ LOCATION_MOCK_SERVER_URL + "?get=1&id="
					+ getDeviceID(context));
		}
		return url;
	}

	public static boolean setDeviceName(Context context, String deviceName) {
		String deviceID = getDeviceID(context);
		StringBuilder responseString = new StringBuilder();
		try {
			URL url = new URL(LOCATION_MOCK_SERVER_URL + "?set=1&id="
					+ deviceID + "&name=" + deviceName);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String response = responseString.toString();
		return (response.startsWith("ok " + deviceID) && response
				.endsWith(deviceName));
	}

	private LocationManager locationManager;
	private int mode = REAL_MODE;
	private MockLocationReceiver mockLocationReceiver;

	private SmartLocationListener locationListener;
	private URL url;
	private Handler handler;
	private long timeStepMockMode;

	/**
	 * @param context
	 *            , e.g. the current activity or application
	 * @return {@code true} if you can currently use location mocking on your
	 *         device, {@code else} otherwise.
	 */
	public static boolean canBeUsed(Context context) {
		boolean result = false;
		try {
			result = (Settings.Secure.getInt(context.getContentResolver(),
					Settings.Secure.ALLOW_MOCK_LOCATION) != 0);
		} catch (SettingNotFoundException snfX) {
			// should not occur
			snfX.printStackTrace();
		}
		return result;
	}

	/**
	 * LocationSource starts in REAL_MODE, i.e. with the best location provider
	 * it currently gets.
	 * 
	 * @param context
	 * @param locationListener
	 *            interface providing call backs e.g. onLocationChanged() which
	 *            is called when appropriate
	 * @throws SecurityException
	 *             if the permissions to use mock location is not set.
	 * @throws IllegalArgumentException
	 *             if the Mock Location Provider already has been set.
	 */
	public LocationSource(Context context,
			SmartLocationListener locationListener, Handler handler,
			long timeStepMockMode) throws SecurityException,
			IllegalArgumentException {
		this.handler = handler;
		this.timeStepMockMode = timeStepMockMode;
		if (locationListener != null) {
			this.locationListener = locationListener;
		} else {
			this.locationListener = new DummyLocationListener(context);
		}
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		try {
			if (!locationManager
					.isProviderEnabled(MockLocationReceiver.MOCK_PROVIDER)) {
				locationManager.addTestProvider(
						MockLocationReceiver.MOCK_PROVIDER, true, false, false,
						false, false, false, false, Criteria.POWER_LOW,
						Criteria.ACCURACY_FINE);
			}
		} catch (SecurityException e) {
			Log.i(TAG,
					"SecurityException thrown when switching to mock location.");
		}

		this.url = makeURLForGetPosition(context);

		this.mockLocationReceiver = new MockLocationReceiver();
	}

	/**
	 * Disconnects the attached location listener from the location manager.
	 */
	public void disconnect() {
		locationManager.removeUpdates(locationListener);
	}

	private class DummyLocationListener extends SmartLocationListener {

		public DummyLocationListener(Context context) {
			super(context);
		}

		@Override
		protected void onRelevantLocationChanged(Location newLocation) {
		}

	}

	public static String getDeviceID(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	/**
	 * Each switch to REAL_MODE reinitializes the location manager so that it
	 * always looks for the best provider (GPS, CELL etc.)
	 * 
	 * @param newMode
	 *            either REAL_MODE or MOCK_MODE
	 * @param forceExecution
	 *            perform detailed action even if newMode equals current mode.
	 *            E.g. used to trigger location update at start of MapOverview
	 *            mission.
	 * @return
	 */
	public int setMode(int newMode) {
		if (newMode == this.mode)
			return this.mode;
		// rest only happens if mode really switches:

		this.mode = newMode;

		switch (newMode) {
		case REAL_MODE:
			Log.i(TAG, "switch to Real mode");
			mockLocationReceiver.keepMocking = false;
			locationManager.setTestProviderEnabled(
					MockLocationReceiver.MOCK_PROVIDER, false);
			locationManager
					.clearTestProviderEnabled(MockLocationReceiver.MOCK_PROVIDER);
			locationManager
					.clearTestProviderLocation(MockLocationReceiver.MOCK_PROVIDER);
			locationListener.connect();
			break;
		case MOCK_MODE:
			Log.i(TAG, "switch to MOCK mode");
			locationListener.disconnect();
			locationManager.setTestProviderEnabled(
					MockLocationReceiver.MOCK_PROVIDER, true);
			// locationManager.setTestProviderEnabled(
			// LocationManager.GPS_PROVIDER, true);
			mockLocationReceiver.resume();
			handler.post(mockLocationReceiver);
			break;
		default:
			throw new IllegalArgumentException(
					"No such location source mode defined: " + newMode);
		}
		return newMode;
	}

	public int getMode() {
		return this.mode;
	}

	private class MockLocationReceiver implements Runnable {
		boolean keepMocking = false;
		boolean firstRunSinceModeActivated = true;

		// initialize step-by-step-store with pretty unrealistic values, that's
		// fine.
		private double oldLatitude = 0.0d;
		private double oldLongitude = 0.0d;

		final static String MOCK_PROVIDER = "LOCATION_MOCK_PROVIDER";

		public void run() {
			// finish mock location receiver if keepMocking is false:
			if (!keepMocking)
				return;
			try {
				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						url.openStream()), 100);
				StringBuilder responseString = new StringBuilder();
				String line = "";
				while ((line = rd.readLine()) != null) {
					responseString.append(line);
				}
				rd.close();
				Location newLocation = null;
				if (!responseString.equals("")) {
					newLocation = parseLocationResultString(responseString
							.toString());
				}
				// just in case in the meantime the user changed mode:
				if (!keepMocking)
					return;
				// put data into system:
				if ((newLocation != null)
						&& (firstRunSinceModeActivated
								|| oldLatitude != newLocation.getLatitude() || oldLongitude != newLocation
								.getLongitude())) {

					locationManager.setTestProviderLocation(MOCK_PROVIDER,
							newLocation);

					// store current location as "old" for next time:
					oldLatitude = newLocation.getLatitude();
					oldLongitude = newLocation.getLongitude();
					firstRunSinceModeActivated = false;

					// post the new location to the model:
					locationListener.pushExternalLocation(newLocation);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// TODO use Logging here (but ont only here)
			}
			// keep Mocking, if still wanted:
			if (keepMocking)
				handler.postDelayed(this, timeStepMockMode);
		}

		/**
		 * Assumed is a response string of the form "latitude;longitude", e.g.
		 * "50.961346054560266;7.004384994506836".
		 * 
		 * @param responseString
		 * @return null in case responseString does not contain enough values.
		 */
		private Location parseLocationResultString(String responseString) {
			Location newLocation = new Location(MOCK_PROVIDER);
			String[] data = TextUtils.split(responseString, ";");
			if (data.length < 2)
				return null;
			newLocation.setLatitude(Double.parseDouble(data[0]));
			newLocation.setLongitude(Double.parseDouble(data[1]));
			newLocation.setTime(System.currentTimeMillis());
			return newLocation;
		}

		void resume() {
			keepMocking = true;
			firstRunSinceModeActivated = true;
		}
	}

}
