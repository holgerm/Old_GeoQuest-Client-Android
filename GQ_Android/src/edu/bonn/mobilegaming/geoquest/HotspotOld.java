package edu.bonn.mobilegaming.geoquest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.qeevee.gq.map.GoogleHotspotOverlay;
import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

/**
 * Hotspots are interaction points on a mapmission map. They have a image and a
 * referenced mission that is started when the player taps on the hotspot.
 * 
 * @author Krischan Udelhoven
 * @author Folker Hoffmann
 * 
 */
public class HotspotOld {

	/**
	 * Hashtable mapping id to Hotspot
	 */
	private static Hashtable<String, HotspotOld> allHotspots = new Hashtable<String, HotspotOld>();

	public static Set<Entry<String, HotspotOld>> getAllHotspots() {
		return allHotspots.entrySet();
	}

	public static List<HotspotOld> getListOfHotspots() {
		List<HotspotOld> list = new ArrayList<HotspotOld>();
		list.addAll(allHotspots.values());
		return list;
	}

	/**
	 * @param id
	 *            the identifier of the hotspot as specified in game.xml.
	 * @return the Hotspot object or null if no hotspot with ID id exists.
	 */
	public static HotspotOld getExisting(String id) {
		return (allHotspots.get(id));
	}

	/**
	 * @param id
	 * @return the Hotspot object for the given id. If no hotspot with ID id
	 *         existed, a new is created.
	 */
	public static HotspotOld get(String id) {
		if (allHotspots.containsKey(id))
			return (allHotspots.get(id));
		return (new HotspotOld(id));
	}

	public static HotspotOld create(Mission _parent, Element _hotspotNode) {
		String _id = _hotspotNode.selectSingleNode("@id").getText();
		if (!allHotspots.containsKey(_id)) {
			new HotspotOld(_id);
		}
		HotspotOld h = allHotspots.get(_id);

		Log.d(h.getClass().getName(), "initiating hotspot. id=" + _id);

		h.init(_parent, _hotspotNode);

		return (h);
	}

	private HotspotOld(String _id) {
		Log.d(getClass().getName(), "constructing hotspot. id=" + _id);
		id = _id;
		googleOverlay = new GoogleHotspotOverlay(this);

		allHotspots.put(id, this);
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
		return new BitmapDrawable(bitmap);
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

	public void setVisible(boolean visible) {
		this.visible = visible;
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
	private void init(Mission _parent, Element _hotspotNode)
			throws IllegalHotspotNodeException {
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
			setBitmap(BitmapUtil.loadBitmap(markerPath, false));
		} else {
			setBitmap(((BitmapDrawable) GeoQuestApp.getInstance()
					.getResources()
					.getDrawable(R.drawable.default_hotspot_icon)).getBitmap());
		}

		// ID of the hotspot:
		this.id = _hotspotNode.attributeValue("id");

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

		// parentMission = _parent;
	}

	private List<Rule> onEnterRules = new ArrayList<Rule>();
	private List<Rule> onLeaveRules = new ArrayList<Rule>();
	private List<Rule> onTapRules = new ArrayList<Rule>();
	public float halfBitmapHeight;
	public float halfBitmapWidth;
	private boolean active = true;

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
		allHotspots.clear();
	}

	/**
	 * get id
	 * 
	 * @return hotspot id or null if not set
	 */
	public String getId() {
		return id;
	}

	public GeoPoint getPosition() {
		return geoPoint;
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

}
