package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import com.qeevee.gq.base.Variablesables;
import com.qeevee.gq.history.History;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.start.LandingScreen;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gq.ui.abstrakt.NPCTalkUI;
import com.qeevee.gq.ui.standard.DefaultUIFactory;
import com.qeevee.gq.ui.standard.NPCTalkUIDefault;

@RunWith(GQTestRunner.class)
public class NPCTalk_TextWithImage_Tests {
	NPCTalkUI ui;
	private LandingScreen start;
	private NPCTalk npcTalk;

	String gameName = "npctalk/TextWithImageTest";

	@After
	public void cleanUp() {
		// get rid of all variables that have been set, e.g. for checking
		// actions.
		Variables.clean();
		History.getInstance().clear();
	}

	@Before
	public void prepare() {
		TestUtils.setMockUIFactory();
	}

	// === TESTS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	@Test
	public void testBeforeStartEvent() {
		// GIVEN:
		start = startGameForTest(gameName);

		// WHEN:
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "TextWithImageInNPCTalk",
				start);

		// THEN:
		shouldHave_NOT_TriggeredOnStartEvent();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStartEvent() {
		// GIVEN:
		start = startGameForTest(gameName);
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "TextWithImageInNPCTalk",
				start);

		// WHEN:
		startMission(npcTalk);

		// THEN:
		shouldHaveTriggeredOnStartEvent();
		assertEquals(1, npcTalk.getNumberOfDialogItems());
		// TODO check mode == chunk; text is correct, sound file set right.
		// TODO check button enabled and pressing triggers onEnd
	}

	@SuppressWarnings("unchecked")
	@Test
	public void checkUI() {
		// GIVEN:
		start = startGameForTest(gameName, DefaultUIFactory.class);
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "TextWithImageInNPCTalk",
				start);

		// WHEN:
		startMission(npcTalk);

		// THEN:
		shouldShowText("The text is now in an attribute named text.");
		shouldProceedButtonBeEnabled(true);

	}

	// === HELPER METHODS FOLLOW =============================================

	/**
	 * You MUST use the DefaultUIFactory in your test before you can call this
	 * check method.
	 * 
	 * @param b
	 */
	private void shouldProceedButtonBeEnabled(boolean expectedButtonState) {
		if (!(ui instanceof NPCTalkUIDefault)) {
			fail("You MUST use the DefaultUIFactory for this test.");
		}

		Button b = (Button) getFieldValue(ui, "button");
		assertEquals(expectedButtonState, b.isEnabled());
	}

	/**
	 * You MUST use the DefaultUIFactory in your test before you can call this
	 * check method.
	 * 
	 * @param string
	 */
	private void shouldShowText(String expectedText) {
		if (!(ui instanceof NPCTalkUIDefault)) {
			fail("You MUST use the DefaultUIFactory for this test.");
		}

		TextView tv = (TextView) getFieldValue(ui, "dialogText");
		assertEquals(Html.fromHtml(expectedText) + "\n", tv.getText());
	}

	private void startMission(MissionActivity mission) {
		mission.onCreate(null);
		ui = (NPCTalkUI) getFieldValue(npcTalk, "ui");
	}

	private void shouldHaveTriggeredOnStartEvent() {
		assertEquals(1.0, Variables.getValue("onStart"));
	}

	private void shouldHave_NOT_TriggeredOnStartEvent() {
		assertFalse(Variables.isDefined("onStart"));
	}

}
