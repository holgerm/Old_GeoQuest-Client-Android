package com.qeevee.gq.history;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public class HistoryListAdapter extends BaseAdapter implements ListAdapter {

	private History history;

	public HistoryListAdapter() {
		history = History.getInstance();
	}

	public int getCount() {
		return history.numberOfItems();
	}

	public Object getItem(int position) {
		return history.getItem(position);
	}

	public long getItemId(int position) {
		return history.getItemId(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return history.getItem(position).getView(convertView);
	}

}
