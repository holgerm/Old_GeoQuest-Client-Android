package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;

import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.Question;

@RunWith(GQTestRunner.class)
public class TextQuestionMissionRegExpTests {
	Question tq;
	TextView tv;
	EditText et;
	Button bt;

	@Test
	public void checkWordsAcceptingRegExp() {
		// GIVEN:
		// WHEN:
		// were doing it all in one here, since it is more readable this time:

		// THEN:  
		shouldRejectAnswer("");
		shouldAcceptAndStoreAnswer("Asterix der Gallier");
		shouldAcceptAndStoreAnswer("Asterix");
	}

	// === HELPER METHODS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	private void initTestMission(String missionID) {
		Variables.clean();
		tq = (Question) TestUtils.prepareMission("TextQuestion", missionID,
				startGameForTest("TextQuestion/FunctionTest"));
		tq.onCreate(null);
		tv = (TextView) getFieldValue(tq, "textView");
		et = (EditText) getFieldValue(tq, "answerEditText");
		bt = (Button) getFieldValue(tq, "button");
	}

	private void shouldAcceptAndStoreAnswer(String answer) {
		initTestMission("RexExp_WordsAccepted");
		et.setText(answer);
		assertTrue(bt.performClick());
		assertTrue(Variables.isDefined("testVariable"));
		assertEquals(answer, Variables.getValue("testVariable"));
	}

	private void shouldRejectAnswer(String answer) {
		initTestMission("RexExp_WordsAccepted");
		et.setText(answer);
		assertFalse(bt.isEnabled());
		bt.performClick();
		assertFalse(Variables.isDefined("testVariable"));
	}
}
