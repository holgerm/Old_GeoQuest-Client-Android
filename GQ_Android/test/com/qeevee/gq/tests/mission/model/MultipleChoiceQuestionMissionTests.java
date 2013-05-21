package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.getResString;
import static com.qeevee.gq.tests.util.TestUtils.getStaticFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.nthLastItemInHistoryShouldBe;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qeevee.gq.history.Actor;
import com.qeevee.gq.history.History;
import com.qeevee.gq.history.TextItem;
import com.qeevee.gq.history.TextType;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.xtremelabs.robolectric.Robolectric;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.mission.MultipleChoiceQuestion;

@RunWith(GQTestRunner.class)
public class MultipleChoiceQuestionMissionTests {

	private static final String GAME_NAME = "MultipleChoiceQuestion/FunctionTest";
	private static final int MODE_QUESTION = (Integer) getStaticFieldValue(
			MultipleChoiceQuestion.class, "MODE_QUESTION");
	private static final int MODE_REPLY_TO_CORRECT_ANSWER = (Integer) getStaticFieldValue(
			MultipleChoiceQuestion.class, "MODE_REPLY_TO_CORRECT_ANSWER");
	private static final int MODE_REPLY_TO_WRONG_ANSWER = (Integer) getStaticFieldValue(
			MultipleChoiceQuestion.class, "MODE_REPLY_TO_WRONG_ANSWER");
	private static final int FIRST_WRONG_ANSWER = 0;
	private static final int FIRST_RIGHT_ANSWER = 1;
	private static final int SECOND_WRONG_ANSWER = 2;

	private static String DEFAULT_RESPONSE_ON_CORRECT_ANSWER;
	private static String DEFAULT_RESPONSE_ON_WRONG_ANSWER;

	private MissionActivity mcqM;
	private String questionText;
	private List<Button> answerButtons;
	private int mode;
	private Button bottomButton;

	@After
	public void cleanUp() {
		// get rid of all variables that have been set, e.g. for checking
		// actions.
		Variables.clean();
		History.getInstance().clear();
	}

	@SuppressWarnings("unchecked")
	public void initTestMission(String missionID) {
		mcqM = (MissionActivity) prepareMission("MultipleChoiceQuestion",
				missionID, startGameForTest(GAME_NAME));
		try {
			mcqM.onCreate(null);
		} catch (NullPointerException npe) {
			fail("Mission with id \"" + missionID + "\" missing. (NPE: "
					+ npe.getMessage() + ")");
		}
		loadFieldsOfObjectUnderTest();
	}

	private void loadFieldsOfObjectUnderTest() {
		questionText = (String) getFieldValue(mcqM, "questionText");
		DEFAULT_RESPONSE_ON_CORRECT_ANSWER = getResString(R.string.questionandanswer_rightAnswer);
		DEFAULT_RESPONSE_ON_WRONG_ANSWER = getResString(R.string.questionandanswer_wrongAnswer);
		LinearLayout mcButtonPanel = (LinearLayout) getFieldValue(mcqM,
				"mcButtonPanel");
		mode = (Integer) getFieldValue(mcqM, "mode");
		answerButtons = new ArrayList<Button>(mcButtonPanel.getChildCount());
		for (int i = 0; i < mcButtonPanel.getChildCount(); i++) {
			answerButtons.add((Button) mcButtonPanel.getChildAt(i));
		}
		bottomButton = (Button) getFieldValue(mcqM, "bottomButton");
	}

	// === TESTS FOLLOW =============================================

	@Test
	public void initialization_No_Loop__No_onChoose() {
		// GIVEN:
		// nothing

		// WHEN:
		initTestMission("No_Loop__No_onChoose");

		// THEN:
		shouldBeInMode(MODE_QUESTION);
		shouldHaveTriggeredEvents("onStart");
		should_NOT_HaveTriggeredEvents("onSuccess", "onFail", "onEnd");
		numberOfAnswersShouldBe(3);
		shouldStoreQuestionText();
		shouldShowText("Text of the question.");
		nthLastItemInHistoryShouldBe(1, TextItem.class, TextType.QUESTION,
				Actor.GAME);
		TestUtils.historyListShouldHaveLength(1);
	}

	@Test
	public void selectCorrectAnswer_No_Loop__No_onChoose() {
		// GIVEN:
		initTestMission("No_Loop__No_onChoose");

		// WHEN:
		clickOnAnswerButton(FIRST_RIGHT_ANSWER);

		// THEN:
		shouldBeInMode(MODE_REPLY_TO_CORRECT_ANSWER);
		shouldHaveTriggeredEvents("onStart", "onSuccess");
		should_NOT_HaveTriggeredEvents("onFail", "onEnd");
		shouldShowText(DEFAULT_RESPONSE_ON_CORRECT_ANSWER);
		shouldShowProceedButton();
		TestUtils.historyListShouldHaveLength(2);
		nthLastItemInHistoryShouldBe(1, TextItem.class,
				TextType.REACTION_ON_CORRECT);
	}

	@Test
	public void proceedAfterCorrectAnswer_No_Loop__No_onChoose() {
		// GIVEN:
		initTestMission("No_Loop__No_onChoose");
		clickOnAnswerButton(FIRST_RIGHT_ANSWER);

		// WHEN:
		bottomButton.performClick();

		// THEN:
		shouldHaveTriggeredEvents("onStart", "onSuccess", "onEnd");
		should_NOT_HaveTriggeredEvents("onFail");
		shouldHaveFinishedActivity(true);
	}

