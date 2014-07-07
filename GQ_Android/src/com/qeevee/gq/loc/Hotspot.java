package com.qeevee.gq.loc;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.qeevee.gq.loc.map.GoogleHotspotOverlay;
import com.qeevee.gq.loc.map.OSMOverlayItem;
import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;
import com.qeevee.gq.Variables;

/**
 * Hotspots are interaction points on a mapmission map. They have a image and a
 * referenced mission that is started when the player taps on the hotspot.
 * 
 * @author Krischan Udelhoven
 * @author Folker Hoffmann
 * @author Holger Muegge
 * 
 */
public class Hotspot {

	private static HotspotManager hm = HotspotManager.getInstance();

	public Hotspot(Element hotspotXML) {
		readXML(hotspotXML);
		hm.add(this);
	}

	/** location of the hotspot */
	public GeoPoint geoPoint;

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public org.osmdroid.util.GeoPoint getOSMGeoPoint() {
		return new org.osmdroid.util.GeoPoint(geoPoint.getLatitudeE6(),
				geoPoint.getLongitudeE6());
	}

	/** icon of the hotspot on the map */
	public Bitmap bitmap;

	public Drawable getDrawable() {
		return new BitmapDrawable(GeoQuestApp.getInstance().getResources(),
				bitmap);
	}

	/** true if the user is in range of the hotspot */
	public boolean isInRange;

