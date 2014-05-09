package com.qeevee.gq.rules.cond;

public class EqCondition extends ComparisonCondition {

	protected boolean compare(Double operandA, Double operandB) {
		return (operandA.compareTo(operandB) == 0);
	}

	protected boolean compare(String operandA, String operandB) {
		return operandA.equals(operandB);
	}

	@Override
	public String getComparisonName() {
		return "EQ";
	}
}
