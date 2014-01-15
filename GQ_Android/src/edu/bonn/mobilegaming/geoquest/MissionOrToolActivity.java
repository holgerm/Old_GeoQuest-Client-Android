package edu.bonn.mobilegaming.geoquest;

import org.dom4j.Element;

import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public interface MissionOrToolActivity extends BlockableAndReleasable {

	MissionOrToolUI getUI();

	Element getXML();

}
