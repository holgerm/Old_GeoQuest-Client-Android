package com.qeevee.gq.history;

import com.qeevee.gq.base.GeoQuestActivity;
import com.qeevee.gq.base.GeoQuestApp;

import android.view.View;
import android.widget.TextView;

/**
 * A TransitionItem is inserted into the history list between missions. It
 * visual representation depends on the kinds of missions it connects. These are
 * determined by the variables {@link TransitionItem#from} and
 * {@link TransitionItem#to}.
 * 
 * @author muegge
 * 
 */
public class TransitionItem extends HistoryItem {

	public TransitionItem(GeoQuestActivity predeccessorActivity) {
		super(predeccessorActivity);
	}

	@Override
	public View getView(View convertView) {
		TextView view = new TextView(GeoQuestApp.getContext());
		String fromType = "undetermined";
		String toType = "undetermined";
		if (getNeighborClass(-1) != null)
			fromType = getNeighborClass(-1).getSimpleName();
		if (getNeighborClass(1) != null)
			toType = getNeighborClass(1).getSimpleName();

		view.setText("Transition between: " + fromType + " and " + toType + ".");
		return view;
	}

	/**
	 * @param dist
	 *            use +1 for successor or -1 for predecessor.
	 * @return
	 */
	protected Class<? extends GeoQuestActivity> getNeighborClass(int dist) {
		History history = History.getInstance();
		int myIndex = history.getIndex(this);
		int neighborIndex = myIndex + dist;
		if (neighborIndex < 0 || neighborIndex >= history.numberOfItems())
			return null;
		else
			return history.getItem(neighborIndex).getActivityType();
	}

}
