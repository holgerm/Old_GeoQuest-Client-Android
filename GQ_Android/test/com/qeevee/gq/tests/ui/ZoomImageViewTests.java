package com.qeevee.gq.tests.ui;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.view.View;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.ui.ZoomImageView;

import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;
import edu.bonn.mobilegaming.geoquest.ui.standard.DefaultUIFactory;

@RunWith(GQTestRunner.class)
public class ZoomImageViewTests {

    NPCTalk npcTalkM;
    NPCTalkUI ui;
    ZoomImageView imageView;

    // === TESTS FOLLOW =============================================

    @Test
    public void correctPathToBitmap() {
	// GIVEN:

	// WHEN:
	startMissionWithCorrectPathToBitmap();

	// THEN:
	shouldShowImage(true);
    }

    @Test
    public void wrongPathToBitmap() {
	// GIVEN:

	// WHEN:
	startMissionWithWrongPathToBitmap();

	// THEN:
	shouldShowImage(false);
    }

    @Test
    public void pathToNonBitmapFile() {
	// GIVEN:

	// WHEN:
	startMissionWithPathToNonBitmapFile();

	// THEN:
	shouldShowImage(false);
    }

    // === HELPER METHODS FOLLOW =============================================

    private void shouldShowImage(boolean shouldShow) {
	if (shouldShow)
	    assertEquals(View.VISIBLE,
			 imageView.getVisibility());
	else
	    assertEquals(View.GONE,
			 imageView.getVisibility());
    }

    @SuppressWarnings("unchecked")
    private void startMissionWithCorrectPathToBitmap() {
	npcTalkM = (NPCTalk) TestUtils
		.startMissionInGame("ZoomImageViewTest",
				    "NPCTalk",
				    "WithCorrectPathToBitmap",
				    DefaultUIFactory.class);
	ui = (NPCTalkUI) getFieldValue(npcTalkM,
				       "ui");
	imageView = (ZoomImageView) getFieldValue(ui,
						  "charImage");
    }

    @SuppressWarnings("unchecked")
    private void startMissionWithWrongPathToBitmap() {
	npcTalkM = (NPCTalk) TestUtils
		.startMissionInGame("ZoomImageViewTest",
				    "NPCTalk",
				    "WithWrongPathToBitmap",
				    DefaultUIFactory.class);
	ui = (NPCTalkUI) getFieldValue(npcTalkM,
				       "ui");
	imageView = (ZoomImageView) getFieldValue(ui,
						  "charImage");
    }

    @SuppressWarnings("unchecked")
    private void startMissionWithPathToNonBitmapFile() {
	npcTalkM = (NPCTalk) TestUtils
		.startMissionInGame("ZoomImageViewTest",
				    "NPCTalk",
				    "WithPathToNonBitmapFile",
				    DefaultUIFactory.class);
	ui = (NPCTalkUI) getFieldValue(npcTalkM,
				       "ui");
	imageView = (ZoomImageView) getFieldValue(ui,
						  "charImage");
    }

}
