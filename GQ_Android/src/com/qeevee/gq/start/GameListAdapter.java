package com.qeevee.gq.start;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.qeevee.gq.game.GameDescription;

public class GameListAdapter extends ArrayAdapter<GameDescription> {

	public GameListAdapter(Context context, int resource,
			List<GameDescription> objects) {
		super(context, resource, objects);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getID();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
