package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.GeoQuestLocationListener;
import edu.bonn.mobilegaming.geoquest.HotspotOld;

import android.location.Location;
import android.util.Log;

/**
 * This is a data object class representing a repository as runtime object.
 * 
 * @author muegge
 * 
 */
public class RepositoryItem {
	
	private String name;
	private boolean onServer = false;
	private boolean onClient = false;
	private List<GameItem> games = new ArrayList<GameItem>();

	public String getName() {
		return name;
	}

	public RepositoryItem(String repoName) {
		this.name = repoName;
		games.clear();
	}

	public void setOnServer() {
		this.onServer = true;
	}

	public void setOnClient() {
		this.onClient = true;
	}

	public void addGame(GameItem gameItem) {
		games.add(gameItem);
	}
	
	public void sortGameItemsBy(int sortMode){
		for(int i = 0; i<games.size(); i++){
			games.get(i).setSortingMode(sortMode);
		}
		Collections.sort(games);
	}
	
	public void sortGameItemsBy(int sortMode, Location location){
		for(int i = 0; i<games.size(); i++){
			games.get(i).setDeviceLocation(location);
			games.get(i).setSortingMode(sortMode);
		}
		Collections.sort(games);
	}

	public List<String> gameNames() {	
		List<String> gameNames = new ArrayList<String>();
		for(int i = 0; i<games.size(); i++){
			gameNames.add(games.get(i).getName());
		}
		
		return gameNames;
	}

	public GameItem getGameItem(String gameName) {
		
		for(int i = 0; i<games.size(); i++){
			if(games.get(i).getName().equals(gameName)) return games.get(i);
		}
		return null;
	}
}
