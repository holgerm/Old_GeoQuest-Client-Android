package com.uni.bonn.nfc4mg.inventory;

import java.io.Serializable;

/**
 * This represents the model of items present in inventory. All the items in the
 * inventory must be unique, but in future we can also introduce item count
 * parameter to maintain the number of items.
 * 
 * @author shubham
 * 
 */
public class InventoryModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 99150405118081679L;

	// id
	private String id;

	// name of item
	private String name;

	private int count;

	public InventoryModel(String id, String name, int count) {
		this.id = id;
		this.name = name;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
