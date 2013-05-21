package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.mission.MultipleChoiceQuestion;

@RunWith(GQTestRunner.class)
public class MultipleChoiceQuestionSetBackgroundTests {

	final private String GAME_NAME = "MultipleChoiceQuestion/BackgroundAttributeReadingTest";
	private MultipleChoiceQuestion mpcq;
	private Button bt;
	private View ov;
	private Button correctAnswerButton;
	private Button wrongAnswerButton;

	@Test
	public void useDefaultBackground() {
		// GIVEN:

		// WHEN:
		initTestMission("Defaults");

		// THEN:
		shouldShowBackground(R.drawable.background_question);

		// WHEN:
		giveWrongAnswer();

		// THEN:
		shouldShowBackground(R.drawable.background_wrong);

		// WHEN:
		goBackToQuestion();
		giveCorrectAnswer();

		// THEN:
		shouldShowBackground(R.drawable.background_correct);
	}

	@Test
	public void allModiUseSameBackground() {
		// GIVEN:

		// WHEN:
		initTestMission("AllUseSameBG");

		// THEN:
		shouldShowBackground("drawable/background_question.png");

		// WHEN:
		giveWrongAnswer();

		// THEN:
		shouldShowBackground("drawable/background_question.png");

		// WHEN:
		goBackToQuestion();
		giveCorrectAnswer();

		// THEN:
		shouldShowBackground("drawable/background_question.png");
	}

	@Test
	public void useBGQuestion() {
		// GIVEN:

		// WHEN:
		initTestMission("UseBGQuestion");

		// THEN:
		shouldShowBackground("drawable/background_question.png");

		// WHEN:
		giveWrongAnswer();

		// THEN:
		shouldShowBackground(R.drawable.background_wrong);

		// WHEN:
		goBackToQuestion();
		giveCorrectAnswer();

		// THEN:
		shouldShowBackground(R.drawable.background_correct);
	}

	@Test
	public void useBGReply() {
		// GIVEN:

		// WHEN:
		initTestMission("UseBGReply");

		// THEN:
		shouldShowBackground(R.drawable.background_question);

		// WHEN:
		giveWrongAnswer();

		// THEN:
		shouldShowBackground("drawable/background_reply.png");

		// WHEN:
		goBackToQuestion();
		giveCorrectAnswer();

		// THEN:
		shouldShowBackground("drawable/background_reply.png");
	}

	@Test
	public void useAllDifferentBGs() {
		// GIVEN:

		// WHEN:
		initTestMission("UseAllDifferentBGs");

		// THEN:
		shouldShowBackground("drawable/background_question.png");

		// WHEN:
		giveWrongAnswer();

		// THEN:
		shouldShowBackground("drawable/background_wrong.png");

		// WHEN:
		goBackToQuestion();
		giveCorrectAnswer();

		// THEN:
		shouldShowBackground("drawable/background_correct.png");
	}

	// === HELPER METHODS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	public void initTestMission(String missionID) {
		mpcq = (MultipleChoiceQuestion) TestUtils.prepareMission(
				"MultipleChoiceQuestion", missionID,
				startGameForTest(GAME_NAME));
		mpcq.onCreate(null);
		bt = (Button) getFieldValue(mpcq, "bottomButton");
		ov = (View) getFieldValue((MissionActivity) mpcq, "outerView");
		LinearLayout mcButtonPanel = (LinearLayout) getFieldValue(mpcq,
				"mcButtonPanel");
		correctAnswerButton = (Button) mcButtonPanel.getChildAt(0);
		wrongAnswerButton = (Button) mcButtonPanel.getChildAt(1);
	}

	private void shouldShowBackground(int backgroundQuestion) {
		assertEquals(mpcq.getResources().getDrawable(backgroundQuestion),
				ov.getBackground());
	}

	private void shouldShowBackground(String relPath) {
		assertEquals(new BitmapDrawable(BitmapUtil.loadBitmap(relPath, false)),
				ov.getBackground());
	}

	private void giveCorrectAnswer() {
		correctAnswerButton.performClick();
	}

	private void giveWrongAnswer() {
		wrongAnswerButton.performClick();
	}

	private void goBackToQuestion() {
		bt.performClick();
	}

}
