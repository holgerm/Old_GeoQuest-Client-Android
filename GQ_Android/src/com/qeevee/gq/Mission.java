package com.qeevee.gq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qeevee.gq.mission.InteractiveMission;
import com.qeevee.gq.mission.MissionActivity;
import com.qeevee.gq.rules.Rule;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;

/**
 * The class Mission contains a missionStore, that stores all missions defined
 * in the game definition.
 * 
 */
public class Mission implements Serializable {
	private static final String MISSION_ID = "missionID";

	private static final long serialVersionUID = 1L;

	private static final String PACKAGE_BASE_NAME = MissionActivity
			.getPackageBaseName();
	private static final String LOG_TAG = Mission.class.getName();

	public static final String BACK_ALLOWED = "backAllowed";

	private static boolean useWebLayoutGlobally = false;

	/** TODO: explain; */
	private static Activity mainActivity;

	private Intent startingIntent;
	private Bundle bundleForExternalMission = null;

	/** Hashtable contains all missions, that are defined for a game. */
	private static Hashtable<String, Mission> missionStore = new Hashtable<String, Mission>();

	/**
	 * Hashtable for the completed submissions. Used to test if the player wants
	 * to replay a mission
	 */
	public List<Mission> directSubMissions = new ArrayList<Mission>();

	private List<Rule> onStartRules = new ArrayList<Rule>();
	private List<Rule> onEndRules = new ArrayList<Rule>();

	public Element xmlMissionNode;
	public static Element documentRoot;

	/** the parent Mission. is null for the root mission. */
	private Mission parent;

	/**
	 * true, if all starting requirements are fulfilled. mission is visible on
	 * map only if this is true. updated by the method
	 * update_startingRequirementsFulfilled
	 * */
	boolean startingRequirementsFulfilled;

	/** the status, that this mission gets, when it was cancelled */
	public Double cancelStatus;
	public int achievedPoints = 0;

	public final String id;
	private Class<MissionOrToolActivity> missionType;

	/**
	 * Creates a new mission and stores it in the missionStore. If the
	 * missionStore already contains the id, the corresponding mission will be
	 * overwritten.
	 * 
	 * @param loadHandler
	 *            is used to update the process dialog
	 */
	public static Mission create(String id, Mission parent, Element missionNode) {
		Mission mission;
		if (missionStore.contains(id)) {
			mission = missionStore.get(id);
		} else {
			mission = new Mission(id);
			missionStore.put(id, mission);
		}
		initMission(mission, parent, missionNode);
		return mission;
	}

	/**
	 * @return the Mission defined by id
	 */
	public static Mission get(String id) {
		if (missionStore.containsKey(id))
			return (missionStore.get(id));
		return (new Mission(id));
	}

	static boolean existsMission(String id) {
		return missionStore.containsKey(id);
	}

	static void append(final Mission newMission) {
		missionStore.put(newMission.id, newMission);
	}

	static void initMission(Mission mission, Mission parent, Element missionNode) {
		Log.d(mission.getClass().getName(), "initing mission. id=" + mission.id);
		mission.xmlMissionNode = missionNode;
		mission.setParent(parent);
		mission.loadXML();
	}

	public void setStatus(Double status) {
		Variables.setValue(Variables.SYSTEM_PREFIX + id
				+ Variables.STATUS_SUFFIX, status);
	}

	public Double getStatus() throws IllegalStateException {
		if (Variables.isDefined(id + Variables.STATUS_SUFFIX)) {
			return (Double) Variables.getValue(id + Variables.STATUS_SUFFIX);
		}
		throw new IllegalStateException("Mission state of mission " + id
				+ " not defined!");
	}

	/** runs the appropriate onStart Events when the mission starts */
	public void applyOnStartRules() {
		Rule.resetRuleFiredTracker();
		for (Rule rule : onStartRules) {
			rule.apply();
		}
	}

	/** runs the GQEvents when the mission ends */
	public void applyOnEndRules() {
		Rule.resetRuleFiredTracker();
		for (Rule rule : onEndRules) {
			rule.apply();
		}
	}

	/*
	 * The constructor is used internally to create a new Mission object, that
	 * can be stored in the missionStore.
	 * 
	 * @param id the id of the mission to create
	 */
	Mission(String id) {
		Log.d(getClass().getName(), "creating mission. id=" + id);
		this.id = id;
		this.setStatus(Globals.STATUS_NEW);
	}

	@SuppressWarnings("unchecked")
	private Class<MissionOrToolActivity> missionType() {
		String mType = xmlMissionNode.attributeValue("type");
		try {

			return (Class<MissionOrToolActivity>) Class
					.forName(PACKAGE_BASE_NAME
							+ xmlMissionNode.attributeValue("type"));
		} catch (ClassNotFoundException e) {
			Log.d(LOG_TAG, " Invalid type specified. Mission type not found: "
					+ mType);
			e.printStackTrace();
		}
		// return abstract class as signal for error (could be better with a
		// null object
		return MissionOrToolActivity.class;
	}

	private void loadXML() {
		missionType = missionType();
		startingIntent = new Intent(GeoQuestApp.getContext(), missionType);
		startingIntent.putExtra(MISSION_ID, id);
		setCancelStatus();
		createRules();
		storeDirectSubmissions();
	}

