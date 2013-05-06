package com.qeevee.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bonn.mobilegaming.geoquest.Variables;

public class StringTools {

	public static String caseUp(String original) {
		return original.substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ original.substring(1);
	}

	public static String trim(String original) {
		return original.replaceAll("\\s+", " ").trim();
	}

	public static String replaceVariables(String inputString) {
		String result = new String(inputString);
		Pattern p = Pattern.compile("@\\$?\\w+@");
		Matcher m = p.matcher(inputString);
		while (m.find()) {
			String varComplete = m.group();
			String varName = varComplete.substring(1, varComplete.length() - 1);
			if (Variables.isDefined(varName)) {
				result = result.replaceAll(varComplete,
						Variables.getValue(varName).toString());
			}
		}

		return result;
	}
}
