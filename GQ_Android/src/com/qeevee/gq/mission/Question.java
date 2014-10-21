package com.qeevee.gq.mission;

import android.graphics.drawable.BitmapDrawable;

import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Device;

import com.qeevee.gq.R;

public abstract class Question extends InteractiveMission {

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setBackgroundQuestion() {
		String bg = (String) getMissionAttribute("bgQuestion",
				XMLUtilities.OPTIONAL_ATTRIBUTE, "bg");
		if (bg == null)
			outerView.setBackgroundResource(R.drawable.mcq_background_question);
		else {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, Device.getDisplayWidth(),
							Device.getDisplayHeight(), false)));
		}
	}

	protected void setBackgroundWrongReply() {
		String bg = (String) getMissionAttribute("bgOnWrongReply",
				XMLUtilities.OPTIONAL_ATTRIBUTE, "bgOnReply", "bg");
		if (bg == null)
			outerView.setBackgroundResource(R.drawable.mcq_background_wrong);
		else {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, Device.getDisplayWidth(),
							Device.getDisplayHeight(), false)));
		}
	}

	protected void setBackgroundCorrectReply() {
		String bg = (String) getMissionAttribute("bgOnCorrectReply",
				XMLUtilities.OPTIONAL_ATTRIBUTE, "bgOnReply", "bg");
		if (bg == null)
			outerView.setBackgroundResource(R.drawable.mcq_background_right);
		else {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, Device.getDisplayWidth(),
							Device.getDisplayHeight(), false)));
		}
	}

}
