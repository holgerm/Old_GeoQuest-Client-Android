package com.qeevee.gq.rules.act;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;

import edu.bonn.mobilegaming.geoquest.Variables;

public class If extends Action {

	private Condition condition;
	private List<Action> actions;

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("condition")
				&& elements.containsKey("then") && elements.containsKey("else");
		return initOK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		actions = new ArrayList<Action>();

		Element xmlCondition = (Element) elements.get("condition")
				.selectNodes("*").get(0);
		condition = ConditionFactory.create(xmlCondition);

		if (condition.isFulfilled()) {

			Element xmlThen = elements.get("then");
			List<Element> xmlActionNodes = xmlThen.selectNodes("action");
			for (Element xmlAction : xmlActionNodes) {
				actions.add(ActionFactory.create(xmlAction));
			}
			for (Action currentAction : actions) {
				currentAction.executeSavely();
				if ((Boolean) Variables.getValue(Variables.BREAK_WHILE))
					break;
			}

		} else {

			Element xmlElse = elements.get("else");
			List<Element> xmlActionNodes = xmlElse.selectNodes("action");
			for (Element xmlAction : xmlActionNodes) {
				actions.add(ActionFactory.create(xmlAction));
			}
			for (Action currentAction : actions) {
				currentAction.executeSavely();
				if ((Boolean) Variables.getValue(Variables.BREAK_WHILE))
					break;
			}
		}
	}
}