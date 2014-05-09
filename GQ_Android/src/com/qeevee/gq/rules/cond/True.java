package com.qeevee.gq.rules.cond;

public class True extends Condition {

	public boolean isFulfilled() {
		return true;
	}

	public String toString() {
		return "TRUE";
	}

}
