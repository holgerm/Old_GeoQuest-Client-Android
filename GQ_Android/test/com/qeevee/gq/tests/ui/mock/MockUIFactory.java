package com.qeevee.gq.tests.ui.mock;

import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.ui.abstrakt.NPCTalkUI;
import com.qeevee.gq.ui.standard.DefaultUIFactory;


public class MockUIFactory extends DefaultUIFactory {

	@Override
	public NPCTalkUI createUI(NPCTalk activity) {
		return new NPCTalkUIMock(activity);
	}

}
