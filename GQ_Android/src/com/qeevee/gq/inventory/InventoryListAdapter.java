package com.qeevee.gq.inventory;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.qeevee.gqdefault.R;

public class InventoryListAdapter extends ArrayAdapter<String> {

	private List<String> items;

	public InventoryListAdapter(Context context, int resource,
			List<String> items) {
		super(context, resource, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {

			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.list_item_inventory, null);

		}

		String itemType = items.get(position);

		if (itemType != null) {

			TextView nameView = (TextView) v.findViewById(R.id.item_name);
			TextView countView = (TextView) v.findViewById(R.id.item_count);

			if (nameView != null) {
				nameView.setText(itemType);
			}
			if (countView != null) {

				countView.setText(String.valueOf(Inventory
						.getStandardInventory().numberOfItem(itemType)));
			}
		}

		return v;

	}
}
