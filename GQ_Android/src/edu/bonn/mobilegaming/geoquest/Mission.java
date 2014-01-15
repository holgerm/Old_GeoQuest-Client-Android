package edu.bonn.mobilegaming.geoquest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;
import edu.bonn.mobilegaming.geoquest.mission.InteractiveMission;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;

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

	private static boolean useWebLayoutGlobally = false;
	private boolean useWebLayout = useWebLayoutGlobally;

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
	private Class<MissionActivity> missionType;

	/**
	 * Creates a new mission and stores it in the missionStore. If the
	 * missionStore already contains the id, the corresponding mission will be
	 * overwritten.
	 * 
	 * @param loadHandler
	 *            is used to update the process dialog
	 */
	public static Mission create(String id, Mission parent,
			Element missionNode, Handler loadHandler) {
		Mission mission;
		if (missionStore.contains(id)) {
			mission = missionStore.get(id);
		} else {
			mission = new Mission(id);
			missionStore.put(id, mission);
		}
		initMission(mission, parent, missionNode, loadHandler);
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

	static void initMission(Mission mission, Mission parent,
			Element missionNode, Handler loadHandler) {
		Log.d(mission.getClass().getName(), "initing mission. id=" + mission.id);
		mission.xmlMissionNode = missionNode;
		mission.setParent(parent);
		mission.loadXML(loadHandler);
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
	private Class<MissionActivity> missionType() {
		String mType = xmlMissionNode.attributeValue("type");
		try {
			if (useWebLayout) {
				return (Class<MissionActivity>) Class.forName(PACKAGE_BASE_NAME
						+ "WebTech");
			} else {
				return (Class<MissionActivity>) Class.forName(PACKAGE_BASE_NAME
						+ xmlMissionNode.attributeValue("type"));
			}
		} catch (ClassNotFoundException e) {
			Log.d(LOG_TAG, " Invalid type specified. Mission type not found: "
					+ mType);
			e.printStackTrace();
		}
		// return abstract class as signal for error (could be better with a
		// null object
		return MissionActivity.class;
	}

	private void loadXML(Handler loadHandler) {
		missionType = missionType();
		chooseMissionLayout();
		startingIntent = new Intent(getMainActivity(), missionType);
		startingIntent.putExtra(MISSION_ID, id);
		setCancelStatus();
		createRules();
		storeDirectSubmissions(loadHandler);
		// Read parameters which can be used as extras to start another activity
		// from within this Missions Activity. This has been introduced for
		// ExternalMissions.
		createBundleForExternalMission();
		sendProgressToHandler(loadHandler);
	}

	private void sendProgressToHandler(Handler loadHandler) {
		if (loadHandler != null)
			loadHandler.sendEmptyMessage(GeoQuestProgressHandler.MSG_PROGRESS);
	}

	private void chooseMissionLayout() {
		String layoutAttr = xmlMissionNode.attributeValue("layout");
		if (layoutAttr != null) {
			// no attribute => no action, use default or global settings instead

			if (layoutAttr.equals("html")) {
				// layout attribute set to html => activate HTML Missions:
				useWebLayout = true;
			} else {
				// layout attribute set to other than html (only "native"
				// allowed cf. Schema) => deactivate HTML Missions:
				useWebLayout = false;
			}
		}
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
	private void storeDirectSubmissions(Handler loadHandler) {
		List<Element> directSubMissionElements = xmlMissionNode
				.selectNodes("./mission");
		for (Element e : directSubMissionElements) {
			directSubMissions.add(Mission.create(e.attributeValue("id"), this,
					e, loadHandler));
		}
	}

	private void createBundleForExternalMission() {
		// TODO: This method should probably go into a specific subclass of
		// Mission which encapsulates the specifics of ExternalMissions.
		Element parametersElement = (Element) xmlMissionNode
				.selectSingleNode("parameters");
		if (parametersElement != null) {
			Map<String, String> arguments = XMLUtilities
					.extractParameters(parametersElement);
			if (bundleForExternalMission == null)
				bundleForExternalMission = new Bundle();
			Set<String> keys = arguments.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator
					.hasNext();) {
				String currentKey = iterator.next();
				bundleForExternalMission.putString(currentKey,
						arguments.get(currentKey));
			}
		}
	}

	public Bundle getBundleForExternalMission() {
		return bundleForExternalMission;
	}

	/**
	 * Starts the mission using an Intent that only has the minimal extras,
	 * namely the mission ID.
	 */
	public void startMission() {
		Intent startingIntent = this.startingIntent;
		if (GeoQuestApp.useAdaptionEngine) {
			String alternMissionId = GeoQuestApp.adaptionEngine
					.getAlternativeMission(id);
			if (!alternMissionId.equals(id)) {
				if (Mission.existsMission(alternMissionId)) {
					AlternativeMission alternativeM = (AlternativeMission) Mission
							.get(alternMissionId);
					alternativeM.setPlaceholderId(id);
					startingIntent = ((Mission) alternativeM).startingIntent;
				} else {
					AlternativeMission alternativeM = AlternativeMission
							.create(alternMissionId, id);
					startingIntent = ((Mission) alternativeM).startingIntent;
				}
			}
		}

		if (startingIntent != null) {
			String id = startingIntent.getStringExtra(MISSION_ID);
			if (GeoQuestApp.isMissionRunning(id)) {
				Log.e(this.getClass().getName(), "Mission " + id
						+ " is already running and not started again.");
				return;
			}
			GameDataManager.stopAudio();
			getMainActivity().startActivityForResult(startingIntent, 1);
		} else
			Log.e(this.getClass().getName(),
					"Mission can NOT be started since Intent is null.");

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
		startMission();
	}

	public static void setMainActivity(Activity mainActivity) {
		Mission.mainActivity = mainActivity;
	}

	public static Activity getMainActivity() {
		return mainActivity;
	}

	public static void clean() {
		Mission.missionStore = new Hashtable<String, Mission>();
	}

	void setParent(Mission parent) {
		this.parent = parent;
	}

	Mission getParent() {
		return parent;
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
}
