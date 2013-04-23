package com.qeevee.gq.rules.act;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;

public class PlayAudio extends Action {

    @Override
    protected boolean checkInitialization() {
	return (params.containsKey("file"));
    }

    @Override
    public void execute() {
	if (params.containsKey("file"))
	    GameDataManager.playAudio(params.get("file"),
				  false);
    }

}
