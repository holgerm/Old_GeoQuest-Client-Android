package com.qeevee.gq.menu;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import android.util.Log;

import com.qeevee.gq.loc.Hotspot;

public class GQMenuManager {

	private static final String TAG = GQMenuManager.class.getCanonicalName();

	static void createItemsFromXML(Element documentRoot)
			throws DocumentException {
		@SuppressWarnings("unchecked")
		List<Element> list = documentRoot.selectNodes("//menuItem");

		for (Iterator<Element> i = list.iterator(); i.hasNext();) {
			Element menuItem = i.next();
			try {
				new GQMenuItem(menuItem);
			} catch (Hotspot.IllegalHotspotNodeException exception) {
				Log.e(TAG, exception.toString());
			}
		}
	}

	private static GQMenuManager instance;

	public static GQMenuManager getInstance() {
		if (instance == null)
			instance = new GQMenuManager();
		return instance;
	}

	private Hashtable<CharSequence, GQMenuItem> allMenuItems = new Hashtable<CharSequence, GQMenuItem>();

	public void add(CharSequence id, GQMenuItem gqMenuItem) {
		allMenuItems.put(id, gqMenuItem);
		updateMenuView();
	}

	public void clear() {
		allMenuItems.clear();
		updateMenuView();
	}

	public void remove(CharSequence id) {
		allMenuItems.remove(id);
		updateMenuView();
	}

	/**
	 * Updates the visible menu items in the app which holds for all in quest
	 * activities, i.e. all types that implement
	 * {@link edu.bonn.mobilegaming.geoquest.MissionOrToolActivity}.
	 */
	private void updateMenuView() {
		// TODO Auto-generated method stub

	}

}
