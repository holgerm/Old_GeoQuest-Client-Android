package com.qeevee.gq.xml;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import android.util.Log;
import android.webkit.WebView;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.ui.WebViewUtil;
import com.qeevee.util.StringTools;

public class XMLUtilities {

	@SuppressWarnings("rawtypes")
	public static String getXMLContent(Element element) {
		StringBuilder builder = new StringBuilder();
		List contentParts = element.content();
		for (Iterator iterator = contentParts.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			builder.append(node.asXML());
		}
		return textify(builder.toString());
	}

	public static String textify(String rawText) {
		String cleanText;
		cleanText = rawText.replaceAll("\\s+", " ").trim();
		cleanText = StringTools.replaceVariables(cleanText);
		if (cleanText.startsWith(" ")) {
			cleanText = cleanText.substring(1);
		}
		return cleanText;
	}

	public static CharSequence textify(CharSequence rawText) {
		return (CharSequence) textify(rawText.toString());
	}

	public static final int NECESSARY_ATTRIBUTE = 0;
	public static final int OPTIONAL_ATTRIBUTE = 1;
	private static final String TAG = XMLUtilities.class.getSimpleName();

	/**
	 * 
	 * @param attributeName
	 * @param defaultAsResourceID
	 *            either NECESSARY_ATTRIBUTE, OPTIONAL_ATTRIBUTE or a valid
	 *            resource ID which points to the default value for this
	 *            attribute as a string resource.
	 * @param xmlElement
	 *            the XML element which defines the attribute, e.g. representing
	 *            a mission or an item in game.xml
	 * 
	 * @return the corresponding attribute value as specified in the game.xml or
	 *         null if the attribute is optional and not specified
	 * @throws IllegalArgumentException
	 *             if the attribute is necessary but not given in the game.xml
	 */
	public static CharSequence getStringAttribute(String attributeName,
			int defaultAsResourceID, Element xmlElement) {
		if (xmlElement == null
				|| xmlElement.attributeValue(attributeName) == null)
			if (defaultAsResourceID == NECESSARY_ATTRIBUTE) {
				// attribute needed but not found => error in game.xml:
				IllegalArgumentException e = new IllegalArgumentException(
						"Necessary attribute \"" + attributeName
								+ "\" missing. Rework game specification.");
				Log.e(TAG, e.toString());
				throw e;
			} else if (defaultAsResourceID == OPTIONAL_ATTRIBUTE) {
				// optional attribute not set in game.xml => return null:
				return null;
			} else
				// attribute not set in game.xml but given as parameter => use
				// referenced resource as default and return its value:
				return textify(GeoQuestApp.getInstance().getText(
						defaultAsResourceID));
		else
			return (CharSequence) textify(xmlElement
					.attributeValue(attributeName));
	}

	/**
	 * 
	 * @param attributeName
	 * @param defaultAsResourceID
	 *            either NECESSARY_ATTRIBUTE, OPTIONAL_ATTRIBUTE or a valid
	 *            resource ID which points to the default value for this
	 *            attribute as a string resource.
	 * @param xmlElement
	 *            the XML element which defines the attribute, e.g. representing
	 *            a mission or an item in game.xml
	 * 
	 * @return the corresponding attribute value as specified in the game.xml or
	 *         null if the attribute is optional and not specified
	 * @throws IllegalArgumentException
	 *             if the attribute is necessary but not given in the game.xml
	 */
	public static Integer getIntegerAttribute(String attributeName,
			int defaultAsResourceID, Element xmlElement) {
		if (xmlElement == null
				|| xmlElement.attributeValue(attributeName) == null)
			if (defaultAsResourceID == NECESSARY_ATTRIBUTE) {
				// attribute needed but not found => error in game.xml:
				IllegalArgumentException e = new IllegalArgumentException(
						"Necessary attribute \"" + attributeName
								+ "\" missing. Rework game specification.");
				Log.e(TAG, e.toString());
				throw e;
			} else if (defaultAsResourceID == OPTIONAL_ATTRIBUTE) {
				// optional attribute not set in game.xml => return null:
				return null;
			} else
				// attribute not set in game.xml but given as parameter => use
				// referenced resource as default and return its value:
				return GeoQuestApp.getInstance().getResources()
						.getInteger(defaultAsResourceID);
		else
			return Integer.valueOf(xmlElement.attributeValue(attributeName));
	}

	/**
	 * 
	 * @param attributeName
	 * @param defaultAsResourceID
	 *            either NECESSARY_ATTRIBUTE, OPTIONAL_ATTRIBUTE or a valid
	 *            resource ID which points to the default value for this
	 *            attribute as a string resource.
	 * @param xmlElement
	 *            the XML element which defines the attribute, e.g. representing
	 *            a mission or an item in game.xml
	 * 
	 * @return the corresponding attribute value as specified in the game.xml or
	 *         null if the attribute is optional and not specified
	 * @throws IllegalArgumentException
	 *             if the attribute is necessary but not given in the game.xml
	 */
	public static Boolean getBooleanAttribute(String attributeName,
			int defaultAsResourceID, Element xmlElement) {
		if (xmlElement == null
				|| xmlElement.attributeValue(attributeName) == null)
			if (defaultAsResourceID == NECESSARY_ATTRIBUTE) {
				// attribute needed but not found => error in game.xml:
				IllegalArgumentException e = new IllegalArgumentException(
						"Necessary attribute \"" + attributeName
								+ "\" missing. Rework game specification.");
				Log.e(TAG, e.toString());
				throw e;
			} else if (defaultAsResourceID == OPTIONAL_ATTRIBUTE) {
				// optional attribute not set in game.xml => return null:
				return null;
			} else
				// attribute not set in game.xml but given as parameter => use
				// referenced resource as default and return its value:
				return GeoQuestApp.getInstance().getResources()
						.getBoolean(defaultAsResourceID);
		else
			return stringToBool(xmlElement.attributeValue(attributeName));
	}

	/**
	 * TODO use it anywhere in our code where we interpret boolean attributes.
	 * 
	 * @param string
	 * @return true if the given string either is "true" or zero.
	 */
	public static boolean stringToBool(String string) {
		if (string == null)
			return false;
		String trimmed = textify(string);
		return ("true".equalsIgnoreCase(trimmed) || "1".equals(trimmed));
	}

	/**
	 * Loads the content (including html tags) of the given xml element into the
	 * given webview.
	 * 
	 * @param view
	 * @param element
	 */
	public static void loadElementContentForWebView(WebView view,
			Element element) {
		WebViewUtil.showTextInWebView(view, getXMLContent(element));
	}

}
