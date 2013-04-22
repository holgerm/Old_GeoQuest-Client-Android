package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.location.Location;

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
	protected List<GameItem> games = new ArrayList<GameItem>();

	public String getName() {
		return name;
	}

	public List<GameItem> getGames() {
		return games;
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

	public void sortGameItemsBy(int sortMode) {
		for (int i = 0; i < games.size(); i++) {
			games.get(i).setSortingMode(sortMode);
		}
		Collections.sort(games);
	}

	public void sortGameItemsBy(int sortMode, Location location) {
		for (int i = 0; i < games.size(); i++) {
			games.get(i).setDeviceLocation(location);
			games.get(i).setSortingMode(sortMode);
		}
		Collections.sort(games);
	}

	public List<String> gameNames() {
		List<String> gameNames = new ArrayList<String>();
		for (int i = 0; i < getGames().size(); i++) {
			gameNames.add(getGames().get(i).getName());
		}

		return gameNames;
	}

	public GameItem getGameItem(String gameName) {

		for (int i = 0; i < getGames().size(); i++) {
			if (getGames().get(i).getName().equals(gameName))
				return getGames().get(i);
		}
		return null;
	}

	public boolean isOnServer() {
		return onServer;
	}

	public boolean isOnClient() {
		return onClient;
	}
}
