package com.qeevee.gq.inventory;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;
import edu.bonn.mobilegaming.geoquest.R;

public class InventoryActivity extends GeoQuestActivity {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.t_default_inventory);
		listView = (ListView) findViewById(R.id.list);
		Inventory inventory = Inventory.getStandardInventory();
		String[] values = inventory.getItemsAsStringArray();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		listView.setAdapter(adapter);
	}

}
