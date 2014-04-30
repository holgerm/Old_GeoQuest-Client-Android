package com.qeevee.gq.rules.act;

import android.content.Context;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;

public class AddToScore extends Action {

	static final String SCORE_VARIABLE = "score";

	private Context ctx = GeoQuestApp.getContext();
	private boolean showToast = false;

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= params.containsKey("value");
		return initOK;
	}

	@Override
	public void execute() {
		if (!Variables.isDefined(SCORE_VARIABLE)) {
			Variables.setValue(SCORE_VARIABLE, 0.);
		}
		double deltaScore = Double.parseDouble(params.get("value"));

		String a = params.get("showMessage");
		if (a != null) {
			showToast = Boolean.parseBoolean(a);
		}

		double resultingScore = addToScore(deltaScore);
		if (showToast) {
			if (resultingScore == 0.0) {
				GeoQuestApp.showMessage(ctx.getText(R.string.scoreZero));
			} else if (deltaScore > 0) {
				// GeoQuestApp.playAudio(ResourceManager.POSITIVE_SOUND, false);
				GeoQuestApp.showMessage(ctx.getText(R.string.scoreIncreasedBy)
						+ " " + deltaScore);
			} else if (deltaScore < 0) {
				// GeoQuestApp.playAudio(ResourceManager.NEGATIVE_SOUND, false);
				GeoQuestApp.showMessage(ctx.getText(R.string.scoreDecreasedBy)
						+ " " + (-deltaScore));
			}
		}
	}

	private double addToScore(double score) {
		double newScore = (Double) Variables.getValue(SCORE_VARIABLE) + score;
		newScore = newScore < 0.0 ? 0.0 : newScore;
		Variables.setValue(SCORE_VARIABLE, newScore);
		return newScore;
		// Object lastScoreO = Variables.getValue(SCORE_VARIABLE);
		// double lastScoreD = 0.;
		// int resultScore = 0;
		// if (lastScoreO.getClass().getName().equals(Integer.class.getName()))
		// {
		// resultScore = (Integer) lastScoreO + score;
		// } else {
		// lastScoreD = (Double) lastScoreO;
		// resultScore = (int) lastScoreD + score;
		// }
		// if (resultScore < 0)
		// resultScore = 0;
		// Variables.setValue(SCORE_VARIABLE, resultScore);
		// Log.d("myTag", "score is: " + resultScore);
		// return resultScore;
	}
}