	private void setCancelStatus() {
		String cancelstr = xmlMissionNode.attributeValue("cancel");
		if (cancelstr == null || cancelstr.equals("no")) {
			cancelStatus = 0.0;
		} else if (cancelstr.equals("success")) {
			cancelStatus = Globals.STATUS_SUCCEEDED;
		} else if (cancelstr.equals("fail")) {
			cancelStatus = Globals.STATUS_FAIL;
		} else if (cancelstr.equals("new")) {
			cancelStatus = Globals.STATUS_NEW;
		} else {
			cancelStatus = Globals.STATUS_FAIL;
			Log.d("Mission", "cancel attribute has invalid value: '"
					+ cancelstr + "', mission id='" + id + "'");
		}
	}

	@SuppressWarnings("unchecked")
	private void storeDirectSubmissions() {
		List<Element> directSubMissionElements = xmlMissionNode
				.selectNodes("./mission");
		for (Element e : directSubMissionElements) {
			directSubMissions.add(Mission.create(e.attributeValue("id"), this,
					e));
		}
	}

	/**
	 * Starts the mission using an Intent that only has the minimal extras,
	 * namely the mission ID.
	 * 
	 * @param backAllowed
	 *            true signals that the started mission activity will allow the
	 *            back button to pop the activity from the activity stack.
	 */
	public void startMission(boolean backAllowed) {
		Intent startingIntent = this.startingIntent;
		if (startingIntent != null) {
			String id = startingIntent.getStringExtra(MISSION_ID);
			if (GeoQuestApp.isMissionRunning(id)) {
				Log.e(this.getClass().getName(), "Mission " + id
						+ " is already running and restarted.");
				startingIntent = GeoQuestApp.getInstance()
						.getRunningActivityByID(id).getIntent();
			}
			if (!backAllowed)
				startingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			else
				startingIntent.putExtra(BACK_ALLOWED, backAllowed);
			GameDataManager.stopAudio();
			if (Mission.get(id) != null
					&& Mission.get(id).xmlMissionNode != null)
				getMainActivity().startActivityForResult(startingIntent, 1);
		} else
			Log.e(this.getClass().getName(),
					"Mission can NOT be started since Intent is null.");

	}

	public void startMission() {
		startMission(false);
	}

	/**
	 * Starts the mission, but stores the given parameters as a Bundle to be
	 * used when the Missions Activity is started. It then will be integrated
	 * into the Intent which starts the external Mission.
	 * 
	 * The parameters given here will overwrite those already read when the
	 * Missions XML Element had been parsed, i.e. when loading the game, cf.
	 * loadXML().
	 * 
	 * This has been introduced to support Missions that start external
	 * applications with a set of input parameters which can be defined already
	 * in the triggering action in game.xml.
	 * 
	 * TODO this method should go into a special subtype ExternalMission of
	 * Mission.
	 * 
	 * @param extraArgumentsFromAction
	 *            additional arguments to be given as extras and specified by
	 *            the triggering Action to the external mission (only if an
	 *            external mission gets started).
	 * @param resultDeclarationsFromAction
	 *            additional result declarations specified by the triggering
	 *            Action (only if an external Mission is started).
	 */
	public void startMission(Map<String, String> extraArgumentsFromAction) {
		if (extraArgumentsFromAction != null) {
			if (bundleForExternalMission == null)
				bundleForExternalMission = new Bundle();
			// Add further argument and result declarations as specified in the
			// triggering action and overwrite duplicates:
			Set<String> keySet = extraArgumentsFromAction.keySet();
			String currentKey;
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				currentKey = iterator.next();
				bundleForExternalMission.putString(currentKey,
						extraArgumentsFromAction.get(currentKey));
			}
		}
		startMission(false); // external mission might not know about back
								// allowed flag.
	}

	public static void setMainActivity(Activity mainActivity) {
		Mission.mainActivity = mainActivity;
	}

	public static Activity getMainActivity() {
		return mainActivity;
	}

	public static void clean() {
		Mission.missionStore = new Hashtable<String, Mission>();
		GeoQuestApp.getInstance().clean();
	}

	void setParent(Mission parent) {
		this.parent = parent;
	}

	Mission getParent() {
		return parent;
	}

	public Class<MissionOrToolActivity> getMissionType() {
		return missionType;
	}

	public String toString() {
		return missionType.getSimpleName() + " id=" + id + "; "
				+ super.toString();
	}

	public static boolean isUseWebLayoutGlobally() {
		return useWebLayoutGlobally;
	}

	public static void setUseWebLayoutGlobally(boolean useWebLayout) {
		Mission.useWebLayoutGlobally = useWebLayout;
	}

	private void createRules() {
		addRulesToList(onStartRules, "onStart/rule");
		addRulesToList(onEndRules, "onEnd/rule");
	}

	/**
	 * TODO: Maybe we can move this into class {@link MissionActivity}. And do
	 * the same with a copy of this method in {@link InteractiveMission}.
	 * 
	 * @param ruleList
	 * @param xpath
	 */
	@SuppressWarnings("unchecked")
	private void addRulesToList(List<Rule> ruleList, String xpath) {
		List<Element> xmlRuleNodes;
		xmlRuleNodes = xmlMissionNode.selectNodes(xpath);
		for (Element xmlRule : xmlRuleNodes) {
			ruleList.add(Rule.createFromXMLElement(xmlRule));
		}
	}

	public static Attribute getGlobalAttribute(String attributeName) {
		if (documentRoot.attribute(attributeName) != null)
			return documentRoot.attribute(attributeName);
		else
			return null;
	}
}
