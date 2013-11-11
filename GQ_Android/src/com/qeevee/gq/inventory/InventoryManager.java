package com.qeevee.gq.inventory;

import java.util.HashMap;
import java.util.Map;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class InventoryManager {

	public static final String DEFAULT_INVENTORY = (String) GeoQuestApp
			.getContext().getText(R.string.default_inventory_name);
	private static Map<String, Inventory> inventories;

	static {
		new Inventory(DEFAULT_INVENTORY);
	}

	public static void register(Inventory inventory) {
		if (inventories == null)
			inventories = new HashMap<String, Inventory>();
		inventories.put(inventory.getName(), inventory);
	}

	public static void addItems(Inventory inv, String itemType, int number) {
		inv.addItems(itemType, number);
	}

	public static void removeItems(Inventory inv, String itemType, int number) {
		inv.removeItems(itemType, number);
	}

	public static Inventory getInventory(String name) {
		return inventories.get(name);
	}
}
