package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.getResString;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.Question;

@RunWith(GQTestRunner.class)
public class TextQuestionMissionPlainTextTests {
	private static final String GAME_NAME = "TextQuestion/FunctionTest";
	Question tq;
	TextView tv;
	EditText et;
	Button bt;

	@SuppressWarnings("unchecked")
	public void initTestMission(String missionID) {
		tq = (Question) TestUtils.prepareMission("TextQuestion", missionID,
				startGameForTest(GAME_NAME));
		tq.onCreate(null);
		tv = (TextView) getFieldValue(tq, "textView");
		et = (EditText) getFieldValue(tq, "answerEditText");
		bt = (Button) getFieldValue(tq, "button");
	}

	@After
	public void cleanUp() {
		// get rid of all variables that have been set, e.g. for checking
		// actions.
		Variables.clean();
	}

	@Test
	public void startEventTriggered() {
		assertFalse(
				"onStart Variable should not be initialized at beginning of test",
				Variables.isDefined("onStart"));
		initTestMission("With_Defaults");
		assertEquals("onStart should have set variable to 1", 1.0,
				Variables.getValue("onStart"));
	}

	@Test
	public void questionTextSet() {
		initTestMission("With_Defaults");
		assertEquals("Question text should be stored in UI.",
				"Text of the question.", tv.getText());
	}

	@Test
	public void answerEditTextInitialized() {
		initTestMission("With_Defaults");
		assertEquals("Initially edit text should be shown", View.VISIBLE,
				et.getVisibility());
		assertEquals("Default hint should be displayed",
				getResString(R.string.textquestion_answerET_hint_default), et
						.getHint().toString());
		assertEquals("No text should be displayed in answer area initially",
				"", et.getText().toString());
	}

	@Test
	public void acceptButtonOnlyEnabledWhenAnswerTextNotEmpty() { 
		initTestMission("With_Defaults");
		assertFalse("Button should be disabled initially", bt.isEnabled());
		assertEquals("Button label should be accept",
				getResString(R.string.button_text_accept), bt.getText());
		et.append("Some answer text...");
		assertEquals("Button should be enabled after text input", true,
				bt.isEnabled());
		assertEquals("Button label should be accept",
				getResString(R.string.button_text_accept), bt.getText());
		et.append(" more text");
		assertEquals("Button should still be enabled after further text input",
				true, bt.isEnabled());
		assertEquals("Button label should be accept",
				getResString(R.string.button_text_accept), bt.getText());
		et.setText("");
		assertEquals(
				"Button should be disabled after text has been removed completely",
				false, bt.isEnabled());
		assertEquals("Button label should be accept",
				getResString(R.string.button_text_accept), bt.getText());
	}

	@Test
	public void goThroughWithWrongAnswer() {
		initTestMission("With_Defaults");
		assertEquals("In question mode edit text should be shown",
				View.VISIBLE, et.getVisibility());
		et.setText("Something completely wrong");
		assertTrue("Button should be enabled even when wrong text is entered",
				bt.isEnabled());
		assertFalse(
				"onFail Variable should not be initialized before proceed button clicked",
				Variables.isDefined("onFail"));
		bt.performClick();
		assertEquals(
				"onFail should have been triggered after click on accept button with wrong answer entered",
				1.0, Variables.getValue("onFail"));
		assertEquals("Reply for wrong answer should be shown",
				getResString(R.string.question_reply_wrong_default),
				tv.getText());
		assertTrue("Button should be enabled in reply on wrong answer mode",
				bt.isEnabled());
		assertEquals("Button label should be proceed",
				getResString(R.string.button_text_proceed), bt.getText());
		assertFalse("In reply on wrong mode edit text should not be shown",
				et.isShown());
		assertFalse(
				"onEnd Variable should not be initialized before proceed button clicked",
				Variables.isDefined("onEnd"));
		bt.performClick();
		assertEquals(
				"onEnd should have been triggered after proceeding after wrong answer",
				1.0, Variables.getValue("onEnd"));
	}

	@Test
	public void goThroughWithRightAnswer() {
		initTestMission("With_Defaults");
		et.setText("Answer One");
		assertFalse(
				"onSuccess Variable should not be initialized before proceed button clicked",
				Variables.isDefined("onSuccess"));
		bt.performClick();
		assertEquals(
				"onSuccess should have been triggered after pressing accept button with right answer entered",
				1.0, Variables.getValue("onSuccess"));
		assertEquals("Reply for correct answer should be shown",
				getResString(R.string.question_reply_correct_default),
				tv.getText());
		assertTrue("Button should be enabled in reply on correct answer mode",
				bt.isEnabled());
		assertEquals("Button label should be proceed",
				getResString(R.string.button_text_proceed), bt.getText());
		assertFalse("In reply on correct mode edit text should not be shown",
				et.isShown());
		assertFalse(
				"onEnd Variable should not be initialized before proceed button clicked",
				Variables.isDefined("onEnd"));
		bt.performClick();
		assertEquals(
				"onEnd should have been triggered after proceeding after wrong answer",
				1.0, Variables.getValue("onEnd"));
	}

