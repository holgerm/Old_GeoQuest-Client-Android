package com.qeevee.gq.rules.act;

import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.loc.LocationUtilities;
import com.qeevee.gq.loc.Route;
import com.qeevee.gq.loc.RouteManager;


public class AddRoute extends Action {

	@Override
	protected boolean checkInitialization() {
		return params.containsKey("from") && params.containsKey("to");
	}

	@Override
	public void execute() {
		
		GeoPoint fromGp = null;
		GeoPoint toGp = null;
		boolean isCommingFromPlayerLocation = false;
		boolean isEndingAtPlayerLocation = false;

		
		Hotspot fromHs = HotspotManager.getInstance().getExisting(
				params.get("from"));
		Hotspot toHs = HotspotManager.getInstance().getExisting(
				params.get("to"));
		
		
		if (fromHs == null){
			if(params.get("from").equals("currentPos")){
				isCommingFromPlayerLocation = true;
				Location location = LocationUtilities.getCurrentLocation(
						GeoQuestApp.getInstance().getApplicationContext());
				if(location != null)
					fromGp = new GeoPoint(location);				
			}
			else return;
		}
		else{
			fromGp = fromHs.getOSMGeoPoint();
		}
		
		if (toHs == null){
			if(params.get("to").equals("currentPos")){
				isEndingAtPlayerLocation = true;
				Location location = LocationUtilities.getCurrentLocation(
						GeoQuestApp.getInstance().getApplicationContext());
				if(location != null)
					toGp = new GeoPoint(location);				
			}
			else return;
		}
		else{
			toGp = toHs.getOSMGeoPoint();
		}
		
		String id = params.get("from") + "/" + params.get("to");
		Route route = new Route(fromGp, toGp, id);
		
		if(isCommingFromPlayerLocation) route.setCommingFromPlayerLocation(true);
		if(isEndingAtPlayerLocation) route.setEndingAtPlayerLocation(true);
		if(params.containsKey("color")){
			int color = Integer.parseInt(params.get("color"));
			route.setColor(color);
		}
		if(params.containsKey("width")){
			float width = Float.parseFloat(params.get("width"));
			route.setWidth(width);
		}	
		RouteManager.getInstance().add(id, route);
	}
}
