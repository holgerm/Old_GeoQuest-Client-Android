package com.uni.bonn.nfc4mg.inventory;

import java.util.HashMap;

/**
 * Inventory Manager class to manage the data about all resources earned during
 * the game.
 * 
 * @author shubham
 * 
 */
public class InventoryManager {

	private static InventoryManager INSTANCE = null;
	
	private static HashMap<String, InventoryModel> INVENTORY_REPO = new HashMap<String, InventoryManager.InventoryModel>();

	/**
	 * Singleton Class
	 */
	private InventoryManager() {
	}

	/**
	 * Get the instance of inventory manager.
	 * @return
	 */
	public InventoryManager getInventoryManager() {
		
		if (null == INSTANCE) {
			INSTANCE = new InventoryManager();
		}
		return INSTANCE;
	}

	
	private class InventoryModel{
		
		private String id; // key and id should match
		private int quantity; // number of resource
		private String name; // key, fruit, milk any...
		private boolean autoRemove; //This field indicate auto deletion of resource after use.
		//currently autoRemove feature is not suported by framework, will add in future
	}
	
	/**
	 * Add item to inventory
	 * @param key : Each NFC Tag is initialized with unique id, that id can be treated as the key in inventory.
	 * @param model
	 */
	public void addToInventory(String key, InventoryModel model){		
		INVENTORY_REPO.put(key, model);
	}
	
	/**
	 * Remove Item from inventory
	 * @param key : Each NFC Tag is initialized with unique id, that id can be treated as the key in inventory.
	 */
	public void removeFromInventory(String key){		
		INVENTORY_REPO.remove(key);
	}
	
	/**
	 * To check inventory is empty or not.
	 * @return
	 */
	boolean isInventoryEmpty(){
		return INVENTORY_REPO.isEmpty();
	}	
}