	@Test
	public void acceptsAllGivenAnswers() {
		initTestMission("With_Defaults");
		et.setText("Answer One");
		bt.performClick();
		assertEquals("Reply for correct answer should be shown",
				getResString(R.string.question_reply_correct_default),
				tv.getText());
		//
		initTestMission("With_Defaults");
		et.setText("Answer Two");
		bt.performClick();
		assertEquals("Reply for correct answer should be shown",
				getResString(R.string.question_reply_correct_default),
				tv.getText());
		//
		initTestMission("With_Defaults");
		et.setText("Answer Three");
		bt.performClick();
		assertEquals("Reply for correct answer should be shown",
				getResString(R.string.question_reply_correct_default),
				tv.getText());
	}

	@Test
	public void correctAnswerIsStoredInResultVariable() {
		initTestMission("With_Defaults");
		et.setText("Answer One");
		bt.performClick();
		assertTrue("Variable for storing answer should have been registered",
				Variables.isDefined("$_With_Defaults.result"));
		assertEquals(
				"Correct answer should be stored in result variable for this mission",
				"Answer One", Variables.getValue("$_With_Defaults.result"));
	}

	@Test
	public void wrongAnswerIsStoredInResultVariable() {
		initTestMission("With_Defaults");
		et.setText("Something wrong");
		bt.performClick();
		assertTrue("Variable for storing answer should have been registered",
				Variables.isDefined("$_With_Defaults.result"));
		assertEquals(
				"Wrong answer should be stored in result variable for this mission",
				"Something wrong", Variables.getValue("$_With_Defaults.result"));
	}

	@Test
	public void answerEditTextInitializedWithMyHint() {
		initTestMission("With_Hint_Replies");
		assertEquals("Specific hint as given in game spec should be displayed",
				"This is my personal hint for you ...", et.getHint().toString());
		assertEquals("No text should be displayed in answer area initially",
				"", et.getText().toString());
	}

	@Test
	public void myReplyOnCorrectShown() {
		initTestMission("With_Hint_Replies");
		et.setText("Answer One");
		bt.performClick();
		assertEquals("My specific reply for correct answer should be shown",
				"You have chosen the right answer.", tv.getText());
		assertFalse("In reply on correct mode edit text should not be shown",
				et.isShown());
	}

	@Test
	public void myReplyOnWrongShown() {
		initTestMission("With_Hint_Replies");
		et.setText("Some worng answer");
		bt.performClick();
		assertEquals("My specific reply for wrong answer should be shown",
				"Sorry, but this was a wrong answer.", tv.getText());
		assertFalse("In reply on wrong mode edit text should not be shown",
				et.isShown());
	}

	@Test
	public void goRoundWithWrongAnswer() {
		initTestMission("With_Loop");
		et.setText("Something completely wrong");
		assertTrue("Button should be enabled even when wrong text is entered",
				bt.isEnabled());
		bt.performClick();
		assertEquals("Reply for wrong answer should be shown",
				getResString(R.string.question_reply_wrong_default),
				tv.getText());
		assertTrue("Button should be enabled in reply on wrong answer mode",
				bt.isEnabled());
		assertEquals("Button label should be repeat",
				getResString(R.string.button_text_repeat), bt.getText());
		bt.performClick();
		assertEquals("Question text should be shown when repeating",
				"Text of the question.", tv.getText());
		assertEquals("Answer area should be empty again", "", et.getText()
				.toString());
		assertEquals("By repeating edit text should be shown again",
				View.VISIBLE, et.getVisibility());
		assertFalse("Button should be disabled when beginning to repeat",
				bt.isEnabled());
		assertEquals("Button label should be accept",
				getResString(R.string.button_text_accept), bt.getText());
	}

	@Test
	public void ignoreLoopWhenNoAnswerAccepted() {
		initTestMission("Without_AcceptedAnswers");
		assertEquals("In question mode edit text should be shown",
				View.VISIBLE, et.getVisibility());
		et.setText("Something completely wrong");
		assertFalse(
				"onFail Variable should not be initialized before proceed button clicked",
				Variables.isDefined("onFail"));
		bt.performClick();
		assertEquals(
				"onFail should have been triggered after proceeding after wrong answer",
				1.0, Variables.getValue("onFail"));
		assertEquals("Reply for wrong answer should be shown",
				getResString(R.string.question_reply_wrong_default),
				tv.getText());
		assertTrue("Button should be enabled in reply on wrong answer mode",
				bt.isEnabled());
		assertEquals(
				"Button label should be proceed since there is no acceptable answer",
				getResString(R.string.button_text_proceed), bt.getText());
		assertFalse(
				"onEnd Variable should not be initialized before proceed button clicked",
				Variables.isDefined("onEnd"));
		bt.performClick();
		assertEquals(
				"onEnd should have been triggered after proceeding after wrong answer",
				1.0, Variables.getValue("onEnd"));
	}


	// === HELPER METHODS FOLLOW =============================================

}
