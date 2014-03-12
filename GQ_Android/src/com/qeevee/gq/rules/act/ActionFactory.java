package com.qeevee.gq.rules.act;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.util.Log;

/**
 * This factory class creates Action objects as specified in the game.xml
 * specification. Attributes and contained elements of the according action xml
 * tag are provided as parameters. These parameters are stored temporarily as
 * key value pairs in the HashMap that is given to the creating constructor of
 * the according action class.
 * 
 */
public class ActionFactory {

	private static String getActionPackageBaseName() {
		String commandClassName = Action.class.getName();
		int indexOfLastDot = commandClassName.lastIndexOf('.');
		return commandClassName.substring(0, indexOfLastDot + 1);
	}

	private static final String PACKAGE_BASE_FOR_ACTIONS = getActionPackageBaseName();
	private static final String TAG = "ActionFactory";

	public static Action create(Element xmlActionNode) {
		String commandType = xmlActionNode.attributeValue("type");
		if (commandType == null || commandType.equals("")) {
			Log.d(TAG, " invalid type specified or type omitted in "
					+ xmlActionNode);
			return null;
		}

		Object actionObject;
		try {
			@SuppressWarnings("rawtypes")
			Class actionClass = Class.forName(PACKAGE_BASE_FOR_ACTIONS
					+ commandType);
			actionObject = actionClass.newInstance();
		} catch (ClassNotFoundException e) {
			Log.d(TAG, " invalid type specified; action not found: "
					+ commandType);
			return null;
		} catch (IllegalAccessException e) {
			Log.d(TAG, e.toString());
			return null;
		} catch (InstantiationException e) {
			Log.d(TAG, e.toString());
			return null;
		}

		/*
		 * Prepare attributes as parameters in a HashMap:
		 */
		Map<String, String> params = new HashMap<String, String>();
		for (Object attribute : xmlActionNode.attributes()) {
			if (attribute instanceof Attribute) {
				params.put(((Attribute) attribute).getName(),
						((Attribute) attribute).getData().toString());
			}
		}

		/*
		 * Prepare contained xml elements in HashMap elements. Each child of the
		 * action node is represented as a map entry by its tag name as key and
		 * its tag body as value.
		 */
		Map<String, Element> elements = new HashMap<String, Element>();
		for (Object containedParameterElementObject : xmlActionNode
				.selectNodes("*")) {
			if (containedParameterElementObject instanceof Element) {
				try {
					elements.put(((Element) containedParameterElementObject)
							.getName(),
							(Element) containedParameterElementObject);
				} catch (ClassCastException e) {
					Log.d(TAG,
							" action contains invalid child element: "
									+ ((Element) containedParameterElementObject)
											.asXML());
					return null;
				}
			}
		}

		if (actionObject instanceof Action) {
			boolean initializationSuccessful = ((Action) actionObject).init(
					params, elements);
			if (!initializationSuccessful) {
				Log.d(TAG, " initialization failed: " + params.toString());
				return null;
			}
		} else {
			Log.d(TAG, " unknown Action type: " + commandType);
			return null;
		}

		return (Action) actionObject;

	}

}
