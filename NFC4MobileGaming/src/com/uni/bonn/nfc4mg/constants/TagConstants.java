package com.uni.bonn.nfc4mg.constants;

/**
 * This class holds the tag initialiser or tag types supported by framework
 * 
 * @author shubham
 * 
 */
public class TagConstants {

	// Predefined prefix id for tags supported by framework.
	public static final String TAG_TYPE_INFO_PREFIX = "info_";
	public static final String TAG_TYPE_GPS_PREFIX = "gps_";
	public static final String TAG_TYPE_BT_PREFIX = "bt_";
	public static final String TAG_TYPE_WIFI_PREFIX = "wifi_";
	public static final String TAG_TYPE_GROUP_PREFIX = "group_";
	public static final String TAG_TYPE_RESOURCE_PREFIX = "res_";

	// Predefined values of different tag types
	public static final int TAG_TYPE_INFO = 0x01;
	public static final int TAG_TYPE_GPS = 0x02;
	public static final int TAG_TYPE_BT = 0x03;
	public static final int TAG_TYPE_WIFI = 0x04;
	public static final int TAG_TYPE_GROUP = 0x05;
	public static final int TAG_TYPE_RESOURCE = 0x06;

	// Max Group capacity
	public static final int MAX_GROUP_CAPACITY = 2;

	// Application shared preference file name
	public static final String NFC4MG_PREF = "nfc4mg";
	// group key, present in the preference file. This will always hold single
	// value
	public static final String GROUP_INFO = "group";
}
