package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import edu.bonn.mobilegaming.geoquest.BlockableAndReleasable;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;

public abstract class MissionUI extends GeoQuestUI implements MissionOrToolUI {

    public MissionUI(MissionActivity activity) {
	super(activity);
	ibm = new InteractionBlockingManager(this);
    }

    protected InteractionBlockingManager ibm;

    public BlockableAndReleasable
	    blockInteraction(InteractionBlocker newBlocker) {
	return ibm.blockInteraction(newBlocker);
    }

    public void releaseInteraction(InteractionBlocker blocker) {
	ibm.releaseInteraction(blocker);
    }

}
