package com.qeevee.gq.tests.ui.mock;

import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;
import edu.bonn.mobilegaming.geoquest.ui.standard.DefaultUIFactory;

public class MockUIFactory extends DefaultUIFactory {

    @Override
    public NPCTalkUI createUI(NPCTalk activity) {
	return new NPCTalkUIMock(activity);
    }
    

}
