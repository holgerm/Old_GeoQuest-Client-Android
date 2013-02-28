package com.qeevee.gq.tests.mission.ui.standard;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.historyListShouldHaveLength;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import com.qeevee.gq.history.History;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.ui.ZoomImageView;

import edu.bonn.mobilegaming.geoquest.Start;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.standard.DefaultUIFactory;
import edu.bonn.mobilegaming.geoquest.ui.standard.NPCTalkUIDefault;

@RunWith(GQTestRunner.class)
public class NPCTalkUIDefaultTests {
    NPCTalk npcTalkM;
    NPCTalkUIDefault ui;
    ZoomImageView imageView;
    TextView talkView;
    Button proceedBT;
    CountDownTimer timer;
    private Start start;
    private NPCTalk npcTalk;

    @SuppressWarnings("unchecked")
    public void initTestMission(String missionID) {
	npcTalkM = (NPCTalk) prepareMission("NPCTalk",
					    missionID,
					    startGameForTest("npctalk/NPCTalkTest"));
	npcTalkM.onCreate(null);
	ui = (NPCTalkUIDefault) getFieldValue(npcTalkM,
					      "ui");
	imageView = (ZoomImageView) getFieldValue(ui,
						  "charImage");
	talkView = (TextView) getFieldValue(ui,
					    "dialogText");
	proceedBT = (Button) getFieldValue(ui,
					   "button");
	timer = (CountDownTimer) getFieldValue(npcTalkM,
					       "myCountDownTimer");
    }

    @After
    public void cleanUp() {
	// get rid of all variables that have been set, e.g. for checking
	// actions.
	Variables.clean();
	History.getInstance().clear();
    }

    @Before
    public void prepare() {
    }

    // === TESTS FOLLOW =============================================

    @SuppressWarnings("unchecked")
    @Test
    public void initGame() {
	// GIVEN:
	start = startGameForTest("npctalk/NPCTalkStandard",
				 DefaultUIFactory.class);
	npcTalk = (NPCTalk) prepareMission("NPCTalk",
					   "m1",
					   start);

	// WHEN:
	npcTalk.onCreate(null);

	// THEN:
	historyListShouldHaveLength(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startFirstMission() {
	// GIVEN:
	start = startGameForTest("npctalk/NPCTalkStandard",
				 DefaultUIFactory.class);
	npcTalk = (NPCTalk) prepareMission("NPCTalk",
					   "m1",
					   start);

	// WHEN:
	npcTalk.onCreate(null);

	// THEN:
    }

    // === HELPER METHODS FOLLOW =============================================

}
