package edu.bonn.mobilegaming.geoquest;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.graphics.Bitmap;
import android.location.Location;

import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.xml.GQML_DEFAULTS;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.HotspotOld.IllegalHotspotNodeException;

public class Hotspot {

	public String id;
	public Location location;
	private int radius;
	private boolean visibility;
	private boolean activation;
	private String iconFilePath;
	private ArrayList<Rule> onEnterRules;
	private ArrayList<Rule> onLeaveRules;

	// TODO: Bitmaps in BitmapPool speichern und wiederverwenden wenn gleiche
	// Quelle.

	Hotspot(Element hotspotElement) {
		id = hotspotElement.attributeValue("id");
		location = readPoint(hotspotElement);
		radius = readRadius(hotspotElement);
		visibility = readInitialVisibility(hotspotElement);
		activation = readInitialActivation(hotspotElement);
		iconFilePath = hotspotElement.attributeValue("img");
		// TODO change to "icon" cf. Ticket http://redmine.qeevee.org/issues/406
		onEnterRules = readRules(hotspotElement, "onEnter");
		onLeaveRules = readRules(hotspotElement, "onLeave");
	}

	private ArrayList<Rule> readRules(Element hotspotElement, String eventTag) {
		ArrayList<Rule> rulesList = new ArrayList<Rule>();
		@SuppressWarnings("unchecked")
		List<Element> eventElements = (List<Element>) hotspotElement
				.selectNodes(eventTag);
		if (eventElements == null || eventElements.size() == 0)
			return rulesList;
		Element eventElement = eventElements.get(0);
		@SuppressWarnings("unchecked")
		List<Element> ruleElements = (List<Element>) eventElement
				.selectNodes("rule");
		for (Element ruleElement : ruleElements) {
			rulesList.add(Rule.createFromXMLElement(ruleElement));
		}
		return rulesList;
	}

	private boolean readInitialActivation(Element hotspotElement) {
		String activationString = hotspotElement
				.attributeValue("initialActivation");
		if (activationString != null) {
			return Boolean.parseBoolean(activationString);
		} else {
			// use default instead:
			return GQML_DEFAULTS.HOTSPOT_INITIAL_VISIBILITY;
		}
	}

	private boolean readInitialVisibility(Element hotspotElement) {
		String visibilityString = hotspotElement
				.attributeValue("initialVisibility");
		if (visibilityString != null) {
			return Boolean.parseBoolean(visibilityString);
		} else {
			// use default instead:
			return GQML_DEFAULTS.HOTSPOT_INITIAL_VISIBILITY;
		}
	}

	private int readRadius(Element hotspotElement) {
		String radiusString = hotspotElement.attributeValue("radius");
		if (radiusString != null) {
			return Integer.parseInt(radiusString);
		} else {
			// use default instead:
			return GQML_DEFAULTS.HOTSPOT_RADIUS;
		}
	}

	private Location readPoint(Element hotspotElement) {
		// first look for 'latlong' abbreviating attribute:
		String latLongString = hotspotElement.attributeValue("latlong");
		if (latLongString != null) {
			return readCoordinateStrings(latLongString.split(",")[0],
					latLongString.split(",")[1]);
		} else {
			// use separate attributes latitude, longitude:
			String latString, longString;
			latString = hotspotElement.attributeValue("latitude");
			longString = hotspotElement.attributeValue("longitude");
			return readCoordinateStrings(latString, longString);
		}
	}

	/**
	 * Interprets the two coordinate strings as internal int representation
	 * 
	 * @param latString
	 * @return
	 */
	private Location readCoordinateStrings(String latString, String longString) {
		if ((latString == null) || (longString == null))
			throw new IllegalHotspotNodeException(
					"Latitude or Longitude is not set for Hotspot " + id);

		Location loc = new Location(GeoQuestApp.GQ_MANUAL_LOCATION_PROVIDER);
		loc.setLatitude(Double.valueOf(latString));
		loc.setLongitude(Double.valueOf(longString));
		return loc;
	}

	/**
	 * @return the radius of this hotspot in meter.
	 */
	public int getRadius() {
		return radius;
	}

	public boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean newVisibility) {
		this.visibility = newVisibility;
	}

	public boolean getActivation() {
		return activation;
	}

	public void setActivation(boolean newActivationState) {
		this.activation = newActivationState;
	}

	public Bitmap getIconBitmap() {
		// TODO retrieve from BitmapPool
		return BitmapUtil.loadBitmap(iconFilePath, false);
	}

}
