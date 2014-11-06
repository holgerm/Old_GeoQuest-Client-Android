package com.qeevee.gq.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.qeevee.gq.R;
import com.qeevee.gq.base.GeoQuestApp;

public class Inventory {

	private static Inventory STANDARD_INVENTORY = null;

	public static Inventory getStandardInventory() {
		if (STANDARD_INVENTORY == null) {
			STANDARD_INVENTORY = new Inventory((String) GeoQuestApp
					.getContext().getText(R.string.default_inventory_name));
		}
		return STANDARD_INVENTORY;
	}

	private String name;

	private Inventory(String name) {
		items = new HashMap<String, Integer>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private Map<String, Integer> items;

	public Map<String, Integer> getItems() {
		return items;
	}

	public void addItems(String itemType, int number) {
		if (items == null)
			items = new HashMap<String, Integer>();
		if (!items.containsKey(itemType))
			items.put(itemType, number);
		else
			items.put(itemType, number + items.get(itemType));
	}

	public void removeItems(String itemType, int number) {
		if (items == null)
			throw new IndexOutOfBoundsException("No items of type " + itemType
					+ "declared.");
		int newNumber = items.get(itemType) - number;
		if (newNumber < 0) {
			items.put(itemType, 0);
			throw new IndexOutOfBoundsException("Not enough " + itemType
					+ " in " + getName() + "to remove " + number + ".");
		}
	}

	public List<String> getItemsAsList() {
		if (items == null) {
			return new ArrayList<String>();
		} else
			return new ArrayList<String>(items.keySet());
	}

	public String[] getItemsAsStringArray() {
		if (items == null) {
			return new String[] { "No Items available" };
		}
		String[] itemStringList = new String[items.size()];
		int i = 0;
		for (Iterator<Entry<String, Integer>> iterator = items.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, Integer> entry = (Entry<String, Integer>) iterator
					.next();
			itemStringList[i++] = entry.getKey() + ":" + entry.getValue();
		}
		return itemStringList;
	}

	public Integer numberOfItem(String itemType) {
		return items.get(itemType);
	}

	public void deleteAll() {
		items.clear();
	}

}
