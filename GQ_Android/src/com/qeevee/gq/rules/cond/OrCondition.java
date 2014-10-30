package com.qeevee.gq.rules.cond;

import java.util.Iterator;

public class OrCondition extends CompositeCondition {

	@Override
	public boolean isFulfilled() {
		boolean fulfilled = false; // empty or condition is false

		for (Condition containedCondition : containedConditions) {
			fulfilled |= containedCondition.isFulfilled();
			if (fulfilled == true)
				return true; // Lazy or evaluation.
		}

		return fulfilled;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		for (Iterator<Condition> conditionIter = containedConditions.iterator(); conditionIter
				.hasNext();) {
			Condition curCond = (Condition) conditionIter.next();
			buf.append(curCond.toString());
			if (conditionIter.hasNext())
				buf.append(" OR ");
			else
				buf.append(")");
		}
		return buf.toString();
	}
}
