package com.qeevee.gq.tests.mission.model;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static com.qeevee.gq.tests.util.TestUtils.getResString;
import static com.qeevee.gq.tests.util.TestUtils.prepareMission;
import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.media.MediaRecorder;
import android.widget.Button;
import android.widget.TextView;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.mission.AudioRecord;

@RunWith(GQTestRunner.class)
public class AudioRecordMissionTests {
    AudioRecord audioRecM;
    TextView taskView;
    TextView activityIndicator; // TODO will be replaced by a composition of
				// text and progress bar
    Button recBT, useBT, playBT;

    @SuppressWarnings("unchecked")
    public void initTestMission(String missionID) {
	audioRecM = (AudioRecord) prepareMission("AudioRecord",
						 missionID,
						 startGameForTest("AudioRecordTest"));
	audioRecM.onCreate(null);
	taskView = (TextView) getFieldValue(audioRecM,
					    "taskView");
	activityIndicator = (TextView) getFieldValue(audioRecM,
						     "activityIndicator");
	recBT = (Button) getFieldValue(audioRecM,
				       "recBT");
	useBT = (Button) getFieldValue(audioRecM,
				       "useBT");
	playBT = (Button) getFieldValue(audioRecM,
					"playBT");
    }

    @After
    public void cleanUp() {
	// get rid of all variables that have been set, e.g. for checking
	// actions.
	Variables.clean();
    }

    @Test
    public void startEventTriggered() {
	assertFalse("onStart Variable should not be initialized at beginning of test",
		    Variables.isDefined("onStart"));
	initTestMission("With_Defaults");
	assertEquals("onStart should have set variable to 1",
		     1.0,
		     Variables.getValue("onStart"));
    }

    @Test
    public void walkTroughDirectly() {
	initTestMission("With_Defaults");
	assertInitialMode();
	recBT.performClick();
	assertRecordingMode();
	recBT.performClick();
	assertReadyMode();
	checkFinalUseButtonClick();
    }

    @Test
    public void walkThroughWithRepeatedRecording() {
	initTestMission("With_Defaults");
	assertInitialMode();
	recBT.performClick();
	assertRecordingMode();
	recBT.performClick();
	assertReadyMode();
	recBT.performClick();
	assertRecordingMode();
	recBT.performClick();
	assertReadyMode();
	checkFinalUseButtonClick();
    }

    @Test
    public void walkThroughWithReplay() {
	initTestMission("With_Defaults");
	assertInitialMode();
	recBT.performClick();
	assertRecordingMode();
	recBT.performClick();
	assertReadyMode();
	playBT.performClick();
	assertPlayingMode();
	playBT.performClick();
	assertReadyMode();
	checkFinalUseButtonClick();
    }

    public void assertInitialMode() {
	assertEquals("Mode variable should be set to initial mode.",
		     (Integer) AudioRecord.MODE_INITIAL,
		     (Integer) getFieldValue(audioRecM,
					     "mode"));
	assertEquals("Task text as given in game spec should be displayed in initial mode.",
		     "Special text to ask the player to record some audio.",
		     taskView.getText());
	assertEquals("Activity Indicator should display correct text in initial mode.",
		     getResString(R.string.audiorecord_activityIndicator_initial),
		     activityIndicator.getText());
	assertTrue("Record button should be enabled in initial mode.",
		   recBT.isEnabled());
	assertEquals("The record button should be tagged with record in initial mode",
		     AudioRecord.BUTTON_TAG_RECORD,
		     recBT.getTag());
	assertFalse("Use button should be disabled in initial mode.",
		    useBT.isEnabled());
	assertFalse("Play button should be disabled in initial mode.",
		    playBT.isEnabled());
	assertEquals("The play button should be tagged with play in initial mode",
		     AudioRecord.BUTTON_TAG_PLAY,
		     playBT.getTag());
    }

