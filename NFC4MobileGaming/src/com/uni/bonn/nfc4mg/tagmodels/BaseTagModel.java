package com.uni.bonn.nfc4mg.tagmodels;

/**
 * Class represents the base model of nested NdefMessage i.e. NdefRecord NOTE :
 * As per Android api's we can write only one NdefMessage in NFC based Tags
 * 
 * @author shubham
 * 
 */
public class BaseTagModel {

	// MIME TYPE
	private String type;

	// Actual Data
	private String data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
