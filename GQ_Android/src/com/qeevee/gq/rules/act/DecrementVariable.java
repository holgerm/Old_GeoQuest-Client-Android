package com.qeevee.gq.rules.act;

import com.qeevee.gq.base.Variables;

import android.util.Log;

public class DecrementVariable extends Action {

	@Override
	protected boolean checkInitialization() {
		boolean initOK = params.containsKey("var");
		initOK &= (Variables.getValue(params.get("var")) instanceof Double);
		return initOK;
	}

	@Override
	public void execute() {
		String varName = params.get("var");
		Double value = (Double) Variables.getValue(varName);
		if (value instanceof Double)
			Variables.setValue(varName, (Double) value - 1.0d);
		else
			Log.w(this.getClass().getName(),
					"Tried to decrement non number variable. Ignored.");
	}

}
