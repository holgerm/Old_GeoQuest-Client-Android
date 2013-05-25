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
public class GPSTagModel {

	// Represents the id of a gpsTag. Every id value is prefixed by 'gps_' :
	// MAX_ID length = 10 else framework will through error
	private String id;

	// holds parsed latitude value from NFC Tag
	private String latitude;

	// holds parsed longitude value from NFC Tag
	private String longitude;

	// data about location
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public GPSTagModel() {
	}

	public GPSTagModel(String id, String latitude, String longitude, String data) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
