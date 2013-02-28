package edu.bonn.mobilegaming.geoquest.mission;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;

import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
import edu.bonn.mobilegaming.geoquest.HotspotListener;
import edu.bonn.mobilegaming.geoquest.HotspotOld;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public abstract class MapNavigation extends GeoQuestMapActivity implements
	HotspotListener {

    public MissionOrToolUI getUI() {
	// TODO Auto-generated method stub
	return null;
    }

    public void onBlockingStateUpdated(boolean isBlocking) {
	// TODO Auto-generated method stub

    }

    @Override
    protected boolean isRouteDisplayed() {
	return false;
    }

    public abstract IMapView getMapView();

    public abstract IMapController getMapController();

    /**
     * list of hotspots, inited in readxml. main thread may not access this
     * until readxml_completed ist true
     * */
    private List<HotspotOld> hotspots = new ArrayList<HotspotOld>();

    public List<HotspotOld> getHotspots() {
	return hotspots;
    }

}
