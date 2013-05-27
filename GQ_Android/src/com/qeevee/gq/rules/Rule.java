package com.qeevee.gq.rules;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.qeevee.gq.rules.act.Action;
import com.qeevee.gq.rules.act.ActionFactory;
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

	private Condition precondition;
	private List<Action> actions;
	private String missionID;

	/**
	 * Checks the precondition of this rule and if it is fulfilled executes the
	 * actions in the order defined by the according rule xml element.
	 * 
	 * @return wheather the condition is fulfilled and the action list is not
	 *         empty, i.e. wheather actions are executed.
	 */
	public final boolean apply() {
		if (precondition.isFulfilled()) {
			for (Action currentAction : actions) {
				currentAction.execute();
			}
			return true;
		} else
			return false;
	}

	// //////////////// STATIC FACTORY STUFF FOLLOWS: ///////////////////

	private Rule(String id) {
		this.missionID = id;
		this.actions = new ArrayList<Action>();
	}

	/**
	 * An example of a rule xml element is:
	 * 
	 * <pre>
	 * {@code
	 * <rule>
	 *  <if>
	 *   <and>
	 *    <geq>
	 *     <num>5</num>
	 *     <var>points</var>
	 *    </geq>
	 *    <geq>
	 *     <var>points</var>
	 *     <num>9</num>
	 *    </geq>
	 *   </and>
	 *  </if>
	 *  <action type="Vibrate"/>
	 *  <action type="StartMission" id="medium_winner_info"/>
	 * </rule>
	 * }
	 * </pre>
	 * 
	 * This rule checks whether the {@code points} variable is between 5 and 9
	 * (incl.) and if so the mobile device vibrates and the specified mission is
	 * started.
	 * 
	 * @param xmlRuleContent
	 *            the content of the xml element {@code<rule>} which typically
	 *            is {@code<if>...</if><then>...</then>}.
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Rule createFromXMLElement(Element xmlRuleContent, String id) {
		Rule rule = new Rule(id);

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
			actions.add(ActionFactory.create(xmlAction));
		}
	}

}
