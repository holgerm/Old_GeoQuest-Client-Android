package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.base.Variables;
import com.qeevee.gq.history.History;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gq.mission.StartAndExitScreen;
import com.qeevee.gq.start.LandingScreen;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gqdefault.R;

@RunWith(GQTestRunner.class)
public class StartScreenMissionTests {
	private View imageView;
	private LandingScreen start;
	private StartAndExitScreen mission;

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
		start = startGameForTest("startscreen/TestOnTap");

		// WHEN:
		mission = (StartAndExitScreen) prepareMission("StartAndExitScreen",
				"TestNrOfTaps", start);
		startMission(mission);

		// THEN:
		assertTrue(Variables.isDefined("nrOfTaps"));
		assertEquals(0.0, Variables.getValue("nrOfTaps"));

		// WHEN:
		performTouch(0, 0);
		assertEquals(1.0, Variables.getValue("nrOfTaps"));

		// WHEN:
		performTouch(0, 0);
		performTouch(0, 0);
		performTouch(0, 0);
		assertEquals(4.0, Variables.getValue("nrOfTaps"));
	}

	@Test
	public void checkDurationDefaultValue() {
		// GIVEN:
		TestUtils.startApp();

		// WHEN:
		CharSequence durationDefault = GeoQuestApp.getInstance().getText(
				R.string.startAndExitScreen_duration_default);

		// THEN:
		shouldBeValidDurationValue(durationDefault);
	}

	// === HELPER METHODS FOLLOW =============================================

	private void shouldBeValidDurationValue(CharSequence durationDefault) {
		try {
			Long.parseLong((String) durationDefault);
		} catch (NumberFormatException nfe) {
			if (!durationDefault.equals("interactive")
					&& !durationDefault.equals("animation"))
				fail();
		}
	}

	private void startMission(MissionActivity mission) {
		mission.onCreate(null);
		imageView = (View) getFieldValue(mission, "fullscreenView");
	}

	private void performTouch(float x, float y) {
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
		imageView.dispatchTouchEvent(event);
	}
}
