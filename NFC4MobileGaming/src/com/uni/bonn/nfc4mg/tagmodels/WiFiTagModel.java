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

	// Holds the current network SSID : This holds the network name to connect
	// with
	private String ssid;

	// required password to connect with network.
	private String password;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
