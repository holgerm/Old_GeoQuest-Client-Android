package com.qeevee.gq.rules.expr;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.dom4j.Element;

import android.annotation.SuppressLint;
import android.util.Log;

import com.qeevee.gq.xml.XMLUtilities;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;
import com.qeevee.gq.Variables;

public class Expressions {
	private static final String TAG = "Expressions";
	private static final int INTERPRETATION_UNDEFINED = 0;
	private static final int INTERPRETATION_NUM = 1;
	private static final int INTERPRETATION_STRING = 2;

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

		if (exprName.equals("sum"))
			return evaluateSum(xmlExpression);

		if (exprName.equals("subtract"))
			return evaluateSubtract(xmlExpression);

		if (exprName.equals("multiply"))
			return evaluateMultiply(xmlExpression);

		// ELSE unknown type of expression:
		Log.d(TAG, " unknown expression: " + xmlExpression.asXML());
		return null;
	}

	@SuppressLint("DefaultLocale")
	private static String d2String(double d) {
		if (d == (int) d)
			return String.format(Locale.getDefault(), "%d", (int) d);
		else
			return String.format(Locale.getDefault(), "%s", d);
	}

	private static Object evaluateSum(Element xmlSumExpression) {
		@SuppressWarnings("unchecked")
		List<Element> xmlSummands = xmlSumExpression.selectNodes("*");
		Double sum = 0.0d;
		StringBuffer text = new StringBuffer();
		Object summand;
		int interpretationAs = INTERPRETATION_UNDEFINED;

		for (Element xmlSummand : xmlSummands) {
			summand = evaluate(xmlSummand);
			if (summand instanceof String) {
				if (interpretationAs == INTERPRETATION_UNDEFINED) {
					interpretationAs = INTERPRETATION_STRING;
				}
				if (interpretationAs == INTERPRETATION_NUM) {
					text.append(d2String(sum));
					interpretationAs = INTERPRETATION_STRING;
				}
				if (interpretationAs == INTERPRETATION_STRING) {
					text.append((String) summand);
				}
			}
			if (summand instanceof Double) {
				if (interpretationAs == INTERPRETATION_UNDEFINED) {
					interpretationAs = INTERPRETATION_NUM;
				}
				if (interpretationAs == INTERPRETATION_NUM) {
					sum += (Double) summand;
				}
				if (interpretationAs == INTERPRETATION_STRING) {
					text.append(d2String((Double) summand));
				}
			}
			if (summand instanceof Boolean) {
				throw new IllegalArgumentException(
						"Boolean not allowed in sum expression.");
			}
		}

		if (interpretationAs == INTERPRETATION_STRING)
			return text.toString();
		else
			return sum;
	}

	private static Object evaluateSubtract(Element xmlSubtractExpression) {
		@SuppressWarnings("unchecked")
		List<Element> xmlSummands = xmlSubtractExpression.selectNodes("*");
		Double subtraction = null;
		Object summand;

		for (Element xmlSummand : xmlSummands) {
			summand = evaluate(xmlSummand);
			if (summand instanceof String) {
				throw new IllegalArgumentException(
						"String not allowed in subtract expression. Occured in "
								+ XMLUtilities
										.getXMLContent(xmlSubtractExpression));
			}
			if (summand instanceof Double) {
				if (subtraction == null)
					subtraction = (Double) summand;
				else
					subtraction -= (Double) summand;
			}
		}

		return subtraction;
	}

	private static Object evaluateMultiply(Element xmlSubtractExpression) {
		@SuppressWarnings("unchecked")
		List<Element> xmlSummands = xmlSubtractExpression.selectNodes("*");
		Double product = 1.0d;
		Object summand;

		for (Element xmlSummand : xmlSummands) {
			summand = evaluate(xmlSummand);
			if (summand instanceof String) {
				throw new IllegalArgumentException(
						"String not allowed in subtract expression. Occured in "
								+ XMLUtilities
										.getXMLContent(xmlSubtractExpression));
			}
			if (summand instanceof Double) {
				if (product == null)
					product = (Double) summand;
				else
					product *= (Double) summand;
			}
		}

		return product;
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
		return xmlStringExpression.getText();
	}

	private static Boolean evaluateBool(Element xmlBoolExpression) {
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
