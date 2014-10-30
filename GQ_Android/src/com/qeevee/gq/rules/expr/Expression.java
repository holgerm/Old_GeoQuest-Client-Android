package com.qeevee.gq.rules.expr;

import org.dom4j.Element;

public abstract class Expression {

	public abstract Object evaluate(Element xmlExpression);
}
