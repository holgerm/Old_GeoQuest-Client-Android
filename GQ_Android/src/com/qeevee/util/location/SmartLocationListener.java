package com.qeevee.util.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class SmartLocationListener implements LocationListener {
	@SuppressWarnings("unused")
	private static final String TAG = SmartLocationListener.class
			.getSimpleName();
	private LocationManager locationManager;

	public SmartLocationListener(Context context) {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		connect();
	}

	private void triggerCallbackWithLastKnownLocation() {
		Location firstLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (firstLocation != null)
			onLocationChanged(firstLocation);
		else {
			firstLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (firstLocation != null)
				onLocationChanged(firstLocation);
		}
	}

	public void connect() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
		if (lastLocation == null)
			triggerCallbackWithLastKnownLocation();
		else
			pushLastLocationAgain();
	}

	public void disconnect() {
		locationManager.removeUpdates(this);
	}

	private Location lastLocation;

	public final Location getLastLocation() {
		return lastLocation;
	}

	/**
	 * You can not overwrite this method as you might expect. It is used to
	 * implement a filter for relevant location changes. In order to implement
	 * your business logic triggered by relevant location changes.
	 * 
	 * @see #onLocationChangedRelevantly()
	 */
	public final void onLocationChanged(Location newLoc) {
		if (lastLocation == null) {
			storeNewLocation(newLoc);
			return;
		}
		double lastAcc = lastLocation.getAccuracy();
		double newAcc = newLoc.getAccuracy();
		if (newAcc < lastAcc) {
			storeNewLocation(newLoc);
			return;
		}
		double dist = 1000 * Distance.distance(lastLocation.getLatitude(),
				lastLocation.getLongitude(), newLoc.getLatitude(),
				newLoc.getLongitude());
		if (dist + lastAcc <= newAcc) {
			onIgnoredLocationChange(newLoc);
		} else {
			storeNewLocation(newLoc);
		}
	}

	private void pushLastLocationAgain() {
		storeNewLocation(lastLocation);
	}

	public void pushExternalLocation(Location externalLocation) {
		onRelevantLocationChanged(externalLocation);
	}

	private void storeNewLocation(Location newLoc) {
		lastLocation = newLoc;
		onRelevantLocationChanged(newLoc);
	}

	/**
	 * This method is called when relevant new location data has been received.
	 * Implement your business logic herein to take action with the new location
	 * data.
	 * 
	 * @param newLocation
	 *            the new location object.
	 */
	protected abstract void onRelevantLocationChanged(Location newLocation);

	/**
	 * This method is called when this location listener has received new
	 * location data object but regarded it as irrelevant. This is the case when
	 * the new location circle encloses the previously stored location circle
	 * completely. I.e. the new location data is merely less precise than the
	 * older had been. This might be because after a GPS fix a network-based
	 * location object has been received, too.
	 * 
	 * @param ignoredLocation
	 *            the location object considered irrelvant.
	 */
	protected void onIgnoredLocationChange(Location ignoredLocation) {
	}

	/**
	 * Additionally to the super class method this method is called when the
	 * following events / status changes occur:
	 * 
	 * <ul>
	 * <li>TimeOut: when the last location data received is older than the given
	 * time out this method is called with status TIMED_OUT.</li>
	 * <li>TimeIn: given a timed out state: when a new location data is
	 * received, this method is called with status TIMED_IN.</li>
	 * <li>Accuracy Low: when a relevant new location data has been received
	 * whose accuracy is below the given threshold this method is called with
	 * status ACCURACY_LOW.</li>
	 * <li>Accuracy OK: given low accuracy, when a new relevant location data is
	 * received with an accuracy higher than the given threshold this method is
	 * called with status ACCURACY_OK.</li>
	 * </ul>
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 *      int, android.os.Bundle)
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}

}
