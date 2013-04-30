package com.qeevee.gq.rules.act;

import org.dom4j.Element;

import com.qeevee.gq.rules.expr.Expressions;

import edu.bonn.mobilegaming.geoquest.Variables;

public class SetVariable extends Action {

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= params.containsKey("var");
		initOK &= elements.containsKey("value");
		return initOK;
	}

	@Override
	public void execute() {
		Variables.setValue(params.get("var"),
				Expressions.evaluate((Element) elements.get("value")
						.selectSingleNode("*")));
	}

}
