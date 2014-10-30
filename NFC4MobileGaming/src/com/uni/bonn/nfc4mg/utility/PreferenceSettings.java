package com.uni.bonn.nfc4mg.utility;

import java.util.HashMap;

import android.content.Context;
import android.provider.Settings;

/**
 * <title> Dynamic Project Settings class </title> <body>
 * <tr id="TAG_MIME">
 * Tags defined in this is only parsed by the framework. Rest all kind of data
 * inside tags will be rejected
 * </tr>
 * </body>
 * 
 * @author shubham
 * 
 */
public class PreferenceSettings {

	// Supported MIME types by Info Nfc Tag
	private static HashMap<String, String> TAG_TYPE;
	
	// NOTE : Following are the tag MIME types supported by framework. user can
	// dynamically add support for new MIME type through framework setting api's
	static {
		TAG_TYPE = new HashMap<String, String>();
		TAG_TYPE.put("info_", "Info Tag");
		TAG_TYPE.put("gps_", "GPS Tag");
		TAG_TYPE.put("bt_", "Bluetooth Tag");
		TAG_TYPE.put("wifi_", "WIFI Tag");
		TAG_TYPE.put("group_", "Group Tag");
	};

	/**
	 * Function to add new TAG MIME.
	 * 
	 * @param tagMime
	 * @param value
	 */
	public static void addTagMime(String tagMime, String value) {
		TAG_TYPE.put(tagMime, value);
	}
	
	/**
	 * To get device specific id.
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceMacId(Context context) {

		String deviceId = Settings.Secure.getString(
				context.getContentResolver(), Settings.System.ANDROID_ID);

		return deviceId;

	}
	
}
