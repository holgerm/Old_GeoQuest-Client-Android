package com.uni.bonn.nfc4mg.tagmodels;

public class InfoTagModel {

	// unique id of info tag
	private String id;

	// represents type of data stored in infor tag
	private String mime;

	// actual data stored into tag
	private String data;

	public InfoTagModel() {
	}

	public InfoTagModel(String id, String mime, String data) {
		super();
		this.id = id;
		this.mime = mime;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
