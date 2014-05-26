package com.qeevee.gq.rules;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.qeevee.gq.rules.act.Action;
import com.qeevee.gq.rules.act.ActionFactory;
import com.qeevee.gq.rules.act.LeavesMission;
import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;
import com.qeevee.gq.rules.cond.True;

/**
 * An instance of this class represents a rule xml element defined in the
 * game.xml specification.
 * 
 * This class also offers a static factory method that produces a new instance
 * given an xml rule element.
 * 
 * @author hm
 * 
 */
public class Rule {

	private static boolean ruleFired;
	private Condition precondition;
	private List<Action> actions;

	/**
	 * Checks the precondition of this rule and if it is fulfilled executes the
	 * actions in the order defined by the according rule xml element.
	 * 
	 * @return wheather the condition is fulfilled and the action list is not
	 *         empty, i.e. whether actions are executed.
	 */
	public final boolean apply() {
		if (precondition.isFulfilled()) {
			for (Action currentAction : actions) {
				currentAction.executeSavely();
			}
			Rule.ruleFired = true;
			return true;
		} else
			return false;
	}

	public boolean leavesMission() {
		boolean leavesMission = false;
		for (Action action : actions) {
			if (action instanceof LeavesMission) {
				leavesMission = true;
				break;
			}
		}
		return leavesMission;
	}

	// //////////////// STATIC FACTORY STUFF FOLLOWS: ///////////////////

	private Rule() {
		this.actions = new ArrayList<Action>();
	}

	@SuppressWarnings("unchecked")
	public static Rule createFromXMLElement(Element xmlRuleContent) {
		Rule rule = new Rule();

		// create precondition:
		Element xmlCondition = (Element) xmlRuleContent
				.selectSingleNode("if/*");
		if (xmlCondition == null) {
			/*
			 * In this case the rule does NOT have explicit precondition (if
			 * element). Hence, empty precondition is true
			 */
			rule.precondition = new True();
		} else {
			/*
			 * In this case the rule contains an explicit if element. Set
			 * precondition:
			 */
			rule.precondition = ConditionFactory.create(xmlCondition);
		}

		rule.addActionsToList(xmlRuleContent.selectNodes("action"));

		return rule;
	}

	private void addActionsToList(List<Element> xmlActionNodes) {
		for (Element xmlAction : xmlActionNodes) {
			Action newAction = ActionFactory.create(xmlAction);
			if (newAction != null)
				actions.add(newAction);
		}
	}

	public static void resetRuleFiredTracker() {
		Rule.ruleFired = false;
	}

	public static boolean getRuleFiredTracker() {
		return Rule.ruleFired;
	}

}
