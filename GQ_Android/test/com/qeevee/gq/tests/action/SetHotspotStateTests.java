package com.qeevee.gq.tests.action;

import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static com.qeevee.gq.tests.util.TestUtils.startMissionInGame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.Variables;
import com.qeevee.gq.history.History;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.tests.robolectric.GQTestRunner;

@RunWith(GQTestRunner.class)
public class SetHotspotStateTests {

	protected String gameName;

	protected void setGameName() {
		gameName = "SetHotspotState/FunctionTest";
	}

	@Before
	public void beforeEachTest() {
		// get rid of all variables that have been set, e.g. for checking
		// actions.
		Variables.clean();
		History.getInstance().clear();
		setGameName();
	}

	// === TESTS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	@Test
	public void checkInitialization() {
		// GIVEN:

		// WHEN:
		startGameForTest(gameName);

		// THEN:
		shouldHoldHotpotActivity("hotspot_1", false);
		shouldHoldHotpotActivity("hotspot_2", false);
		shouldHoldHotpotVisibility("hotspot_1", false);
		shouldHoldHotpotVisibility("hotspot_2", false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setAllHotspotsActive() {
		// GIVEN:
		startGameForTest(gameName);

		// WHEN:
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsActive");

		// THEN:
		shouldHoldHotpotActivity("hotspot_1", true);
		shouldHoldHotpotActivity("hotspot_2", true);
		shouldHoldHotpotVisibility("hotspot_1", false);
		shouldHoldHotpotVisibility("hotspot_2", false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setFirstHotspotActive() {
		// GIVEN:
		startGameForTest(gameName);

		// WHEN:
		startMissionInGame(gameName, "NPCTalk", "SetFirstHotspotActive");

		// THEN:
		shouldHoldHotpotActivity("hotspot_1", true);
		shouldHoldHotpotActivity("hotspot_2", false);
		shouldHoldHotpotVisibility("hotspot_1", false);
		shouldHoldHotpotVisibility("hotspot_2", false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setAllHotspotsVisible() {
		// GIVEN:
		startGameForTest(gameName);

		// WHEN:
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsVisible");

		// THEN:
		shouldHoldHotpotActivity("hotspot_1", false);
		shouldHoldHotpotActivity("hotspot_2", false);
		shouldHoldHotpotVisibility("hotspot_1", true);
		shouldHoldHotpotVisibility("hotspot_2", true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setFirstHotspotVisible() {
		// GIVEN:
		startGameForTest(gameName);

		// WHEN:
		startMissionInGame(gameName, "NPCTalk", "SetFirstHotspotVisible");

		// THEN:
		shouldHoldHotpotActivity("hotspot_1", false);
		shouldHoldHotpotActivity("hotspot_2", false);
		shouldHoldHotpotVisibility("hotspot_1", true);
		shouldHoldHotpotVisibility("hotspot_2", false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setAllHotspotsInvisible() {
		// GIVEN:
		startGameForTest(gameName);
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsVisible");

		// WHEN:
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsInvisible");

		// THEN:
		shouldHoldHotpotVisibility("hotspot_1", false);
		shouldHoldHotpotVisibility("hotspot_2", false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setAllHotspotsInvisibleAndInactive() {
		// GIVEN:
		startGameForTest(gameName);
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsActive");
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsVisible");

		// WHEN:
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsInactive");
		startMissionInGame(gameName, "NPCTalk", "SetAllHotspotsInvisible");

		// THEN:
		shouldHoldHotpotActivity("hotspot_1", false);
		shouldHoldHotpotActivity("hotspot_2", false);
		shouldHoldHotpotVisibility("hotspot_1", false);
		shouldHoldHotpotVisibility("hotspot_2", false);
	}

	// === HELPER METHODS FOLLOW =============================================

	private void shouldHoldHotpotActivity(String hotspotID,
			boolean expectedState) {
		Hotspot h = HotspotManager.getInstance().getExisting(hotspotID);
		assertNotNull(h);
		assertEquals(expectedState, h.isActive());
	}

	private void shouldHoldHotpotVisibility(String hotspotID,
			boolean expectedState) {
		Hotspot h = HotspotManager.getInstance().getExisting(hotspotID);
		assertNotNull(h);
		assertEquals(expectedState, h.isVisible());
	}

}
