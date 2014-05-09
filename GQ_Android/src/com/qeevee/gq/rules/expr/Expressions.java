package com.qeevee.gq.rules.expr;

import java.util.Random;

import org.dom4j.Element;

import android.util.Log;

import com.qeevee.gq.rules.cond.Condition;
import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;

public class Expressions {
	public static final String PACKAGE_NAME = getPackageName();
	private static final String TAG = "Expressions";

	private static String getPackageName() {
		String conditionClassName = Condition.class.getName();
		int indexOfLastDot = conditionClassName.lastIndexOf('.');
		return conditionClassName.substring(0, indexOfLastDot + 1);
	}

	public static Object evaluate(Element xmlExpression) {
		final String exprName = xmlExpression.getName();

		if (exprName.equals("num"))
			return evaluateNum(xmlExpression);

		if (exprName.equals("random"))
			return evaluateRandom(xmlExpression);

		if (exprName.equals("var"))
			return evaluateVar(xmlExpression);

		if (exprName.equals("bool"))
			return evaluateBool(xmlExpression);

		if (exprName.equals("string"))
			return evaluateString(xmlExpression);

		// ELSE unknown type of expression:
		Log.d(TAG, " unknown expression: " + xmlExpression.asXML());
		return null;
	}

	private static Double evaluateRandom(Element xmlRandomExpression) {
		int min, max;
		min = (Integer) (xmlRandomExpression.attributeValue("min") == null ? GeoQuestApp
				.getContext().getResources()
				.getInteger(R.integer.randomExpression_defaultMin)
				: Integer.parseInt(xmlRandomExpression.attributeValue("min")));
		max = (Integer) (xmlRandomExpression.attributeValue("max") == null ? GeoQuestApp
				.getContext().getResources()
				.getInteger(R.integer.randomExpression_defaultMax)
				: Integer.parseInt(xmlRandomExpression.attributeValue("max")));
		Random generator = new Random();
		return Double
				.valueOf((double) (generator.nextInt(max - min + 1) + min));
	}

	private static String evaluateString(Element xmlStringExpression) {
		return xmlStringExpression.getText().trim();
	}

	private static Object evaluateBool(Element xmlBoolExpression) {
		Boolean boolVal;
		try {
			boolVal = Boolean.parseBoolean(xmlBoolExpression.getText());
		} catch (NumberFormatException e) {
			Log.d(TAG,
					" invalid boolean expression: " + xmlBoolExpression.asXML());
			boolVal = null;
		}
		return boolVal;
	}

	private static Double evaluateNum(Element xmlNumExpression) {
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

	private static Object evaluateVar(Element xmlVarExpression) {
		String varName = xmlVarExpression.getText().trim();
		if (Variables.isDefined(varName))
			return Variables.getValue(varName);
		else {
			Log.d(TAG, " variable \"" + varName + "\" undefined.");
			return Double.valueOf(0.0d); // but we return 0.0d as default value
											// of
											// undefined variables.
		}
	}

	public static String toString(Element xmlExpression) {
		return xmlExpression.getName() + "("
				+ XMLUtilities.getXMLContent(xmlExpression) + ")";
	}

}
