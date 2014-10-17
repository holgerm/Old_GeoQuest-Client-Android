package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.Variables;
import com.qeevee.gq.history.History;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.start.LandingScreen;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gq.ui.abstrakt.NPCTalkUI;

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

	// === HELPER METHODS FOLLOW =============================================

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
