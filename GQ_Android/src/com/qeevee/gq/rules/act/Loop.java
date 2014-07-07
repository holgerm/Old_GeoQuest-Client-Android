package com.qeevee.gq.rules.act;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;

import com.qeevee.gq.Variables;
import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.rules.cond.ConditionFactory;

public class Loop extends Action {

	private Condition condition;
	private List<Action> actions;

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= elements.containsKey("condition")
				&& elements.containsKey("then");
		return initOK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		actions = new ArrayList<Action>();
		Variables.setValue("break_while", Boolean.valueOf("false"));

		Element xmlCondition = (Element) elements.get("condition")
				.selectNodes("*").get(0);
		condition = ConditionFactory.create(xmlCondition);

		Element xmlThen = elements.get("then");
		List<Element> xmlActionNodes = xmlThen.selectNodes("action");
		for (Element xmlAction : xmlActionNodes) {
			actions.add(ActionFactory.create(xmlAction));
		}
		int len = actions.size();

		while (condition.isFulfilled()) {
			for (int i = 0; i < len; i++) {
				actions.get(i).executeSavely();
				if ((Boolean) Variables.getValue(Variables.BREAK_WHILE))
					break;
			}
			if ((Boolean) Variables.getValue(Variables.BREAK_WHILE)) {
				Variables.setValue(Variables.BREAK_WHILE,
						Boolean.valueOf("false"));
				break;
			}
		}
	}
}
