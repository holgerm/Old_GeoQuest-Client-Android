package com.qeevee.gq.rules.cond;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.util.Log;

import com.qeevee.gq.rules.expr.Expressions;

/**
 * NumberComparisonConditions rely on expressions that evaluate to a number
 * (i.e. a Double).
 * 
 * TODO: change to a general ComparisonCondition and allow also for Strings.
 * Even better: allow for a set of types for which we support compare(T, T).
 * 
 * @author hm
 * 
 */
public abstract class ComparisonCondition extends Condition {

	// private static final String TAG = "ComparisonCondition";

	protected List<Element> xmlExpressions;

	@SuppressWarnings("unchecked")
	@Override
	protected boolean init(Element xmlCondition) {
		boolean successfullyInitialized = super.init(xmlCondition);

		this.xmlExpressions = xmlCondition.selectNodes("*");

		successfullyInitialized &= xmlExpressions.size() >= 2;
		return successfullyInitialized;
	}

	@Override
	public boolean isFulfilled() {
		/*
		 * Evaluate each expression contained in the according xml condition
		 * element as it has been parsed on initialization.
		 */
		List<Object> values = new ArrayList<Object>();
		for (Element xmlExpression : xmlExpressions) {
			Object valueObject = Expressions.evaluate(xmlExpression);
			// if (valueObject instanceof Number) // TODO skip type check!
			// values.add((Double) valueObject);
			values.add(valueObject);
			// else
			// Log.d(TAG,
			// ": Number expected, but got " + valueObject.getClass()
			// + " instead from " + xmlExpression.asXML());
			/*
			 * Operands that are not Numbers are ignored.
			 */
		}

		if (values.size() < 2)
			return false;

		/*
		 * Compare each value (as evaluated from the expressions in this
		 * condition) with its follower. Stops when a comparison yields false
		 * and returns false then.
		 */
		boolean fulfilled = true;
		for (int i = 0; fulfilled && i <= values.size() - 2; i++) {
			fulfilled &= compare(values.get(i), values.get(i + 1));
			/*
			 * Using doubleValue() here is not reliable when it comes to
			 * calculations. Since we do not intend calculation for now, it is
			 * ok. Be aware of this as a potential source of errors in the
			 * future. (TODO)
			 */
		}

		return fulfilled;

	}

	protected abstract boolean compare(Double object, Double object2);

	protected abstract boolean compare(String object, String object2);

	private final boolean compare(Object opA, Object opB) {
		if (opA instanceof Double && opB instanceof Double)
			return (compare((Double) opA, (Double) opB));
		else if (opA instanceof String && opB instanceof String)
			return (compare((String) opA, (String) opB));
		else
			return false;
	}

}
