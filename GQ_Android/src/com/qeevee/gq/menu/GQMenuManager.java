package com.qeevee.gq.menu;

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

}
