package com.qeevee.gq.items;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.WebViewUtil;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;

public class MultipleChoiceQuestionItem extends Question {

	public class AnswerArrayAdapter extends ArrayAdapter<String> {

		private Context context;
		private List<String> answers;

		public AnswerArrayAdapter(MissionActivity containingActivity,
				List<String> answersList) {
			super(containingActivity, R.layout.question_long_list_item,
					R.id.answer_list_entry_text_id, answersList);
			this.context = containingActivity;
			this.answers = answersList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.question_long_list_item,
					parent, false);
			TextView textView = (TextView) rowView
					.findViewById(R.id.answer_list_entry_text_id);
			textView.setText(answers.get(position));
			if (position % 2 == 0) {
				rowView.setBackgroundColor(0x0A222222); // TODO layout mixed
														// with logic here
			}
			return rowView;
		}

	}

	private static final String TAG = MultipleChoiceQuestionItem.class
			.getSimpleName();

	@Override
	public View getView(MissionActivity containingActivity) {
		// make answers:
		LayoutInflater inflater = (LayoutInflater) containingActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		CharSequence length = XMLUtilities.getAttribute("answerLength",
				XMLUtilities.OPTIONAL_ATTRIBUTE, xmlNode);
		View itemView = null;
		if (length == null)
			itemView = (isShortViewFitting() ? createShortAnswerView(inflater)
					: createLongAnswerView(inflater, containingActivity));
		else {
			if (length.equals("short"))
				itemView = createShortAnswerView(inflater);
			else
				itemView = createLongAnswerView(inflater, containingActivity);
		}
		// make question:
		Element xmlQuestion = (Element) xmlNode.selectSingleNode("question");
		if (xmlQuestion != null) {
			WebView questionView = (WebView) itemView
					.findViewById(R.id.question_text_id);
			WebViewUtil.showTextInWebView(questionView, XMLUtilities.getXMLContent(xmlQuestion));
		} else
			Log.e(TAG, "No Question text given!");

		return itemView;
	}

	private View createLongAnswerView(LayoutInflater inflater,
			MissionActivity containingActivity) {
		View itemView = inflater.inflate(R.layout.question_long,
				(ViewGroup) containingActivity
						.findViewById(R.id.item_flipper_id), false);
		// Since, the (inner) item view now is not yet connected with the
		// flipper, we have to search inside this view. (hm)
		ListView listView = (ListView) itemView
				.findViewById(R.id.answer_list_id);
		listView.setAdapter(new AnswerArrayAdapter(containingActivity,
				extractAnswerDescriptions()));
		// listView.setAdapter(new ArrayAdapter<String>(containingActivity,
		// android.R.layout.simple_list_item_1, answersList));
		return itemView;
	}

	private List<String> extractAnswerDescriptions() {
		@SuppressWarnings("unchecked")
		List<Element> xmlAnswers = xmlNode.selectNodes("./answer");
		List<String> answersList = new ArrayList<String>(xmlAnswers.size());
		for (Element xmlAnswer : xmlAnswers) {
			Element xmlDescription = (Element) xmlAnswer
					.selectSingleNode("./description");
			answersList.add(XMLUtilities.getXMLContent(xmlDescription));
		}
		return answersList;
	}

	private View createShortAnswerView(LayoutInflater inflater) {
		View itemView = inflater.inflate(R.layout.question_short, null);
		return itemView;
	}

	private boolean isShortViewFitting() {
		// TODO Auto-generated method stub
		return true;
	}
}
