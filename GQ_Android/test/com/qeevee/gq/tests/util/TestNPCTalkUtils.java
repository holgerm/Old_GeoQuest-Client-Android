package com.qeevee.gq.tests.util;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;

import java.util.Iterator;

import android.os.CountDownTimer;
import android.widget.Button;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk.DialogItem;

public class TestNPCTalkUtils {

    /**
     * This helper method emulates the Timer used in the real application. It is
     * very specialized according to the implementation, e.g. the delta between
     * word appearance is set to 100ms as in the code.
     */
    public static void letCurrentDialogItemAppearCompletely(NPCTalk npcTalk) {
	CountDownTimer timer = (CountDownTimer) getFieldValue(npcTalk,
							      "myCountDownTimer");
	timer.onFinish();
	DialogItem dialogItem = (DialogItem) getFieldValue(npcTalk,
							   "currItem");
	timer = (CountDownTimer) getFieldValue(npcTalk,
					       "myCountDownTimer");
	for (int i = 0; i < dialogItem.getNumParts(); i++) {
	    timer.onTick(100l * (dialogItem.getNumParts() - (i + 1)));
	}
	timer.onFinish();
    }

    @SuppressWarnings("unchecked")
    public static void forwardUntilLastDialogItemIsShown(NPCTalk npcTalk) {
	Iterator<DialogItem> dialogItemIterator = (Iterator<DialogItem>) getFieldValue(npcTalk,
										       "dialogItemIterator");
	Button button = (Button) getFieldValue(npcTalk,
					       "button");
	while (dialogItemIterator.hasNext()) {
	    letCurrentDialogItemAppearCompletely(npcTalk);
	    button.performClick();
	}
	letCurrentDialogItemAppearCompletely(npcTalk);
    }

}
