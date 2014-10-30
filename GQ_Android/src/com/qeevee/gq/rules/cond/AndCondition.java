package com.qeevee.gq.rules.cond;

import java.util.Iterator;

public class AndCondition extends CompositeCondition {

	@Override
	public boolean isFulfilled() {
		boolean fulfilled = true; // empty and condition is true

		for (Condition containedCondition : containedConditions) {
			fulfilled &= containedCondition.isFulfilled();
			if (fulfilled == false)
				return false;
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
				buf.append(" AND ");
			else
				buf.append(")");
		}
		return buf.toString();
	}
}
