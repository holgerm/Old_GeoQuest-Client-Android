package com.qeevee.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qeevee.gq.Variables;

import android.util.Log;

public class StringTools {

	private static final Pattern varReplacePattern = Pattern
			.compile("@(\\$_)?[A-Za-z0-9_.]+@");

	public static String caseUp(String original) {
		return original.substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ original.substring(1);
	}

	public static String trim(String original) {
		return original.replaceAll("\\s+", " ").trim();
	}

	public static String replaceVariables(String inputString) {
		String result = new String(inputString);
		Matcher m = varReplacePattern.matcher(inputString);
		while (m.find()) {
			String varComplete = m.group();
			String varName = varComplete.substring(1, varComplete.length() - 1);
			if (Variables.isDefined(varName)) {
				String replacement;
				Object value = Variables.getValue(varName);
				// for doubles which have only zeroes after semicolon, we return
				// int-like strings (without trailing zeroes).
				if (value instanceof Double) {
					replacement = value.toString();
					if (replacement.lastIndexOf('.') >= 0) {
						int nachKomma = Integer.parseInt(replacement.substring(
								replacement.lastIndexOf('.') + 1,
								replacement.length()));
						if (nachKomma == 0) {
							value = replacement.substring(0,
									replacement.lastIndexOf('.'));
						}
					}
				}
				result = result.replace(varComplete, value.toString());
			} else {
				Log.w(StringTools.class.getCanonicalName(),
						"Undefined variable found: " + varName);
			}
		}

		return result;
	}

	/**
	 * Interprets an attribute value from game.xml as boolean value.
	 * 
	 * @param text
	 *            the attribute content to interpret as boolean. Null is ok and
	 *            leads to false.
	 * @return true, iff the attribute either contains "true" or "1". in any
	 *         other case it returns false.
	 */
	public static boolean asBoolean(String text) {
		if (text == null)
			return false;
		if (text.equals("true"))
			return true;
		if (text.equals("1"))
			return true;
		return false;
	}
}
