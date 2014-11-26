package com.qeevee.gq.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.qeevee.gq.base.Variables;

public class ScreenArea {

	int xMin, xMax, yMin, yMax;
	private static Map<String, ScreenArea> screenAreas = new HashMap<String, ScreenArea>();

	/**
	 * @param definitionString
	 *            i.e. ScreenArea(xMin, xMax, yMin, yMax)
	 */
	public ScreenArea(String definitionString) {
		String argumentString = definitionString.substring(
				"ScreenArea(".length(), definitionString.length() - 1);
		StringTokenizer tokenizer = new StringTokenizer(argumentString, ",");
		xMin = Integer.parseInt(tokenizer.nextToken());
		xMax = Integer.parseInt(tokenizer.nextToken());
		yMin = Integer.parseInt(tokenizer.nextToken());
		yMax = Integer.parseInt(tokenizer.nextToken());
	}

	private boolean inArea(int posX, int posY) {
		return (xMin <= posX && posX <= xMax && yMin <= posY && posY <= yMax);
	}

	/**
	 * Used within SetVariable action to define a new screen area.
	 * 
	 * @param varName
	 * @param sca
	 */
	public static void setValue(String varName, ScreenArea sca) {
		screenAreas.put(varName.trim(), sca);
	}

	public static void clear() {
		screenAreas.clear();
	}

	public static void updateScreenAreaTappedVars() {
		int xTapped = (int) Math.round((Double) Variables
				.getValue(Variables.LAST_TAP_X));
		int yTapped = (int) Math.round((Double) Variables
				.getValue(Variables.LAST_TAP_Y));

		for (Entry<String, ScreenArea> sca : screenAreas.entrySet()) {
			Variables.setValue(sca.getKey() + ".tapped", (Boolean) sca
					.getValue().inArea(xTapped, yTapped));
		}
	}

}
