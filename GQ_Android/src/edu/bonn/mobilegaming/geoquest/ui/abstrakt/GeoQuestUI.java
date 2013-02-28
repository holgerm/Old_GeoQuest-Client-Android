package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import android.view.View;
import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;

public abstract class GeoQuestUI {

    protected GeoQuestActivity activity = null;
    protected View view = null;

    public GeoQuestUI(GeoQuestActivity activity) {
	this.activity = activity;
	view = createView();
	activity.setContentView(view);
    }

    /**
     * Creates a view and its components if needed.
     * 
     * @return
     */
    abstract public View createView();

}
