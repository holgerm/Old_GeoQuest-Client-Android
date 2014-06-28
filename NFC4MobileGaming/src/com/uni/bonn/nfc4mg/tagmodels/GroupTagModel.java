package com.uni.bonn.nfc4mg.tagmodels;

/**
 * This class represents the model of Group. NFC Tag which is storing this
 * structure is called GroupTag.
 * 
 * @author shubham
 * 
 */

public class GroupTagModel {

	// Represents the id of a group. Every id value is prefixed by 'grp_'
	private String id;

	// represents the group tag permission
	private int permission;

	// Represents current member count in the group
	private int occupied;

	// Represents capacity of a group. MAX CAPACITY = 4
	private int capacity;

	// Data shared among group users
	private String data;

	public GroupTagModel() {

	}

	public GroupTagModel(String id, int permission, int capacity, int occupied,
			String data) {
		this.id = id;
		this.permission = permission;
		this.capacity = capacity;
		this.occupied = occupied;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getOccupied() {
		return occupied;
	}

	public void setOccupied(int occupied) {
		this.occupied = occupied;
	}
}
