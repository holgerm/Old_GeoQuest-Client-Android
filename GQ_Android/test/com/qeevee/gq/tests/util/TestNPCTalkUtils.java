package com.qeevee.gq.tests.util;

import static com.qeevee.gq.tests.util.TestUtils.getFieldValue;

import java.util.Iterator;

import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.mission.NPCTalk.DialogItem;

import android.os.CountDownTimer;
import android.widget.Button;

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
	for (int i = 0; i < dialogItem.getNumberOfTextTokens(); i++) {
	    timer.onTick(100l * (dialogItem.getNumberOfTextTokens() - (i + 1)));
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
