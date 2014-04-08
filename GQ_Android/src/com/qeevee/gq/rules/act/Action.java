package com.qeevee.gq.rules.act;

import java.util.Map;

import org.dom4j.Element;

import android.util.Log;

public abstract class Action {

	boolean executable = false;

	Action() {
	}

	/**
	 * The <code>params</code> map contains all given attributes of the action
	 * XML element. Cf. ActionFactory for details.
	 */
	protected Map<String, String> params;
	/**
	 * The <code>elements</code> map contains all direct XML children of the
	 * action XML element. Cf. ActionFactory for details.
	 */
	protected Map<String, Element> elements;

	protected final boolean init(Map<String, String> params,
			Map<String, Element> elements) {
		this.params = params;
		this.elements = elements;
		executable = checkInitialization();
		return executable;
	}

	protected abstract boolean checkInitialization();

	public void execute() {
		if (executable)
			_execute();
		else {
			Log.w(this.getClass().getCanonicalName(),
					"tried to execute incorrectly initialized action");
		}
	}

	protected abstract void _execute();

}
