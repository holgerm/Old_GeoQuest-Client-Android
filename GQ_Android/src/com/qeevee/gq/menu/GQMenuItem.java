package com.qeevee.gq.menu;

import org.dom4j.Element;

import com.qeevee.gq.xml.XMLUtilities;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class GQMenuItem {

	private CharSequence id;
	private CharSequence title;
	boolean showText;
	int priority;
	CharSequence iconURL;
	ShowState showState;
	boolean activity;

	public enum ShowState {
		ALWAYS, IF_ROOM, NEVER;

		public static ShowState valueOf(CharSequence input) {
			String trimmed = XMLUtilities.textify(input).toString();
			if ("always".equalsIgnoreCase(trimmed))
				return ALWAYS;
			if ("if_room".equalsIgnoreCase(trimmed))
				return IF_ROOM;
			if ("never".equalsIgnoreCase(trimmed))
				return NEVER;
			else
				return ShowState.valueOf(GeoQuestApp.getInstance().getText(
						R.string.menuItem_showState_default));
		}
	}

	public GQMenuItem(Element menuItemXML) {
		// get attributes:
		id = XMLUtilities.getStringAttribute("id",
				XMLUtilities.NECESSARY_ATTRIBUTE, menuItemXML);
		title = XMLUtilities.getStringAttribute("title",
				XMLUtilities.NECESSARY_ATTRIBUTE, menuItemXML);
		showText = XMLUtilities.getBooleanAttribute("showText",
				R.bool.menuItem_default_showText, menuItemXML);
		priority = XMLUtilities.getIntegerAttribute("priority",
				R.integer.menuItem_defaultPriority, menuItemXML);
		iconURL = XMLUtilities.getStringAttribute("icon",
				XMLUtilities.NECESSARY_ATTRIBUTE, menuItemXML);
		showState = ShowState.valueOf(XMLUtilities.getStringAttribute(
				"showstate", R.string.menuItem_showState_default, menuItemXML));
		activity = XMLUtilities.getBooleanAttribute("initialActivity",
				R.bool.menuItem_default_initialActivity, menuItemXML);
		
		// store in manager:
		GQMenuManager.getInstance().add(id, this);
	}

}
