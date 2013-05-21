package edu.bonn.mobilegaming.geoquest.mission;

import org.dom4j.Element;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.BlockableAndReleasable;
import edu.bonn.mobilegaming.geoquest.GeoQuestActivity;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.MissionOrToolActivity;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManager;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;

public abstract class MissionActivity extends GeoQuestActivity implements
		MissionOrToolActivity {
	protected Mission mission;

	public Mission getMission() {
		return mission;
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

	/**
	 * Back button Handler quits the Mission, when back button is hit.
	 */
	@Override
	public boolean onKeyDown(final int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK: // Back => Cancel
			if (mission.cancelStatus == 0) {
				Log.d(this.getClass().getName(),
						"Back Button was pressed, but mission may not be cancelled.");
				return true;
			} else {
				finish(mission.cancelStatus);
				return true;
			}
		case KeyEvent.KEYCODE_SEARCH:
			// ignore search button
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

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

		// TODO -- Sabine -- Result anpassen., bzw. in jeder Mission einzel
		// aufrufen. mission.getResult()....
		// contextManager.setEndValues(mission.id, missionResultInPercent);
		mission.applyOnEndRules();
		GeoQuestApp.getInstance().removeMissionActivity(mission.id);
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
		String id = extras.getString("missionID");
		mission = Mission.get(id);

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
	 *            given order.
	 * @return the corresponding attribute value as specified in the game.xml or
	 *         null if the attribute is optional and not specified and none of
	 *         the alternative attributes are specified.
	 * @throws IllegalArgumentException
	 *             if the attribute is necessary but not given in the game.xml
	 */
	public CharSequence getMissionAttribute(String attributeName,
			int defaultAsResourceID, String... alternativeAttributeNames) {
		CharSequence result = null;
		result = XMLUtilities.getAttribute(attributeName, defaultAsResourceID,
				mission.xmlMissionNode);
		int i = 0;
		while (result == null && alternativeAttributeNames.length > i) {
			result = XMLUtilities.getAttribute(alternativeAttributeNames[i],
					defaultAsResourceID, mission.xmlMissionNode);
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
		return mission.xmlMissionNode;
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

}