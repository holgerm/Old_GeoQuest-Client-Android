package com.qeevee.gq.tests.mission.ui.standard;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gq.ui.abstrakt.MissionUI;
import com.qeevee.gq.ui.standard.DefaultUIFactory;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Device;


@RunWith(GQTestRunner.class)
public class NPCTalkUIDefaultGlobalBackgroundColorTests {

	final private String GAME_NAME = "NPCTalk/GlobalBackgroundColorTest";
	private NPCTalk npcTalk;
	MissionUI ui;
	private View ov;

	@Test
	public void withoutLocalBackground() {
		// GIVEN:

		// WHEN:
		initTestMission("WithoutLocalBackground");

		// THEN:
		shouldShowBackgroundColor("#FFaabb11");
	}

	@Test
	@Ignore
	// TODO fix path problems
	public void localBackgroundImageOverridesGlobal() {
		// GIVEN:

		// WHEN:
		initTestMission("WithLocalBgImage");

		// THEN:
		shouldShowBackground("drawable/background.png");
	}

	@Test
	public void localBackgroundColorOverridesGlobal() {
		// GIVEN:

		// WHEN:
		initTestMission("WithoutLocalBgColor");

		// THEN:
		shouldShowBackgroundColor("#FF07c95f");

	}

	// === HELPER METHODS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	public void initTestMission(String missionID) {
		npcTalk = (NPCTalk) TestUtils.prepareMission("NPCTalk", missionID,
				startGameForTest(GAME_NAME, DefaultUIFactory.class));
		npcTalk.onCreate(null);
		ui = (MissionUI) getFieldValue(npcTalk, "ui");
		ov = (View) getFieldValue(ui, "outerView");
	}

	private void shouldShowBackground(String relPath) {
		assertEquals(
				new BitmapDrawable(BitmapUtil.loadBitmap(relPath,
						Device.getDisplayWidth(), 0, false)).getConstantState(),
				ov.getBackground().getConstantState());
	}

	private void shouldShowBackgroundColor(String colorString) {
		assertTrue(ov.getBackground() instanceof ColorDrawable);
		// ColorDrawable cd = (ColorDrawable) ov.getBackground();
		// TODO only possible from API level 11 on:
		// assertEquals(Color.parseColor(colorString), cd.getColor());
	}
}
