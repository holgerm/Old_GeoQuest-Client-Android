package com.qeevee.gq.rules.cond;

import com.qeevee.gq.rules.Rule;

public class NoRuleFiredYetCondition extends Condition { 

	@Override
	public boolean isFulfilled() {
		return !Rule.getRuleFiredTracker();
	}

}
