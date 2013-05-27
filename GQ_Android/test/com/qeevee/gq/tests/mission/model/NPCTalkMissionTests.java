package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.callMethod;
import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.historyListShouldHaveLength;
import static com.qeevee.gq.tests.util.TestUtils.nthLastItemInHistoryShouldBe;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.history.Actor;
import com.qeevee.gq.history.History;
import com.qeevee.gq.history.TextItem;
import com.qeevee.gq.history.TextType;
import com.qeevee.gq.history.TransitionItem;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;

import edu.bonn.mobilegaming.geoquest.Start;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;

@RunWith(GQTestRunner.class)
public class NPCTalkMissionTests {
	NPCTalkUI ui;
	private Start start;
	private NPCTalk npcTalk;

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
		start = startGameForTest("npctalk/NPCTalkTest");

		// WHEN:
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "With_Defaults", start);

		// THEN:
		shouldHave_NOT_TriggeredOnStartEvent();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStartEvent() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkTest");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "Empty", start);

		// WHEN:
		startMission(npcTalk);

		// THEN:
		shouldHaveTriggeredOnStartEvent();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initGame() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");

		// WHEN:
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);

		// THEN:
		historyListShouldHaveLength(0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void startFirstMission() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);

		// WHEN:
		startMission(npcTalk);

		// THEN:
		historyListShouldHaveLength(1);
		nthLastItemInHistoryShouldBe(1, TextItem.class, TextType.PLAIN,
				Actor.NPC);

		nrOfDialogItemsShouldBe(3);
		indexOfCurrentDialogItemShouldBe(1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void secondDialogItemOfFirstMission() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);
		startMission(npcTalk);

		// WHEN:
		ui.showNextDialogItem();

		// THEN:
		historyListShouldHaveLength(2);
		indexOfCurrentDialogItemShouldBe(2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void thirdAndLastDialogItemOfFirstMission() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);
		startMission(npcTalk);
		ui.showNextDialogItem();

		// WHEN:
		ui.showNextDialogItem();

		// THEN:
		historyListShouldHaveLength(3);
		indexOfCurrentDialogItemShouldBe(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void noMoreDialogItemsAfterLast() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);
		startMission(npcTalk);
		ui.showNextDialogItem();
		ui.showNextDialogItem();

		// WHEN:
		ui.showNextDialogItem();

		// THEN:
		historyListShouldHaveLength(3);
		indexOfCurrentDialogItemShouldBe(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void afterFirstMission() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);
		startMission(npcTalk);
		ui.showNextDialogItem();
		ui.showNextDialogItem();

		// WHEN:
		ui.finishMission();

		// THEN:
		historyListShouldHaveLength(4);
		TestUtils.nthLastItemInHistoryShouldBe(1, TransitionItem.class,
				Actor.GAME);
		TransitionItem transitionItem = (TransitionItem) History.getInstance()
				.getNthLastItem(1);
		shouldHavePredeccessorOfType(transitionItem, NPCTalk.class);
		shouldHaveSucceccessorOfType(transitionItem, null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void startSecondMission() {
		// GIVEN:
		start = startGameForTest("npctalk/NPCTalkStandard");
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m1", start);
		startMission(npcTalk);
		ui.showNextDialogItem();
		ui.showNextDialogItem();
		ui.finishMission();

		// WHEN:
		npcTalk = (NPCTalk) prepareMission("NPCTalk", "m2", start);
		startMission(npcTalk);

		// THEN:
		historyListShouldHaveLength(5);
		TransitionItem transitionItem = (TransitionItem) History.getInstance()
				.getNthLastItem(2);
		shouldHavePredeccessorOfType(transitionItem, NPCTalk.class);
		shouldHaveSucceccessorOfType(transitionItem, NPCTalk.class);
	}

	// === HELPER METHODS FOLLOW =============================================

	private void startMission(MissionActivity mission) {
		mission.onCreate(null);
		ui = (NPCTalkUI) getFieldValue(npcTalk, "ui");
	}

	private void indexOfCurrentDialogItemShouldBe(int expectedIndex) {
		assertEquals(expectedIndex, npcTalk.getIndexOfCurrentDialogItem());
	}

	private void nrOfDialogItemsShouldBe(int expectedNr) {
		assertEquals(expectedNr, npcTalk.getNumberOfDialogItems());
	}

	private void shouldHaveTriggeredOnStartEvent() {
		assertEquals(1.0, Variables.getValue("onStart"));
	}

	private void shouldHave_NOT_TriggeredOnStartEvent() {
		assertFalse(Variables.isDefined("onStart"));
	}

	private void shouldHavePredeccessorOfType(TransitionItem transitionItem,
			Class<?> expectedType) {
		shouldHaveNeighborOfType(transitionItem, expectedType, -1);
	}

	private void shouldHaveSucceccessorOfType(TransitionItem transitionItem,
			Class<NPCTalk> expectedType) {
		shouldHaveNeighborOfType(transitionItem, expectedType, 1);
	}

	private void shouldHaveNeighborOfType(TransitionItem transitionItem,
			Class<?> expectedType, int n) {
		Class<?> realType = (Class<?>) callMethod(transitionItem,
				"getNeighborClass", new Class[] { int.class },
				new Object[] { n });
		assertEquals(expectedType, realType);
	}
}
