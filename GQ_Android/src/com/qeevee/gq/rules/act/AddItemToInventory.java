package com.qeevee.gq.rules.act;

import com.qeevee.gq.inventory.Inventory;
import com.qeevee.gq.inventory.InventoryManager;

public class AddItemToInventory extends Action {

	private static final String NUMBER_OF_ITEMS = "numberOfItems";
	private static final String ITEM_TYPE = "itemType";

	@Override
	protected boolean checkInitialization() {
		return params.containsKey(ITEM_TYPE)
				&& params.containsKey(NUMBER_OF_ITEMS);
	}

	@Override
	public void execute() {
		Inventory inventory = InventoryManager
				.getInventory(InventoryManager.DEFAULT_INVENTORY);
		inventory.addItems(params.get(ITEM_TYPE),
				Integer.valueOf(params.get(NUMBER_OF_ITEMS)));
	}
}
