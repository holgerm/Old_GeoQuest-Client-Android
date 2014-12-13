package com.qeevee.gq.rules.act;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gqdefault.R;

public class DrawRectangle extends Action {

	@Override
	protected boolean checkInitialization() {
		boolean valid = true;
		valid &= params.containsKey("x");
		valid &= params.containsKey("y");
		valid &= params.containsKey("width");
		valid &= params.containsKey("height");
		valid &= params.containsKey("color");
		return valid;
	}

	@Override
	public void execute() {
		Activity a = GeoQuestApp.getCurrentActivity();
		if (!(a instanceof MissionActivity)) {
			return;
		}

		View view = ((MissionActivity) a).findViewById(R.id.canvas);
		if (view == null)
			return;
		
		ShapeDrawable drawable = new ShapeDrawable(new RectShape());
		drawable.getPaint().setColor(Color.parseColor(params.get("color")));
//		drawable.setBounds(left, top, right, bottom);
//
//
//		GeoQuestApp.getDrawStack().addRectangle()
	}
}
