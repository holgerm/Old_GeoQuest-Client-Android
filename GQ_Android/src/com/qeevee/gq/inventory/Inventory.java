package com.qeevee.gq.inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Inventory {

	private String name;

	Inventory(String name) {
		this.name = name;
		InventoryManager.register(this);
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

}
