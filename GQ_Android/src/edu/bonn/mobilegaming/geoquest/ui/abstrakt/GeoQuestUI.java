package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import android.app.Activity;
import android.view.View;

public abstract class GeoQuestUI {

	protected Activity activity = null;
	protected View contentView = null;

	public GeoQuestUI(Activity activity) {
		this.activity = activity;
		contentView = createContentView();
		activity.setContentView(contentView);
	}

	/**
	 * Creates a view and its components if needed. This view must be assembled
	 * by subclasses. It will immediately be used as content view of this UIs
	 * activity.
	 * 
	 * @return
	 */
	abstract public View createContentView();

}
