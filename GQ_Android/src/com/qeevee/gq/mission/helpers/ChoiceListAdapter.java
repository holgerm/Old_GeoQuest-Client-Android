package com.qeevee.gq.mission.helpers;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Device;

import com.qeevee.gqtours.R;
import com.qeevee.gq.mission.MultipleChoiceQuestion.Answer;

public class ChoiceListAdapter extends ArrayAdapter<Answer> {

	private Context context;
	private List<Answer> answers;

	public ChoiceListAdapter(Context context, int resource, List<Answer> answers) {
		super(context, resource, answers);
		this.context = context;
		this.answers = answers;
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
			rowView = inflater.inflate(R.layout.choice_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.choiceTitle);
			viewHolder.image = (ImageView) rowView
					.findViewById(R.id.choiceIcon);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		String s = answers.get(position).answertext;
		holder.text.setText(s);
		CharSequence imagePath = answers.get(position).imagePath;
		if (imagePath == null)
			holder.image.setImageResource(R.drawable.ic_launcher);
		else
			holder.image.setImageBitmap(BitmapUtil.loadBitmap(
					imagePath.toString(), Device.getDisplayWidth() / 4, 0, true));

		return rowView;
	}

	static class ViewHolder {
		public TextView text;
		public ImageView image;
		public String answertext;
		public Boolean correct;
		public CharSequence onChoose;
		public CharSequence nextbuttontext;
		public CharSequence imagePath;
		public boolean showAsImage;
	}

}
