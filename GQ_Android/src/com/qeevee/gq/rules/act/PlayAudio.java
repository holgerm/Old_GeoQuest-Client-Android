package com.qeevee.gq.rules.act;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.util.StringTools;

public class PlayAudio extends Action {

	@Override
	protected boolean checkInitialization() {
		return (params.containsKey("file"));
	}

	@Override
	public void execute() {
		if (params.containsKey("file")) {
			boolean loop = StringTools.asBoolean(params.get("loop"));
			GeoQuestApp.playAudio(params.get("file"), loop, false);

		}
	}

}
