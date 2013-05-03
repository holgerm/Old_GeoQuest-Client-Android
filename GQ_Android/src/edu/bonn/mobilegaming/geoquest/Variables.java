package edu.bonn.mobilegaming.geoquest;

import java.util.HashMap;
import java.util.Map;

import android.location.Location;

import com.qeevee.util.location.LocationUtilities;

public class Variables {

	private static Map<String, Object> variables = new HashMap<String, Object>();
	/**
	 * use this constant for defining the key of the mission status variable,
	 * e.g.
	 * <code>setValue(<mission id> + STATUS_SUFFIX, Globals.STATUS_SUCCESS);</code>
	 */
	public static final String STATUS_SUFFIX = ".state";

	/**
	 * use this constant for defining the key of the mission result variable,
	 * e.g.
	 * <code>setValue(<mission id> + RESULT_SUFFIX, some_result_value);</code>
	 * It is used for example to store the last scan result of QRTagReading
	 * missions but could also be used for QuestionAndAnswer mission.
	 */
	public static final String RESULT_SUFFIX = ".result";
	/**
	 * use this constant for defining the key of the hotspot visibility
	 * variable, e.g.
	 * <code>getValue(Variables.HOTSPOT_PREFIX + 1234 + VISIBLE_SUFFIX);</code>
	 */
	public static final String VISIBLE_SUFFIX = ".visible";

	/**
	 * use this constant for defining the key of hotspot related variables, e.g.
	 * <code>getValue(Variables.HOTSPOT_PREFIX + 1234 + VISIBLE_SUFFIX);</code>
	 * 
	 * TODO add '$_' before, so that all system variables start with '$_'.
	 */
	public static final String HOTSPOT_PREFIX = "_hotspot-";

	/**
	 * use this constant for defining the key of the hotspot location variable,
	 * e.g.
	 * <code>setValue(Variables.HOTSPOT_PREFIX + 1234 + LOCATION_SUFFIX, new GeoPoint(50,3)));
	 */
	public static final String LOCATION_SUFFIX = ".location";

	public static final String SYSTEM_PREFIX = "$_";
	public static final String LOCATION_LAT = SYSTEM_PREFIX + "location.lat";
	public static final String LOCATION_LONG = SYSTEM_PREFIX + "location.long";

	/**
	 * Checks if the given key exists in the variables hashmap.
	 * 
	 * @param varName
	 *            [HOTSPOT_PREFIX]{id}{SUFFIX}
	 * @return
	 */
	public static boolean isDefined(String varName) {
		return variables.containsKey(varName);
	}

	/**
	 * Returns the value for a given key, i.e. variable name.
	 * 
	 * All variable names are trimmed strings, i.e. leading and trailing
	 * whitespaces are removed inside the get and set methods here. Inner white
	 * space is not removed and is allowed, although not very readable in the
	 * specification.
	 * 
	 * 
	 * @param varName
	 *            [HOTSPOT_PREFIX]{id}{SUFFIX}
	 * @return the stored value or if no variable among that name is stored
	 *         "0.0"
	 */
	public static Object getValue(String var) {
		String varName = var.trim();
		if (varName.startsWith(HOTSPOT_PREFIX)
				&& varName.endsWith(VISIBLE_SUFFIX)) {
			String hotspotID = varName.substring(HOTSPOT_PREFIX.length(),
					varName.length() - VISIBLE_SUFFIX.length());
			Boolean.toString(HotspotOld.getExisting(hotspotID).isVisible());
		}
		if (varName.startsWith("$")) {
			if (varName.equals(LOCATION_LAT)) {
				// Location lastKnownLocation =
				// GeoQuestApp.getInstance().getLastKnownLocation();
				Location lastKnownLocation = GeoQuestActivity.contextManager
						.getActLocation();
				if (lastKnownLocation != null) {
					return lastKnownLocation.getLatitude();
				} else {
					return LocationUtilities.getCurrentLocation(
							GeoQuestApp.getInstance().getApplicationContext())
							.getLatitude();
				}
			} else if (varName.equals(LOCATION_LONG)) {
				// Location lastKnownLocation =
				// GeoQuestApp.getInstance().getLastKnownLocation();
				Location lastKnownLocation = GeoQuestActivity.contextManager
						.getActLocation();
				if (lastKnownLocation != null) {
					return lastKnownLocation.getLongitude();
				} else {
					return LocationUtilities.getCurrentLocation(
							GeoQuestApp.getInstance().getApplicationContext())
							.getLongitude();
				}
			} else if (variables.containsKey(varName)) {
				return variables.get(varName);
			} else {
				throw new IllegalArgumentException("dynamic system variable "
						+ varName + " undefined");
			}
		} else if (!variables.containsKey(varName)) {
			setValue(varName, 0.0d);
		}
		return variables.get(varName);
	}

	/**
	 * Adds a key/value pair to the variables hashmap
	 * 
	 * @param varName
	 *            [HOTSPOT_PREFIX]{id}{SUFFIX}
	 * @param value
	 */
	public static void setValue(String varName, Object value) {
		variables.put(varName.trim(), value);
	}

	public static void setValueIfUndefined(String varName, Object value) {
		if (!isDefined(varName)) {
			setValue(varName, value);
		}
	}

	public static void clean() {
		variables.clear();
	}

	/**
	 * Creates or overwrites a system variable representing the last result of
	 * the given mission.
	 * 
	 * TODO call this in finish() in MissionActivity as a central point instead
	 * of in all mission classes separate. how to deal with those which do not
	 * have a result? Make another finish method with small signature.
	 * 
	 * @param missionID
	 * @param result
	 */
	public static void registerMissionResult(String missionID, String result) {
		Variables.setValue(SYSTEM_PREFIX + missionID + RESULT_SUFFIX, result);
	}

}
