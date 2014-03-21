package com.qeevee.gq.tests.mission.ui.standard;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Util;

import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionUI;
import edu.bonn.mobilegaming.geoquest.ui.standard.DefaultUIFactory;

@RunWith(GQTestRunner.class)
public class NPCTalkUIDefaultGlobalBackgroundImageTests {

	final private String GAME_NAME = "NPCTalk/GlobalBackgroundImageTest";
	private NPCTalk npcTalk;
	MissionUI ui;
	private View ov;

	@Test
	public void withoutLocalBackground() {
		// GIVEN:

		// WHEN:
		initTestMission("WithoutLocalBackground");

		// THEN:
		shouldShowBackground("drawable/background.png");
	}

	@Test
	public void localBackgroundImageOverridesGlobal() {
		// GIVEN:

		// WHEN:
		initTestMission("WithLocalBgImage");

		// THEN:
		shouldShowBackground("drawable/background2.png");
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
//		Bitmap expectedBitmap = new BitmapDrawable(BitmapUtil.loadBitmap(
//				relPath, Util.getDisplayWidth(), 0, false)).getBitmap();
//		Bitmap foundBitmap = ((BitmapDrawable) ov.getBackground()).getBitmap();
//		assertTrue(expectedBitmap.sameAs(foundBitmap));
	}

	private void shouldShowBackgroundColor(String colorString) {
		assertTrue(ov.getBackground() instanceof ColorDrawable);
		// ColorDrawable cd = (ColorDrawable) ov.getBackground();
		// TODO only possible from API level 11 on:
		// assertEquals(Color.parseColor(colorString), cd.getColor());
	}
}
