package edu.bonn.mobilegaming.geoquest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
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
public class HotspotOld /* extends Overlay */{

	/**
	 * Hashtable mapping id to Hotspot
	 */
	private static Hashtable<String, HotspotOld> allHotspots = new Hashtable<String, HotspotOld>();

	public static Set<Entry<String, HotspotOld>> getAllHotspots() {
		return allHotspots.entrySet();
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
		googleOverlay = new Overlay() {

			@Override
			public void draw(Canvas canvas, MapView view, boolean shadow) {
				if (!HotspotOld.this.isVisible())
					return; // If it is invisible there is not really much to
				// draw...
				Projection projection = view.getProjection();
				Point screenPoint = new Point();
				projection.toPixels(geoPoint, screenPoint);

				// draw Bitmap
				canvas.drawBitmap(bitmap, screenPoint.x
						- HotspotOld.this.halfBitmapWidth, screenPoint.y
						- HotspotOld.this.halfBitmapHeight, null);

				// draw interaction circle
				if (drawCircle) {
					// TODO verify that this works
					float mPixRadius = (float) (view.getProjection()
							.metersToEquatorPixels(radius) * (1 / Math.cos(Math
							.toRadians(geoPoint.getLatitudeE6() / 1E6))));
					canvas.drawCircle(screenPoint.x, screenPoint.y, mPixRadius,
							paint);
				}
			}

			/**
			 * tap handler. Tests if the current hotspots is tapped and if so
			 * starts the a new mission.
			 */
			@Override
			public boolean onTap(GeoPoint point, MapView mapView) {

				// hit test
				Projection projection = mapView.getProjection();
				Point screenPoint = new Point();
				projection.toPixels(geoPoint, screenPoint);

				RectF hitTestRect = new RectF();
				hitTestRect.set(-bitmap.getWidth() / 2,
						-bitmap.getHeight() / 2, bitmap.getWidth() / 2,
						bitmap.getHeight() / 2);

				hitTestRect.offset(screenPoint.x, screenPoint.y);

				projection.toPixels(point, screenPoint);
				if (hitTestRect.contains(screenPoint.x, screenPoint.y)) {

					if (!isInRange)
						drawCircle = !drawCircle;
					else {
					}

					// start the event
					Log.d("", "a");
					runOnTapEvent();

					return true;
				}
				return false;
			}

		};
		osmOverlay = new org.osmdroid.views.overlay.Overlay(
				GeoQuestApp.getContext()) {

			@Override
			protected void draw(Canvas canvas, org.osmdroid.views.MapView view,
					boolean arg2) {
				if (!HotspotOld.this.isVisible())
					return; // If it is invisible there is not really much to
				// draw...
				org.osmdroid.views.MapView.Projection projection = view
						.getProjection();
				Point screenPoint = new Point(); // TODO can we reuse a field?
				projection.toPixels(new org.osmdroid.google.wrapper.GeoPoint(
						HotspotOld.this.geoPoint), screenPoint);

				// draw Bitmap
				canvas.drawBitmap(bitmap, screenPoint.x
						- HotspotOld.this.halfBitmapWidth, screenPoint.y
						- HotspotOld.this.halfBitmapHeight, null);

				// draw interaction circle
				if (drawCircle) {
					// TODO verify that this works
					float mPixRadius = (float) (view.getProjection()
							.metersToEquatorPixels(radius) * (1 / Math.cos(Math
							.toRadians(geoPoint.getLatitudeE6() / 1E6))));
					canvas.drawCircle(screenPoint.x, screenPoint.y, mPixRadius,
							paint);
				}
			}

		};
		allHotspots.put(id, this);
	}

	/** location of the hotspot */
	private GeoPoint geoPoint;
	/** icon of the hotspot on the map */
	private Bitmap bitmap;
	/** should the interaction circle be drawn or not? */
	private boolean drawCircle;
	/** true if the user is in range of the hotspot */
	public boolean isInRange;

	/**
	 * Visibility means for example that the hotspot is drawn on the map.
	 * Default is <code>true</code>.
	 */
	private boolean visible = true;
	private org.osmdroid.views.overlay.Overlay osmOverlay;
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

	public org.osmdroid.views.overlay.Overlay getOSMOverlay() {
		return osmOverlay;
	}

	/** radius of the interaction circle */
	private int radius;
	/** needed for painting the interaction circle */
	private Paint paint;
	/** id of the hotspot */
	public String id;

	static String TAG = "Hotspot";

	enum Triggers {
		ON_ENTER, ON_LEAVE
	};

	public void runOnTapEvent() {
		for (Rule rule : onTapRules) {
			rule.apply();
		}
	}

	public void runOnEnterEvent() {
		for (Rule rule : onEnterRules) {
			rule.apply();
		}
	}

	public void runOnLeaveEvent() {
		for (Rule rule : onLeaveRules) {
			rule.apply();
		}
	}

	/** the mapmission the hotspot belongs to */
	// private Mission parentMission;

	private List<HotspotListener> listener = new LinkedList<HotspotListener>();
	private String name;
	private String description;

	// private String iconRessource;

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
		GeoPoint point = new GeoPoint((int) (latitude), (int) (longitude));
		Variables.setValue(Variables.HOTSPOT_PREFIX + id
				+ Variables.LOCATION_SUFFIX, point);

		// image
		String imgsrc = _hotspotNode.attributeValue("img");
		if (imgsrc != null) {
			setBitmap(BitmapUtil.loadBitmap(imgsrc, false));
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

		setActive("true".equals(XMLUtilities.getAttribute("initialActivity",
				R.string.hotspot_default_activity, _hotspotNode)));

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

		geoPoint = point;
		new org.osmdroid.google.wrapper.GeoPoint(point);

		// TODO: use an animation
		// bitmapActive =
		// BitmapFactory.decodeResource(parentMission.getResources(),
		// R.drawable.icon_active);
		drawCircle = false;
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
	private float halfBitmapHeight;
	private float halfBitmapWidth;
	private boolean active;

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
			ruleList.add(Rule.createFromXMLElement(xmlRule, id));
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
		boolean preActive = isInRange;

		if (loc.distanceTo(hotspotLocation) <= radius) {
			isInRange = true;
			paint.setARGB(80, 255, 0, 0);
			// rest of the game
			if (!preActive) {
				fireChanges(Triggers.ON_ENTER); // Der Hotspot wurde aktiv
				runOnEnterEvent();
			}
			return true;
		} else {
			paint.setARGB(80, 0, 0, 255);
			isInRange = false;
			if (preActive) {
				fireChanges(Triggers.ON_LEAVE); // Der Hotspot wurde inaktiv
				runOnLeaveEvent();
			}
			return false;
		}
	}

	/**
	 * informs the registrated hotspotlisteners
	 * 
	 * @param t
	 */
	private void fireChanges(Triggers t) {
		Log.d(TAG, "fireChanges");
		for (HotspotListener hl : listener) {
			if (t.equals(Triggers.ON_ENTER)) {
				isInRange = true;
				hl.onEnterRange(this);
			} else if (t.equals(Triggers.ON_LEAVE)) {
				isInRange = false;
				hl.onLeaveRange(this);
			}
		}

	}

	/**
	 * adds a new hotspot listener. The listener is informed when the player
	 * enters or leaves the interaction circle of the hotspot.
	 * 
	 * @param h
	 *            listener to be added
	 */
	public void addHotspotListener(HotspotListener h) {
		listener.add(h);
	}

	/**
	 * removed a listener
	 * 
	 * @param h
	 *            listener to be removed
	 */
	public void removeHotspotListener(HotspotListener h) {
		listener.remove(h);
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
