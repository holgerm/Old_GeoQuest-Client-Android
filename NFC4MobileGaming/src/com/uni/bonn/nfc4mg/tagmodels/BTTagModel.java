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
public class BTTagModel {

	// Represents the id of a Blue tooth Tag. Every id value is prefixed by
	// 'bt_' :
	// MAX_ID length = 10 else framework will through error
	private String id;

	// Devive BT Mac Address. This is required so client can easily identify and
	// connect to BT.
	private String macAddr; // E.g 04:8F:E8:01:1A:C7

	// Key to connect to remote Bt device. E.g. 0000
	private String passKey;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public String getPassKey() {
		return passKey;
	}

	public void setPassKey(String passKey) {
		this.passKey = passKey;
	}

}
