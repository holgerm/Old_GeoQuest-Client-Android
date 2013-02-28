package com.qeevee.gq.tests.ui.mock;

import android.view.View;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk.DialogItem;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;

public class NPCTalkUIMock extends NPCTalkUI {

    public NPCTalkUIMock(NPCTalk activity) {
	super(activity);
	showNextDialogItem();
    }

    @Override
    public void onBlockingStateUpdated(boolean isBlocking) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showNextDialogItem() {
	if (getNPCTalk().hasMoreDialogItems()) {
	    DialogItem dialogItem = getNPCTalk().getNextDialogItem();
	    getNPCTalk().hasShownDialogItem(dialogItem);
	}
    }

    @Override
    public View createView() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void finishMission() {
	getNPCTalk().finishMission();
    }

}
