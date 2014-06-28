package com.uni.bonn.nfc4mg.tagmodels;

/**
 * This class represents the model of GPS Tags. NFC Tag which is storing the
 * information about Coordinates. All Fields in this class represents a single
 * NdefRecord inside a NFC Tag NdefMessage.
 * 
 * This model represents the internal storage structure of GPS_NFC_TAG
 * NFC_GPS_TAG[NDEFMessage[NdefRecord_id, NdefRecord_latitude,
 * NdefRecord_longitude]]
 * 
 * @author shubham
 * 
 */
public class WiFiTagModel {

	// Represents the id of a Wi-Fi Tag. Every id value is prefixed by 'wifi_' :
	// MAX_ID length = 10 else framework will through error
	private String id;

	// Action mode for WIFI NFC interaction, currently framework is
	// supporting two action modes
	private int actionMode;

	private String ssid = "";

	private String password = "";

	public WiFiTagModel() {

	}

	public WiFiTagModel(String id, int actionMode) {
		super();
		this.id = id;
		this.actionMode = actionMode;
	}

	public WiFiTagModel(String id, int actionMode, String ssid, String password) {
		super();
		this.id = id;
		this.actionMode = actionMode;
		this.ssid = ssid;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getActionMode() {
		return actionMode;
	}

	public void setActionMode(int actionMode) {
		this.actionMode = actionMode;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
