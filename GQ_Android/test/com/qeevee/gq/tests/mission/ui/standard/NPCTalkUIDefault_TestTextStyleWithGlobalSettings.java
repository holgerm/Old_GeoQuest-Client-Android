package com.qeevee.gq.tests.mission.ui.standard;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Color;
import android.widget.TextView;

import com.qeevee.gq.history.History;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.xtremelabs.robolectric.Robolectric;

import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionUI;
import edu.bonn.mobilegaming.geoquest.ui.standard.DefaultUIFactory;

@RunWith(GQTestRunner.class)
@Ignore
public class NPCTalkUIDefault_TestTextStyleWithGlobalSettings {

	final private String GAME_NAME = "NPCTalk/TextStyleTestWithGlobalSettings";
	private NPCTalk npcTalk;
	MissionUI ui;
	private TextView textView;

	@Test
	public void globalTextStyle() {
		// GIVEN:

		// WHEN:
		initTestMission("WithGlobalStyle");

		// THEN:
		shouldHaveTextSize(10);
		shouldHaveTextColor(Color.GREEN);
	}

	// @Test
	// public void withLocalStyleWithUnitsAndWords() {
	// // GIVEN:
	//
	// // WHEN:
	// initTestMission("WithLocalStyleWithUnitsAndWords");
	//
	// // THEN:
	// shouldHaveTextSize(D);
	// shouldHaveTextColor(R.color.foreground);
	// }

	// === HELPER METHODS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	public void initTestMission(String missionID) {
		npcTalk = (NPCTalk) TestUtils.prepareMission("NPCTalk", missionID,
				startGameForTest(GAME_NAME, DefaultUIFactory.class));
		npcTalk.onCreate(null);
		ui = (MissionUI) getFieldValue(npcTalk, "ui");
		textView = (TextView) getFieldValue(ui, "dialogText");
	}

	private void shouldHaveTextSize(float expectedTextSize) {
		assertEquals(expectedTextSize, textView.getTextSize(),
				TestUtils.DELTA_4_FLOAT_COMPARISON);
	}

	private void shouldHaveTextColor(int expectedTextColor) {
		assertEquals(Integer.toHexString(expectedTextColor),
				Integer.toHexString(Robolectric.shadowOf(textView)
						.getTextColorHexValue()));
	}

	@After
	public void cleanUp() {
		// get rid of all variables that have been set, e.g. for checking
		// actions.
		Variables.clean();
		History.getInstance().clear();
	}

}
