package com.uni.bonn.nfc4mg.tagmodels;

public class InfoTagModel {

	// unique id of info tag
	private String id;

	// actual data stored into tag
	private String data;

	public InfoTagModel() {
	}

	public InfoTagModel(String id, String data) {
		this.id = id;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
