package edu.bonn.mobilegaming.geoquest.mission;

import org.dom4j.Element;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.qeevee.gq.history.HistoryActivity;
import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.BlockableAndReleasable;
import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.MissionOrToolActivity;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManager;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public abstract class MissionActivity extends GeoQuestActivity implements
		MissionOrToolActivity {
	protected Mission mission;
	protected String id;

	public Mission getMission() {
		return mission;
	}

	private boolean keepsActivity = false;

	public void setKeepActivity(boolean keep) {
		keepsActivity = keep;
	}

	public boolean keepsActivity() {
		return keepsActivity;
	}

	private boolean backAllowed;

	public void setBackAllowed(boolean backAllowed) {
		this.backAllowed = backAllowed;
	}

	public boolean isBackAllowed() {
		return backAllowed;
	}

	@Override
	protected void onDestroy() {
		if (getUI() != null)
			getUI().release();
		super.onDestroy();
	}

	private ContextManager contextManager;
	protected int missionResultInPercent = 100;

	/** Intent used to return values to the parent mission */
	protected Intent result;

	public static String getPackageBaseName() {
		String className = MissionActivity.class.getName();
		int indexOfLastDot = className.lastIndexOf('.');
		return className.substring(0, indexOfLastDot + 1);
	}

	@Override
	public void onBackPressed() {
		if (isBackAllowed())
			super.onBackPressed();
	}

	// /**
	// * Back button Handler quits the Mission, when back button is hit.
	// */
	// @Override
	// public boolean onKeyDown(final int keyCode, KeyEvent event) {
	// // switch (keyCode) {
	// //
	// // case KeyEvent.KEYCODE_BACK: // Back => Cancel
	// // if (mission.cancelStatus == 0) {
	// // Log.d(this.getClass().getName(),
	// // "Back Button was pressed, but mission may not be cancelled.");
	// // return true;
	// // } else {
	// // finish(mission.cancelStatus);
	// // return true;
	// // }
	// // case KeyEvent.KEYCODE_SEARCH:
	// // // ignore search button
	// // break;
	// // }
	// return super.onKeyDown(keyCode, event);
	// }

	/**
	 * Finishes the mission activity and sets the result code
	 * 
	 * TODO only real stati should be allowed, i.e. FAIL and SUCCESS. Hence, we
	 * can use boolena here and make calls much more readable. Cf.
	 * {@link MultipleChoiceQuestion}.
	 * 
	 * @param status
	 *            Mission.STATUS_SUCCESS or FAIL or NEW
	 */
	public void finish(Double status) {
		mission.setStatus(status);
		mission.applyOnEndRules();
		GeoQuestApp.getInstance().removeMissionActivity(mission.id);
		if (!keepsActivity())
			finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Called by the android framework when the mission is created. Reads some
	 * basic informations from the xml file.
	 * 
	 * @param savedInstanceState
	 *            Bundle with the id of the mission. If null then this is the
	 *            first mission and the id is zero.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(this.getClass().getName(), "creating activity");
		ibm = new InteractionBlockingManager(this);

		// get extras
		Bundle extras = getIntent().getExtras();
		id = extras.getString("missionID");
		mission = Mission.get(id);
		setBackAllowed(extras.getBoolean(Mission.BACK_ALLOWED, false));

		contextManager = GeoQuestActivity.contextManager;
		if (contextManager != null)
			contextManager.setStartValues(id);

		mission.setStatus(Globals.STATUS_RUNNING);
		mission.applyOnStartRules();

		super.onCreate(savedInstanceState);
	}

	/**
	 * TODO should be moved to an XMLHelper class as a static helper method.
	 * Requires the mission node to be passed as additional argument.
	 * 
	 * TODO create versions for boolean and Integer also.
	 * 
	 * Also move the static flag NECESSARY_ATTRIBUTE.
	 * 
	 * @param attributeName
	 * @param defaultAsResourceID
	 *            either a defined resource string or
	 *            {@link XMLUtilities#NECESSARY_ATTRIBUTE} to specify that this
	 *            attribute MUST be given explicitly, or
	 *            {@link XMLUtilities#OPTIONAL_ATTRIBUTE} to state that it can
	 *            be omitted even without a default. In the latter case the
	 *            method returns null.
	 * @param alternativeAttributeNames
	 *            list of attribute names that are used in case the
	 *            attributeName does not result in a value. They are used in
	 *            given order, i.e. the first found attribute value is used.
	 * @return the corresponding attribute value as specified in the game.xml or
	 *         null if the attribute is optional and not specified and none of
	 *         the alternative attributes are specified.
	 * @throws IllegalArgumentException
	 *             if the attribute is necessary but not given in the game.xml
	 */
	public CharSequence getMissionAttribute(String attributeName,
			int defaultAsResourceID, String... alternativeAttributeNames) {
		CharSequence result = null;
		result = XMLUtilities.getStringAttribute(attributeName,
				defaultAsResourceID, mission.xmlMissionNode);
		int i = 0;
		while (result == null && alternativeAttributeNames.length > i) {
			result = XMLUtilities.getStringAttribute(
					alternativeAttributeNames[i], defaultAsResourceID,
					mission.xmlMissionNode);
			i++;
		}
		return result;
	}

	/**
	 * Same as {@link #getMissionAttribute(String, int)} but assuming an
	 * optional attribute (cf. {@link XMLUtilities#OPTIONAL_ATTRIBUTE}).
	 * 
	 * @param attributeName
	 * @return
	 */
	public CharSequence getMissionAttribute(String attributeName,
			String... alternativeAttributeNames) {
		return getMissionAttribute(attributeName,
				XMLUtilities.OPTIONAL_ATTRIBUTE, alternativeAttributeNames);
	}

	public Element getXML() {
		return Mission.get(id).xmlMissionNode;
	}

	// TODO Blocking stuff should be moved to UI class:
	protected InteractionBlockingManager ibm;
	protected View outerView;

	public BlockableAndReleasable blockInteraction(InteractionBlocker newBlocker) {
		return ibm.blockInteraction(newBlocker);
	}

	public void releaseInteraction(InteractionBlocker blocker) {
		ibm.releaseInteraction(blocker);
	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO Auto-generated method stub

	}

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_gqactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_endGame:
			GeoQuestApp.getInstance().endGame();
			return true;
		case R.id.menu_history:
			Intent historyActivity = new Intent(getBaseContext(),
					HistoryActivity.class);
			startActivity(historyActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}