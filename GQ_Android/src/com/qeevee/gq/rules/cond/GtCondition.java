package com.qeevee.gq.rules.cond;

public class GtCondition extends ComparisonCondition {

	@Override
	protected boolean compare(Double operandA, Double operandB) {
		return operandA > operandB;
	}

	@Override
	protected boolean compare(String object, String object2) {
		return (object.compareTo(object2) > 0);
	}

	@Override
	public String getComparisonName() {
		return "GT";
	}

	@Override
	protected boolean compare(Boolean object, Boolean object2) {
		return object == true && object2 == false;
	}
}
