package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import org.dom4j.Element;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import edu.bonn.mobilegaming.geoquest.BlockableAndReleasable;
import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;

public abstract class MissionUI extends GeoQuestUI implements MissionOrToolUI {

	public MissionUI(MissionActivity activity) {
		super(activity);
		ibm = new InteractionBlockingManager(this);
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

	protected void setBackground() {
		Element xmlMissionNode = getMissionXML();
		String bg = (String) XMLUtilities.getAttribute("bg",
				XMLUtilities.OPTIONAL_ATTRIBUTE, xmlMissionNode);
		if (bg != null) {
			outerView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
					.loadBitmap(bg, true)));
			return;
		}
		// IF NO BACKGROUND PICTURE IS GIVEN, TRY COLOR:
		String bgColor = (String) XMLUtilities.getAttribute("bgcolor",
				XMLUtilities.OPTIONAL_ATTRIBUTE, xmlMissionNode);
		if (bgColor != null) {
			outerView.setBackgroundColor(Integer.parseInt(bgColor));
			return;
		}
	};

}