	/**
	 * Visibility means for example that the hotspot is drawn on the map.
	 * Default is <code>true</code>.
	 */
	private boolean visible = true;
	private Overlay googleOverlay;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean newVisibility) {
		if (this.visible && !newVisibility) {
			hm.hideHotspot(this);
		}
		if (!this.visible && newVisibility) {
			hm.unveilHotspot(this);
		}
		this.visible = newVisibility;
	}

	public Overlay getGoogleOverlay() {
		return googleOverlay;
	}

	/** radius of the interaction circle */
	public int radius;
	/** needed for painting the interaction circle */
	public Paint paint;
	/** id of the hotspot */
	public String id;

	static String TAG = "Hotspot";

	enum Triggers {
		ON_ENTER, ON_LEAVE
	};

	public void runOnTapEvent() {
		if (GeoQuestApp.getInstance().isInDebugmode()) {
			// in debug mode we emulate enter event when tapping.
			runOnEnterEvent();
		}
		Rule.resetRuleFiredTracker();
		for (Rule rule : onTapRules) {
			rule.apply();
		}
	}

	public void runOnEnterEvent() {
		Rule.resetRuleFiredTracker();
		for (Rule rule : onEnterRules) {
			rule.apply();
		}
	}

	public void runOnLeaveEvent() {
		Rule.resetRuleFiredTracker();
		for (Rule rule : onLeaveRules) {
			rule.apply();
		}
	}

	public String name;
	private String description;

	private String markerPath;

	public String getMarkerPath() {
		return markerPath;
	}

	public static class IllegalHotspotNodeException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public IllegalHotspotNodeException(String error) {
			super(error);
		}
	}

	/**
	 * Inits some basic parameters
	 * 
	 * @param location
	 * @param drawableID
	 * @param parentMission
	 * @param radius
	 * @param startMissionID
	 * @param invisible
	 * @param id
	 * @throws IllegalHotspotNodeException
	 */
	private void readXML(Element _hotspotNode)
			throws IllegalHotspotNodeException {
		// ID of the hotspot:
		this.id = _hotspotNode.attributeValue("id").trim();

		double latitude, longitude;
		// first look for 'latlong' abbreviating attribute:
		String latLongString = _hotspotNode.attributeValue("latlong");
		if (latLongString != null) {
			latitude = Double.valueOf(latLongString.split(",")[0]) * 1E6;
			longitude = Double.valueOf(latLongString.split(",")[1]) * 1E6;
		} else {
			// latitude & longitude attribute
			Attribute latitudeA = (Attribute) _hotspotNode
					.selectSingleNode("@latitude");
			Attribute longitudeA = (Attribute) _hotspotNode
					.selectSingleNode("@longitude");

			if ((latitudeA == null) || (longitudeA == null))
				throw new IllegalHotspotNodeException(
						"Latitude or Longitude is not set.\n" + _hotspotNode);
			latitude = Double.valueOf(latitudeA.getText()) * 1E6;
			longitude = Double.valueOf(longitudeA.getText()) * 1E6;

		}
		geoPoint = new GeoPoint((int) (latitude), (int) (longitude));
		Variables.setValue(Variables.HOTSPOT_PREFIX + id
				+ Variables.LOCATION_SUFFIX, geoPoint);

		// image
		markerPath = _hotspotNode.attributeValue("img");
		if (markerPath != null) {
			setBitmap(BitmapUtil.loadBitmap(markerPath, 0, 0, false));
		} else {
			setBitmap(((BitmapDrawable) GeoQuestApp.getInstance()
					.getResources()
					.getDrawable(R.drawable.default_hotspot_icon)).getBitmap());
		}

		// Default for initialVisibility attribute is "true"
		if (_hotspotNode.attributeValue("initialVisibility") != null
				&& _hotspotNode.attributeValue("initialVisibility").equals(
						"false")) {
			this.setVisible(false);
		}

		setActive("true".equals(XMLUtilities.getStringAttribute(
				"initialActivity", R.string.hotspot_default_activity,
				_hotspotNode)));

		// Retrieve name attribute:
		if (_hotspotNode.attributeValue("name") != null)
			setName(_hotspotNode.attributeValue("name"));
		else
			// if no name is given, we use the id instead:
			setName(this.id);

		// Retrieve description attribute:
		if (_hotspotNode.attributeValue("description") != null)
			setDescription(_hotspotNode.attributeValue("description"));
		else
			setDescription(GeoQuestApp.getContext()
					.getText(R.string.missing_hotspot_description).toString());

		// radius
		this.radius = Integer.valueOf(((Attribute) _hotspotNode
				.selectSingleNode("@radius")).getText());

		// read events from xml

		createRules(_hotspotNode);

		isInRange = false;
		// TODO: add features like name, description, icon to use in Augmented
		// Reality Browser

		// setup the paint tool for drawing the circle
		paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setARGB(80, 0, 0, 255);
		paint.setStyle(Paint.Style.FILL);

		googleOverlay = new GoogleHotspotOverlay(this);
		setOverlayItem(new OSMOverlayItem(this));
	}

	private List<Rule> onEnterRules = new ArrayList<Rule>();
	private List<Rule> onLeaveRules = new ArrayList<Rule>();
	private List<Rule> onTapRules = new ArrayList<Rule>();
	public float halfBitmapHeight;
	public float halfBitmapWidth;
	private boolean active = true;

	private OverlayItem overlayItem;

	public boolean isActive() {
		return active;
	}

	private void createRules(Element hotspotNode) {
		addRulesToList(onEnterRules, "onEnter/rule", hotspotNode);
		addRulesToList(onLeaveRules, "onLeave/rule", hotspotNode);
		addRulesToList(onTapRules, "onTap/rule", hotspotNode);
	}

	@SuppressWarnings("unchecked")
	private void addRulesToList(List<Rule> ruleList, String xpath,
			Element hotspotNode) {
		List<Element> xmlRuleNodes;
		xmlRuleNodes = hotspotNode.selectNodes(xpath);
		for (Element xmlRule : xmlRuleNodes) {
			ruleList.add(Rule.createFromXMLElement(xmlRule));
		}
	}

	public static void clean() {
		HotspotManager.getInstance().clear();
	}

	/**
	 * get id
	 * 
	 * @return hotspot id or null if not set
	 */
	public String getId() {
		return id;
	}

	/**
	 * tests if the given location is in range of the hotspots location
	 * 
	 * @param loc
	 * @return true if in range
	 */
	public boolean inRange(Location loc) {
		Location hotspotLocation = new Location(
				GeoQuestApp.GQ_MANUAL_LOCATION_PROVIDER);
		hotspotLocation.setLatitude(geoPoint.getLatitudeE6() / 1E6);
		hotspotLocation.setLongitude(geoPoint.getLongitudeE6() / 1E6);
		boolean wasInRangeBefore = isInRange;

		if (loc.distanceTo(hotspotLocation) <= radius) {
			isInRange = true;
			paint.setARGB(80, 255, 0, 0);
			// rest of the game
			if (!wasInRangeBefore) {
				runOnEnterEvent();
			}
			return true;
		} else {
			paint.setARGB(80, 0, 0, 255);
			isInRange = false;
			if (wasInRangeBefore) {
				runOnLeaveEvent();
			}
			return false;
		}
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		this.halfBitmapHeight = bitmap.getHeight() / 2.0f;
		this.halfBitmapWidth = bitmap.getWidth() / 2.0f;
	}

	public String getName() {
		return this.name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public void setActive(boolean newActivityValue) {
		this.active = newActivityValue;
	}

	public void setOverlayItem(OverlayItem overlayItem) {
		this.overlayItem = overlayItem;
	}

	public OverlayItem getOverlayItem() {
		return overlayItem;
	}

}
