package edu.bonn.mobilegaming.geoquest.mission;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;

import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.Variables;

/**
 * A super class for interactive mission that do have a result while the player
 * interacts with them.
 * 
 * Instances of subclasses store the result into a variable
 * {@link Variables#RESULT_SUFFIX} and then call any specified rules for the
 * onInteractionPerformed event.
 * 
 * Examples are {@link QRTagReading} where the Scan is the event triggering
 * interaction, and {@link QuestionAndAnswer} where it is the choice of one
 * answer.
 * 
 * @author muegge
 * 
 */
public abstract class InteractiveMission extends MissionActivity {

	/**
	 * Interactive missions can be specified so that they are restarted again,
	 * if the interaction did not succeed. In this case the onFailed rues are
	 * first executed and then the mission is restarted.
	 * 
	 * Default is {@code false}.
	 * 
	 * TODO discuss problems with specific actions. E.g. what happens if another
	 * mission is started?
	 */
	protected boolean loopUntilSuccess;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// read loop behaviour from game spec:
		CharSequence loopCS = getMissionAttribute("loopUntilSuccess",
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		if (loopCS != null && loopCS.equals("true")) {
			loopUntilSuccess = true;
		} else {
			loopUntilSuccess = false;
		}

		// init interaction rules from game spec:
		addRulesToList(onSuccessRules, "onSuccess/rule");
		for (Rule rule : onSuccessRules) {
			if (rule.leavesMission()) {
				setOnSuccessRulesLeaveMission(true);
				break;
			}
		}
		addRulesToList(onFailRules, "onFail/rule");
		for (Rule rule : onFailRules) {
			if (rule.leavesMission()) {
				setOnFailRulesLeaveMission(true);
				break;
			}
		}
	}

	private List<Rule> onSuccessRules = new ArrayList<Rule>();
	private boolean onSuccessRulesLeaveMission = false;
	private List<Rule> onFailRules = new ArrayList<Rule>();
	private boolean onFailRulesLeaveMission = false;

	protected void invokeOnSuccessEvents() {
		Rule.resetRuleFiredTracker();
		for (Rule rule : onSuccessRules) {
			rule.apply();
		}
	}

	protected void invokeOnFailEvents() {
		Rule.resetRuleFiredTracker();
		for (Rule rule : onFailRules) {
			rule.apply();
		}
	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish(Double status) {
		super.finish(status);
	}

	protected boolean isOnSuccessRulesLeaveMission() {
		return onSuccessRulesLeaveMission;
	}

	protected void setOnSuccessRulesLeaveMission(boolean onSuccessRulesLeaveMission) {
		this.onSuccessRulesLeaveMission = onSuccessRulesLeaveMission;
	}

	protected boolean isOnFailRulesLeaveMission() {
		return onFailRulesLeaveMission;
	}

	protected void setOnFailRulesLeaveMission(boolean onFailRulesLeaveMission) {
		this.onFailRulesLeaveMission = onFailRulesLeaveMission;
	}

}
