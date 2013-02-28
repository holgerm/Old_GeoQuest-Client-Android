package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.getResString;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.Button;
import android.widget.TextView;

import com.qeevee.gq.history.History;
import com.qeevee.gq.tests.robolectric.GQTestRunner;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.QRTagReadingTreasure;

@RunWith(GQTestRunner.class)
public class QRTagReadingTreasureTests {

    QRTagReadingTreasure mission;
    private int buttonMode;
    private TextView taskTextView;
    private Button button;
    private int START_SCAN;
    @SuppressWarnings("unused")
    private int END_MISSION;
    @SuppressWarnings("unused")
    private String DEFAULT_SCANBUTTONTEXT, DEFAULT_TASKDESCRIPTION;

    @Before
    public void cleanUp() {
	// get rid of all variables that have been set, e.g. for checking
	// actions.
	Variables.clean();
	History.getInstance().clear();
    }

    @SuppressWarnings("unchecked")
    public void initTestMission(String missionID) {
	mission = (QRTagReadingTreasure) prepareMission("QRTagReadingTreasure",
							missionID,
							startGameForTest("QRTagReadingTreasureTest"));
	try {
	    mission.onCreate(null);
	} catch (NullPointerException npe) {
	    fail("Mission with id \""
		    + missionID
		    + "\" missing. (NPE: "
		    + npe.getMessage()
		    + ")");
	}
	loadFieldsOfObjectUnderTest();
    }

    private void loadFieldsOfObjectUnderTest() {
	taskTextView = (TextView) getFieldValue(mission,
						"taskTextView");
	getResString(R.string.button_text_proceed);
	DEFAULT_SCANBUTTONTEXT = getResString(R.string.qrtagreading_startscanbutton_default);
	DEFAULT_TASKDESCRIPTION = getResString(R.string.qrtagreading_taskdescription_default);
	buttonMode = (Integer) getFieldValue(mission,
					     "buttonMode");
	button = (Button) getFieldValue(mission,
					"okButton");
	START_SCAN = (Integer) getFieldValue(mission,
					     "START_SCAN");
	END_MISSION = (Integer) getFieldValue(mission,
					      "END_MISSION");
    }

    // === TESTS FOLLOW =============================================

    @Test
    public void initialization_With_Explicit_Attribute_Values() {
	// GIVEN:
	// nothing

	// WHEN:
	initTestMission("With_Explicit_Attribute_Values");

	// THEN:
	shouldBeInMode(START_SCAN);
	shouldShowTaskText("This is a demo task description.");
	shouldShowButtonText("Start demo scan ...");
    }

    // === HELPER METHODS FOLLOW =============================================

    private void shouldShowTaskText(String expectedText) {
	assertEquals(expectedText,
		     taskTextView.getText().toString());
    }

    private void shouldShowButtonText(String expectedText) {
	assertEquals(expectedText,
		     button.getText().toString());
    }

    private void shouldBeInMode(int expectedMode) {
	assertEquals(expectedMode,
		     buttonMode);
    }

}
