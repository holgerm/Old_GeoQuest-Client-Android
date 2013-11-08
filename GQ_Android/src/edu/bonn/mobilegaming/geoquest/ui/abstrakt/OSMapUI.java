package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import org.dom4j.Element;
import org.osmdroid.views.MapView;

import edu.bonn.mobilegaming.geoquest.mission.OSMap;

public abstract class OSMapUI extends MissionUI {

	protected MapView mapView;

	/**
	 * Initializes the UI for an NPCTalk mission.
	 * 
	 * @param activity
	 */
	public OSMapUI(OSMap activity) {
		super(activity);
		// TODO assert that mapView is not null
		getOSMap().setMapView(mapView);
		getOSMap().setMapController(mapView.getController());
	}

	protected OSMap getOSMap() {
		return (OSMap) activity;
	}

	/**
	 * Ends this mission if the UI allows that at this moment.
	 */
	public abstract void finishMission();

	@Override
	protected Element getMissionXML() {
		return getOSMap().getXML();
	}

}
