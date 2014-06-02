package com.qeevee.gq.loc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.mission.MapOSM;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;


public class RouteManager {

	private Hashtable<String, Route> allRoutes = new Hashtable<String, Route>();
	private static RouteManager instance;

	public static RouteManager getInstance() {
		if (instance == null)
			instance = new RouteManager();
		return instance;
	}

	public List<Route> getListOfRoutes() {
		List<Route> list = new ArrayList<Route>();
		list.addAll(allRoutes.values());
		return list;
	}
	
	public Route getExisting(String id) {
		return (allRoutes.get(id));
	}

	public boolean existsRoute(String id) {
		return allRoutes.containsKey(id);
	}

	public void add(String id, Route route) {
		new CreateRouteAsync().execute(route);
	}
	
	public void remove(String id){
		Activity currentActivity = GeoQuestApp.getCurrentActivity();
		if (currentActivity instanceof MapOSM) {
			((MapOSM) currentActivity).removeRouteFromMap(allRoutes.get(id));
		}
		allRoutes.remove(id);
	}

	public void clear() {
		allRoutes.clear();
	}
	
	public void updateRoutesConnectedToPlayer(Location newLocation){
		List<Route> routes = getListOfRoutes();
		for (Route route : routes) {
			if(route.isCommingFromPlayerLocation()){
				route.setStartingGeoPoint(new GeoPoint(newLocation));
				new CreateRouteAsync().execute(route);
			}
			if(route.isEndingAtPlayerLocation()){
				route.setEndingGeoPoint(new GeoPoint(newLocation));
				new CreateRouteAsync().execute(route);
			}
		}  	
	}	
	
	/**
     * Async task to create the route in a separate thread 
     */
    private class CreateRouteAsync extends AsyncTask<Route, Void, Route> {
    	 	
    	protected Route doInBackground(Route... params) {
            	
    		Route route = params[0];
    		Road road = null;
    		RoadManager roadManager = new OSRMRoadManager();
    		ArrayList<GeoPoint> routePoints = new ArrayList<GeoPoint>();
    		
    		if(route.getStartingGeoPoint() != null && route.getEndingGeoPoint() != null){
        		routePoints.add(route.getStartingGeoPoint());
        		routePoints.add(route.getEndingGeoPoint());
        		road = roadManager.getRoad(routePoints);
        		route.setRoad(road);
    		}
    		return route;
    	}

    	protected void onPostExecute(Route route) {
    		if(!RouteManager.getInstance().existsRoute(route.getId()))
    			RouteManager.getInstance().allRoutes.put(route.getId(), route);
    		
    		Activity currentActivity = GeoQuestApp.getCurrentActivity();
    		if (currentActivity instanceof MapOSM) {
    			((MapOSM) currentActivity).removeRouteFromMap(route);
    			((MapOSM) currentActivity).addRouteToMap(route);
    		}
    	}
    }
}


