package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import org.dom4j.Element;
import org.osmdroid.views.MapView;

import edu.bonn.mobilegaming.geoquest.mission.MapOSM;

public abstract class MapOSM_UI extends MissionUI {

	protected MapView mapView;

	/**
	 * Initializes the UI for an NPCTalk mission.
	 * 
	 * @param activity
	 */
	public MapOSM_UI(MapOSM activity) {
		super(activity);
		// TODO assert that mapView is not null
		getOSMap().setMapView(mapView);
		getOSMap().setMapController(mapView.getController());
	}

	protected MapOSM getOSMap() {
		return (MapOSM) activity;
	}

	/**
	 * Ends this mission if the UI allows that at this moment.
	 */
	public abstract void finishMission();

	@Override
	protected Element getMissionXML() {
		return getOSMap().getXML();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionUI#setBackground()
	 */
	protected void setBackground() {
		// No background usable for OSM
		return;
	}

}
