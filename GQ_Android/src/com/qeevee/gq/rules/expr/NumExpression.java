package com.qeevee.gq.rules.expr;

import org.dom4j.Element;

import android.util.Log;

public class NumExpression extends Expression {

	private static final String TAG = NumExpression.class.getCanonicalName();

	@Override
	public Object evaluate(Element xmlNumExpression) {
		Double value;
		try {
			value = Double.parseDouble(xmlNumExpression.getText());
		} catch (NumberFormatException e) {
			Log.d(TAG,
					" invalid number expression: " + xmlNumExpression.asXML());
			value = null;
		}
		return value;
	}

}
