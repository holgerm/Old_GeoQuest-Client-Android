package com.qeevee.gq.tests.rules;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;

import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;

@RunWith(GQTestRunner.class)
public class NoRuleFiredYetTest {
	NPCTalkUI ui;

	@Test
	public void checkRulesForFiring() {
		// GIVEN:
		init();

		// WHEN:
		ui.finishMission();

		// THEN:
		assertEquals(0.0, Variables.getValue("shouldNotHaveFired"));
		assertEquals(1.0, Variables.getValue("shouldHaveFired"));
	}

	// === HELPER METHODS FOLLOW =============================================

	public void init() {
		Variables.clean();
		@SuppressWarnings("unchecked")
		NPCTalk npct = (NPCTalk) TestUtils.prepareMission("NPCTalk", "Intro",
				startGameForTest("RuleTests/NoRuleFiredYetGame"));
		npct.onCreate(null);
		ui = (NPCTalkUI) getFieldValue(npct, "ui");
	}

}