	@Test
	public void selectWrongAnswer_No_Loop__No_onChoose() {
		// GIVEN:
		initTestMission("No_Loop__No_onChoose");

		// WHEN:
		clickOnAnswerButton(FIRST_WRONG_ANSWER);

		// THEN:
		shouldBeInMode(MODE_REPLY_TO_WRONG_ANSWER);
		shouldHaveTriggeredEvents("onStart", "onFail");
		should_NOT_HaveTriggeredEvents("onSuccess", "onEnd");
		shouldShowText(DEFAULT_RESPONSE_ON_WRONG_ANSWER);
		shouldShowProceedButton();
		TestUtils.historyListShouldHaveLength(2);
		nthLastItemInHistoryShouldBe(1, TextItem.class,
				TextType.REACTION_ON_WRONG);
	}

	@Test
	public void proceedAfterWrongAnswer_No_Loop__No_onChoose() {
		// GIVEN:
		initTestMission("No_Loop__No_onChoose");
		clickOnAnswerButton(FIRST_WRONG_ANSWER);

		// WHEN:
		bottomButton.performClick();

		// THEN:
		shouldHaveTriggeredEvents("onStart", "onFail", "onEnd");
		should_NOT_HaveTriggeredEvents("onSuccess");
		shouldHaveFinishedActivity(true);
	}

	@Test
	public void initialization_Loop_onChoose() {
		// GIVEN:
		// nothing

		// WHEN:
		initTestMission("Loop_onChoose");

		// THEN:
		shouldBeInMode(MODE_QUESTION);
		numberOfAnswersShouldBe(4);
		shouldStoreQuestionText();
		shouldShowText("Text of the question.");
		TestUtils.historyListShouldHaveLength(1);
		nthLastItemInHistoryShouldBe(1, TextItem.class, TextType.QUESTION);
	}

	@Test
	public void selectWrongAnswerFirstTime_Loop_onChoose() {
		// GIVEN:
		initTestMission("Loop_onChoose");

		// WHEN:
		clickOnAnswerButton(FIRST_WRONG_ANSWER);

		// THEN:
		shouldBeInMode(MODE_REPLY_TO_WRONG_ANSWER);
		shouldHaveTriggeredEvents("onFail");
		shouldShowText("Answer one is wrong.");
		shouldShowRestartButton();
		TestUtils.historyListShouldHaveLength(2);
		nthLastItemInHistoryShouldBe(1, TextItem.class,
				TextType.REACTION_ON_WRONG);
	}

	@Test
	public void restartAfterFirstWrongAnswer_Loop_onChoose() {
		// GIVEN:
		initTestMission("Loop_onChoose");
		clickOnAnswerButton(FIRST_WRONG_ANSWER);

		// WHEN:
		bottomButton.performClick();

		// THEN:
		shouldBeInMode(MODE_QUESTION);
		numberOfAnswersShouldBe(4);
		shouldStoreQuestionText();
		shouldShowText("Text of the question.");
		TestUtils.historyListShouldHaveLength(3);
		nthLastItemInHistoryShouldBe(1, TextItem.class, TextType.QUESTION);
	}

	@Test
	public void selectWrongAnswerTwice_Loop_onChoose() {
		// GIVEN:
		initTestMission("Loop_onChoose");
		clickOnAnswerButton(FIRST_WRONG_ANSWER);
		bottomButton.performClick();
		clearEventRecognition();

		// WHEN:
		clickOnAnswerButton(SECOND_WRONG_ANSWER);

		// THEN:
		shouldBeInMode(MODE_REPLY_TO_WRONG_ANSWER);
		shouldShowText("Answer three is wrong.");
		shouldShowRestartButton();
		shouldHaveTriggeredEvents("onFail");
		TestUtils.historyListShouldHaveLength(4);
		nthLastItemInHistoryShouldBe(1, TextItem.class,
				TextType.REACTION_ON_WRONG);
	}

	// === HELPER METHODS FOLLOW =============================================

	private void shouldShowRestartButton() {
		assertEquals(getResString(R.string.question_repeat_button),
				bottomButton.getText());
	}

	private void shouldShowProceedButton() {
		assertEquals(getResString(R.string.question_proceed_button),
				bottomButton.getText());
	}

	private void clearEventRecognition() {
		Variables.clean();
	}

	private void shouldHaveFinishedActivity(boolean shouldHaveFinished) {
		assertEquals(shouldHaveFinished, Robolectric.shadowOf(mcqM)
				.isFinishing());
	}

	private void shouldShowText(String textExpectedToBeShown) {
		assertEquals(textExpectedToBeShown,
				((TextView) getFieldValue(mcqM, "mcTextView")).getText());
	}

	private void clickOnAnswerButton(int i) {
		answerButtons.get(i).performClick();
	}

	private void shouldHaveTriggeredEvents(String... eventName) {
		for (int i = 0; i < eventName.length; i++) {
			assertEquals(1.0, Variables.getValue(eventName[i]));
		}
	}

	private void should_NOT_HaveTriggeredEvents(String... eventName) {
		for (int i = 0; i < eventName.length; i++) {
			assertFalse(
					"onFail Variable should NOT be initialized AFTER proceed button has been pressed",
					Variables.isDefined(eventName[i]));
		}
	}

	private void shouldBeInMode(int expectedMode) {
		mode = (Integer) getFieldValue(mcqM, "mode");
		assertEquals(expectedMode, mode);
	}

	private void numberOfAnswersShouldBe(int nr) {
		assertEquals(nr, answerButtons.size());
	}

	private void shouldStoreQuestionText() {
		assertEquals("Text of the question.", questionText);
	}
}