    private void assertRecordingMode() {
	assertEquals("Mode variable should be set to recording mode.",
		     (Integer) AudioRecord.MODE_RECORDING,
		     (Integer) getFieldValue(audioRecM,
					     "mode"));
	assertEquals("Task text as given in game spec should be displayed in recording mode.",
		     "Special text to ask the player to record some audio.",
		     taskView.getText());
	assertEquals("Activity Indicator should display correct text in recording mode.",
		     getResString(R.string.audiorecord_activityIndicator_recording),
		     activityIndicator.getText());
	assertTrue("Stop recording button should be enabled in initial mode.",
		   recBT.isEnabled());
	assertEquals("The record button should be tagged with stop_recording in recording mode",
		     AudioRecord.BUTTON_TAG_STOP_RECORDING,
		     recBT.getTag());
	assertNotNull("Media Recorder should not be null while recording.",
		      (MediaRecorder) getFieldValue(audioRecM,
						    "mRecorder"));
	assertFalse("Use button should be disabled in recording mode.",
		    useBT.isEnabled());
	assertFalse("Play button should be disabled in recording mode.",
		    playBT.isEnabled());
	assertEquals("The play button should be tagged with play in recording mode",
		     AudioRecord.BUTTON_TAG_PLAY,
		     playBT.getTag());
    }

    private void assertReadyMode() {
	assertEquals("Mode variable should be set to ready mode.",
		     (Integer) AudioRecord.MODE_READY,
		     (Integer) getFieldValue(audioRecM,
					     "mode"));
	assertEquals("Task text as given in game spec should be displayed in ready mode.",
		     "Special text to ask the player to record some audio.",
		     taskView.getText());
	assertEquals("Activity Indicator should display correct text in ready mode.",
		     getResString(R.string.audiorecord_activityIndicator_ready),
		     activityIndicator.getText());
	assertTrue("Record button should be enabled in ready mode.",
		   recBT.isEnabled());
	assertEquals("Record button should be tagged with record in ready mode",
		     AudioRecord.BUTTON_TAG_RECORD,
		     recBT.getTag());
	assertTrue("Use button should be enabled in ready mode.",
		   useBT.isEnabled());
	assertTrue("Play button should be enabled in ready mode.",
		   playBT.isEnabled());
	assertEquals("The play button should be tagged with play in ready mode",
		     AudioRecord.BUTTON_TAG_PLAY,
		     playBT.getTag());
    }

    private void assertPlayingMode() {
	assertEquals("Mode variable should be set to playing mode.",
		     (Integer) AudioRecord.MODE_PLAYING,
		     (Integer) getFieldValue(audioRecM,
					     "mode"));
	assertEquals("Task text as given in game spec should be displayed in playing mode.",
		     "Special text to ask the player to record some audio.",
		     taskView.getText());
	assertEquals("Activity Indicator should display correct text in playing mode.",
		     getResString(R.string.audiorecord_activityIndicator_playing),
		     activityIndicator.getText());
	assertFalse("Record button should be disabled in ready mode.",
		    recBT.isEnabled());
	assertEquals("Record button should be tagged with record in ready mode",
		     AudioRecord.BUTTON_TAG_RECORD,
		     recBT.getTag());
	assertFalse("Use button should be disabled in ready mode.",
		    useBT.isEnabled());
	assertTrue("Play button should be enabled in ready mode.",
		   playBT.isEnabled());
	assertEquals("The play button should be tagged with play in ready mode",
		     AudioRecord.BUTTON_TAG_STOP_PLAYING,
		     playBT.getTag());
    }

    public void checkFinalUseButtonClick() {
	assertFalse("onSuccess Variable should not be initialized before clicking use button.",
		    Variables.isDefined("onSuccess"));
	assertFalse("onEnd Variable should not be initialized before clicking use button.",
		    Variables.isDefined("onEnd"));
	useBT.performClick();
	assertEquals("onSuccess should have set variable to 1 after clicking use button.",
		     1.0,
		     Variables.getValue("onSuccess"));
	assertEquals("onEnd should have set variable to 1 after clicking use button.",
		     1.0,
		     Variables.getValue("onEnd"));
    }

}
