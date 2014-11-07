package com.qeevee.gq.tests.mission.ui.standard;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qeevee.gq.history.History;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.start.LandingScreen;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gq.ui.abstrakt.MissionUI;
import com.qeevee.gq.ui.standard.DefaultUIFactory;
import com.qeevee.gq.ui.standard.NPCTalkUIDefault.WordTicker;

import com.qeevee.gqphka.R;
import com.qeevee.gq.base.Variables;


@RunWith(GQTestRunner.class)
public class NPCTalkUIDefaultTests {
	MissionUI ui;
	ImageView imageView;
	TextView textView;
	Button proceedBT;
	private LandingScreen start;
	private NPCTalk npcTalk;
	private WordTicker ticker;

	public void initUIFields() {
		ui = (MissionUI) getFieldValue(npcTalk, "ui");
		imageView = (ImageView) getFieldValue(ui, "charImage");
		textView = (TextView) getFieldValue(ui, "dialogText");
		proceedBT = (Button) getFieldValue(ui, "button");
		ticker = (WordTicker) getFieldValue(ui, "ticker");
	}

	@After
	public void cleanUp() {
		// get rid of all variables that have been set, e.g. for checking
		// actions.
		Variables.clean();
		History.getInstance().clear();
	}

	// === TESTS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	@Test
	public void startFirstMission_DoNotShowAllWordsYet() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard",
				DefaultUIFactory.class);
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);

		// WHEN:
		startMission(npcTalk);

		// THEN:
		buttonShouldBeDisabled();
		buttonShouldShowNextDialogItemText();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void wordTickerDisplaysTextWordByWord() {
		// GIVEN:
		start = startGameForTest("npctalk/WordCountTest",
				DefaultUIFactory.class);
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "3210WordsInDialogItems",
				start);
		startMission(npcTalk);

		// WHEN:
		ticker.onTick(0);
		// THEN:
		npcTextShouldShowAtEnd("One ");

		// WHEN:
		ticker.onTick(0);
		// THEN:
		npcTextShouldShowAtEnd("two ");

		// WHEN:
		ticker.onTick(0);
		// THEN:
		npcTextShouldShowAtEnd("three\n");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void buttonEnabledOnlyWhenWordTickerFinished() {
		// GIVEN:
		start = startGameForTest("npctalk/WordCountTest",
				DefaultUIFactory.class);
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "3210WordsInDialogItems",
				start);
		startMission(npcTalk);
		ticker.onTick(0);
		ticker.onTick(0);
		ticker.onTick(0);

		// THEN:
		buttonShouldBeDisabled();

		// WHEN:
		ticker.onFinish();
		// THEN:
		npcTextShouldShowAtEnd("One two three\n");
		buttonShouldBeEnabled();
		buttonShouldShowNextDialogItemText();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void goToThroughDialogItems() {
		// GIVEN:
		start = startGameForTest("npctalk/WordCountTest",
				DefaultUIFactory.class);
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "3210WordsInDialogItems",
				start);
		startMission(npcTalk);
		ticker.onFinish();

		// WHEN:
		proceedBT.performClick();

		// THEN:
		buttonShouldBeDisabled();
		shouldStillHaveMoreDialogItemsToShow();

		// WHEN:
		ticker.onFinish();

		// THEN:
		npcTextShouldShowAtEnd("Just two\n");
		buttonShouldBeEnabled();
		shouldStillHaveMoreDialogItemsToShow();

		// WHEN:
		proceedBT.performClick();
		ticker.onFinish();

		// THEN:
		npcTextShouldShowAtEnd("Emptyfollows\n");
		buttonShouldBeEnabled();
		shouldHaveShownAllDialogItems();
	}

	// === HELPER METHODS FOLLOW =============================================

	private void shouldStillHaveMoreDialogItemsToShow() {
		assertTrue(npcTalk.getNumberOfDialogItems() > npcTalk
				.getIndexOfCurrentDialogItem());

	}

	private void shouldHaveShownAllDialogItems() {
		assertEquals(npcTalk.getNumberOfDialogItems(),
				npcTalk.getIndexOfCurrentDialogItem());
	}

	private void startMission(MissionActivity mission) {
		mission.onCreate(null);
		initUIFields();
	}

	private void buttonShouldBeEnabled() {
		assertTrue(proceedBT.isEnabled());
	}

	private void buttonShouldBeDisabled() {
		assertFalse(proceedBT.isEnabled());
	}

	private void buttonShouldShowNextDialogItemText() {
		assertEquals(TestUtils.getResString(R.string.button_text_next),
				proceedBT.getText());
	}

	private void npcTextShouldShowAtEnd(String expectedStringAtEnd) {
		assertTrue(textView.getText().toString().endsWith(expectedStringAtEnd));
	}

}
