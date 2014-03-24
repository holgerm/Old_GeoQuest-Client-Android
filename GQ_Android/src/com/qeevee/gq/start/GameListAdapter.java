package com.qeevee.gq.start;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.bonn.mobilegaming.geoquest.R;

public class GameListAdapter extends ArrayAdapter<GameDescription> {

	private Context context;
	private List<GameDescription> gameDescriptions;

	public GameListAdapter(Context context, int resource,
			List<GameDescription> gameDescriptions) {
		super(context, resource, gameDescriptions);
		this.context = context;
		this.gameDescriptions = gameDescriptions;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.questTitle);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.questIcon);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		String s = (gameDescriptions.get(position)).getName();
		holder.text.setText(s);
		holder.image.setImageResource(R.drawable.ic_launcher);

		return rowView;
	}

	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}

}
