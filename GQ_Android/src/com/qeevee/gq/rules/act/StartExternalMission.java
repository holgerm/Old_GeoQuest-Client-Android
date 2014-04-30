package com.qeevee.gq.rules.act;

import java.util.Map;

import org.dom4j.Element;

import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.Mission;

public class StartExternalMission extends StartMission {

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= super.checkInitialization();

		initOK &= Mission.get(params.get("id")) != null;
		return initOK;
	}

	@Override
	public void execute() {
		if (elements.containsKey("parameters")) {
			Element parametersElement = elements.get("parameters");
			Map<String, String> inputParameters = XMLUtilities
					.extractParameters(parametersElement);
			Mission.get(params.get("id")).startMission(inputParameters);
		} else
			Mission.get(params.get("id")).startMission();
	}

}
