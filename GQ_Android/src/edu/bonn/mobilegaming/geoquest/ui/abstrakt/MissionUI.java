package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Util;

import edu.bonn.mobilegaming.geoquest.BlockableAndReleasable;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
import edu.bonn.mobilegaming.geoquest.Mission;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;

public abstract class MissionUI extends GeoQuestUI implements MissionOrToolUI {

	/**
	 * @see GeoQuestUI#GeoQuestUI(android.app.Activity)
	 * @param activity
	 */
	public MissionUI(MissionActivity activity) {
		super(activity);
		initUI();
	}

	/**
	 * Does UI initialization which is common for both MissionActivities and
	 * GeoQuestMapActivties.
	 */
	private void initUI() {
		setBackground();
		ibm = new InteractionBlockingManager(this);
	}

	public void disable() {
	}

	public void enable() {
	}

	public MissionUI(GeoQuestMapActivity activity) {
		super(activity);
		initUI();
	}

	protected InteractionBlockingManager ibm;
	protected View outerView;

	public BlockableAndReleasable blockInteraction(InteractionBlocker newBlocker) {
		return ibm.blockInteraction(newBlocker);
	}

	public void releaseInteraction(InteractionBlocker blocker) {
		ibm.releaseInteraction(blocker);
	}

	abstract protected Element getMissionXML();

	/**
	 * Applies mission attributes "bg" or "bgcolor" to that view that is named
	 * "outerview" in layout. Cf. layouts for each mission type.
	 */
	protected void setBackground() {
		if (outerView == null)
			return;

		Element localMissionNode = getMissionXML();
		if (tryBackgroundSettingsFromXMLElement(localMissionNode))
			return;
		else
			tryBackgroundSettingsFromXMLElement(Mission.documentRoot);
	}

	private boolean tryBackgroundSettingsFromXMLElement(Element xmlMissionNode) {
		// Try background image:
		String bg = (String) XMLUtilities.getStringAttribute("bg",
				XMLUtilities.OPTIONAL_ATTRIBUTE, xmlMissionNode);
		if (bg != null) {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, Util.getDisplayWidth(), 0, true)));
			return true;
		}
		// IF NO BACKGROUND PICTURE IS GIVEN, TRY COLOR:
		String bgColor = (String) XMLUtilities.getStringAttribute("bgcolor",
				XMLUtilities.OPTIONAL_ATTRIBUTE, xmlMissionNode);
		if (bgColor != null && !bgColor.trim().equals("")) {
			outerView.setBackgroundColor(Color.parseColor(bgColor));
			return true;
		}
		return false;
	};

	protected float getTextsize() {
		Attribute textSizeAttr = getMissionXML().attribute("textsize");
		if (textSizeAttr != null)
			return Float
					.valueOf(textSizeAttr.getValue().replaceAll("[sp]", ""));
		// if no local mission text size given: look for global game text size:
		textSizeAttr = Mission.getGlobalAttribute("textsize");
		if (textSizeAttr != null)
			return Float
					.valueOf(textSizeAttr.getValue().replaceAll("[sp]", ""));
		// use default text size:
		return GeoQuestApp.getContext().getResources()
				.getDimension(R.dimen.text_size);
	}

	protected int getTextColor() {
		Attribute textColorAttr = getMissionXML().attribute("textcolor");
		if (textColorAttr != null) {
			try {
				return Integer.valueOf(Color.parseColor(textColorAttr
						.getValue()));
			} catch (IllegalArgumentException iae) {
				Log.e(MissionUI.class.getCanonicalName(),
						"Can not parse color name " + textColorAttr.getValue());
			}
		}
		// if no local mission text color given: look for global game text
		// color:
		textColorAttr = Mission.getGlobalAttribute("textcolor");
		if (textColorAttr != null) {
			Integer result;
			try {
				result = Integer.valueOf(Color.parseColor(textColorAttr
						.getValue()));
			} catch (IllegalArgumentException iae) {
				Log.e(MissionUI.class.getCanonicalName(),
						"Can not parse color name " + textColorAttr.getValue());
				return getDefaultTextColor();
			}
			return result;
		}
		// use default text color:
		return getDefaultTextColor();
	}

	private int getDefaultTextColor() {
		return GeoQuestApp.getContext().getResources()
				.getColor(R.color.foreground);
	}

}
