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

	// Action mode for Bluetooth NFC interaction, currently framework is
	// supporting two action modes
	private int actionMode;

	public BTTagModel(){
		
	}
	
	public BTTagModel(String id, int actionMode) {
		super();
		this.id = id;
		this.actionMode = actionMode;
	}

	public String getId() {
		return id;
	}

	public int getActionMode() {
		return actionMode;
	}

	public void setActionMode(int actionMode) {
		this.actionMode = actionMode;
	}

	public void setId(String id) {
		this.id = id;
	}
}
