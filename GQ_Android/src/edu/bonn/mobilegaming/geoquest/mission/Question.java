package edu.bonn.mobilegaming.geoquest.mission;

import android.graphics.drawable.BitmapDrawable;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public abstract class Question extends InteractiveMission {

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setBackgroundQuestion() {
		String bg = (String) getMissionAttribute("bgQuestion",
				XMLUtilities.OPTIONAL_ATTRIBUTE, "bg");
		if (bg == null)
			outerView.setBackgroundResource(R.drawable.background_question);
		else {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, true)));
		}
	}

	protected void setBackgroundWrongReply() {
		String bg = (String) getMissionAttribute("bgOnWrongReply",
				XMLUtilities.OPTIONAL_ATTRIBUTE, "bgOnReply", "bg");
		if (bg == null)
			outerView.setBackgroundResource(R.drawable.background_wrong);
		else {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, true)));
		}
	}

	protected void setBackgroundCorrectReply() {
		String bg = (String) getMissionAttribute("bgOnCorrectReply",
				XMLUtilities.OPTIONAL_ATTRIBUTE, "bgOnReply", "bg");
		if (bg == null)
			outerView.setBackgroundResource(R.drawable.background_correct);
		else {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, true)));
		}
	}

}
