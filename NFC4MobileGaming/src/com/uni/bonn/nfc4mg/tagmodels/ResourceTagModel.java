package com.uni.bonn.nfc4mg.tagmodels;


/**
 * This class represents the model of Group. NFC Tag which is storing this
 * structure is called GroupTag.
 * 
 * TODO : In future Group permission on resource can be introduced.
 * TODO : In future one NFC tags can contain multiple resource
 * @author shubham
 * 
 */

public class ResourceTagModel{

	// Represents resource id. Every id value is prefixed by 'res_'
	private String id;

	// Represents resource name
	private String name;

	// Represents resource count: default is set to 1 at the time of
	// initialization
	private int count;

	public ResourceTagModel() {

	}

	public ResourceTagModel(String id, String name, int count) {
		this.id = id;
		this.name = name;
		this.count = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
