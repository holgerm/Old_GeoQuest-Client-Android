package com.qeevee.gq.inventory;

import android.os.Bundle;
import android.widget.ListView;

import com.qeevee.gqdefault.R;
import com.qeevee.gq.base.GeoQuestActivity;

public class InventoryActivity extends GeoQuestActivity {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.t_default_inventorytest);
		listView = (ListView) findViewById(R.id.list);
		Inventory inventory = Inventory.getStandardInventory();

		InventoryListAdapter listAdapter = new InventoryListAdapter(this,
				R.layout.list_item_inventory, inventory.getItemsAsList());

		// String[] values = inventory.getItemsAsStringArray();
		// ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, android.R.id.text1, values);

		listView.setAdapter(listAdapter);
	}

}
