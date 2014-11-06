package com.qeevee.gq.base;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.qeevee.gq.loc.HotspotManager;

public class Variables {

	private static Map<String, Object> variables;

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
	 */
	private static final String HOTSPOT_PREFIX = "$_hotspot_";

	/**
	 * use this constant for defining the key of the hotspot location variable,
	 * e.g.
	 * <code>setValue(Variables.HOTSPOT_PREFIX + 1234 + LOCATION_SUFFIX, new GeoPoint(50,3)));
	 */
	public static final String LOCATION_SUFFIX = "location";

	public static final String SYSTEM_PREFIX = "$_";
	private static final String MISSION_PREFIX = "$_mission_";
	private static final String LONG = ".long";
	private static final String LAT = ".lat";

	public static final String LOCATION_LAT = SYSTEM_PREFIX + LOCATION_SUFFIX
			+ LAT;
	public static final String LOCATION_LONG = SYSTEM_PREFIX + LOCATION_SUFFIX
			+ LONG;

	public static final String CENTER_MAP_POSITION = SYSTEM_PREFIX
			+ "centerToPosition";
	public static final String CENTER_MAP_ACTIVE_HOTSPOTS = SYSTEM_PREFIX
			+ "centerToActiveHotspots";
	public static final String CENTER_MAP_VISIBLE_HOTSPOTS = SYSTEM_PREFIX
			+ "centerToVisibleHotspots";

	public static final String LAST_TAP_X = SYSTEM_PREFIX + "tap.x";
	public static final String LAST_TAP_Y = SYSTEM_PREFIX + "tap.y";

	private static final String NULL_VARIABLE = SYSTEM_PREFIX + "null";

	public static final String BREAK_WHILE = SYSTEM_PREFIX + "break_while";

	private static final String TAG = Variables.class.getCanonicalName();

	private static Object UNDEFINED_VARIABLE = new Object() {

		@Override
		public boolean equals(Object o) {
			return false;
		}

		@Override
		public String toString() {
			return "UNDEFINED";
		}

	};

	static {
		variables = new HashMap<String, Object>();
		clean();
	}

	/**
	 * Checks if the given key exists in the variables hashmap.
	 * 
	 * @param varName
	 *            [HOTSPOT_PREFIX]{id}{SUFFIX}
	 * @return
	 */
	public static boolean isDefined(String varName) {
		if (varName == null)
			varName = NULL_VARIABLE;
		return variables.containsKey(varName.trim());
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
		if (var == null) {
			var = NULL_VARIABLE;
		}
		String varName = var.trim();
		if (varName.startsWith(HOTSPOT_PREFIX)
				&& varName.endsWith(VISIBLE_SUFFIX)) {
			String hotspotID = varName.substring(HOTSPOT_PREFIX.length(),
					varName.length() - VISIBLE_SUFFIX.length());
			Boolean.toString(HotspotManager.getInstance()
					.getExisting(hotspotID).isVisible());
		}
		if (varName.startsWith("$")) {
			if (variables.containsKey(varName)) {
				return variables.get(varName);
			} else {
				Log.e(TAG, "dynamic system variable " + varName + " undefined");
				return UNDEFINED_VARIABLE;
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
		if (varName == null)
			varName = NULL_VARIABLE;
		variables.put(varName.trim(), value);
	}

	public static void setValueIfUndefined(String varName, Object value) {
		if (varName == null)
			varName = NULL_VARIABLE;
		if (!isDefined(varName)) {
			setValue(varName.trim(), value);
		}
	}

	public static void clean() {
		variables.clear();
		variables.put(CENTER_MAP_POSITION, "true");
		variables.put(CENTER_MAP_ACTIVE_HOTSPOTS, "false");
		variables.put(CENTER_MAP_VISIBLE_HOTSPOTS, "true");
		variables.put(BREAK_WHILE, Boolean.valueOf("false"));
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
		Variables.setValue(MISSION_PREFIX + missionID + RESULT_SUFFIX, result);
	}

	public static String getMissionStateVarName(String id) {
		return Variables.MISSION_PREFIX + id + Variables.STATUS_SUFFIX;
	}

	public static String getHotspotLongitudeVarName(String id) {
		return Variables.HOTSPOT_PREFIX + id + LONG;
	}

	public static String getHotspotLatitudeVarName(String id) {
		return Variables.HOTSPOT_PREFIX + id + LAT;
	}

}
