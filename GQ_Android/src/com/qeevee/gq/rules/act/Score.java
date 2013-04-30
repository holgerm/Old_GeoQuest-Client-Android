package com.qeevee.gq.rules.act;

import org.dom4j.Element;

import android.content.Context;
import android.util.Log;

import com.qeevee.gq.res.ResourceManager;
import com.qeevee.gq.rules.expr.Expressions;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;

public class Score extends Action {

	private static final String SCORE_VARIABLE_NAME = "score";
	private Context ctx = GeoQuestApp.getContext();

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("value");
		return initOK;
	}

	@Override
	public void execute() {
		int score = 0;
		Object valXML = Expressions.evaluate((Element) elements.get("value")
				.selectSingleNode("*"));
		if (valXML instanceof Double) {
			score = ((Double) valXML).intValue();
		}
		int newScore = storeScore(score);
		if (score > 0) {
			GeoQuestApp.playAudio(ResourceManager.POSITIVE_SOUND, false);
			GeoQuestApp.showMessage(ctx.getText(R.string.scoreIncreasedTo) + " " + newScore);
		}
		if (score < 0) {
			GeoQuestApp.playAudio(ResourceManager.NEGATIVE_SOUND, false);
			GeoQuestApp.showMessage(ctx.getText(R.string.scoreDecreasedTo) + " " + newScore);
		}
		storeScore(score);
	}

	private int storeScore(int score) {
		String varName = Variables.SYSTEM_PREFIX + SCORE_VARIABLE_NAME;
		Object oldValue = Variables.getValue(varName);
		int newValue = 0;
		if (oldValue instanceof Double) {
			newValue = ((Double) oldValue).intValue() + score;
			Variables.setValue(varName, newValue);
		} else {
			Log.w(this.getClass().getName(),
					"Tried to set non number variable. Ignored.");
		}
		return newValue;
	}
}
