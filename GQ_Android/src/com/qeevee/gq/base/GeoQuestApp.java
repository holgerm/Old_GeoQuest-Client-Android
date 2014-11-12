package com.qeevee.gq.base;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.qeevee.gqdefault.R;
import com.qeevee.gqdefault.R.drawable;
import com.qeevee.gqdefault.R.string;
import com.qeevee.gq.commands.EndGame;
import com.qeevee.gq.loc.Hotspot;
import com.qeevee.gq.loc.HotspotManager;
import com.qeevee.gq.res.ResourceManager;
import com.qeevee.gq.res.ResourceManager.ResourceType;
import com.qeevee.gq.start.LandingScreen;
import com.qeevee.gq.ui.InteractionBlocker;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.StringTools;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;
import edu.bonn.mobilegaming.geoquest.gameaccess.RepositoryItem;

public class GeoQuestApp extends Application implements InteractionBlocker {

	public static boolean useAdaptionEngine = false;
	private static boolean adaptionEngineLibAvailable = false;

	// private static HostConnector[] hostConnectorsOLD;

	// public static HostConnector[] getHostConnectorsOLD() {
	// if (hostConnectorsOLD == null) {
	// int[] hostIDs = Configuration.getPortalIDs();
	// hostConnectorsOLD = new HostConnector[hostIDs.length];
	// for (int i = 0; i < hostIDs.length; i++) {
	// hostConnectorsOLD[i] = new HostConnector(hostIDs[i]);
	// }
	// }
	// return hostConnectorsOLD;
	// }
	//
	// public void setHostConnectorsOLD(HostConnector[] connectors) {
	// hostConnectorsOLD = connectors;
	// }

	private MapView googleMap;
	private boolean isInGame = false;
	private boolean usingAutostart = false;

	public boolean isUsingAutostart() {
		return usingAutostart;
	}

	public void setUsingAutostart(boolean usesAutostart) {
		this.usingAutostart = usesAutostart;
	}

	public void setGoogleMap(com.google.android.maps.MapView value) {
		this.googleMap = value;
	}

	public MapView getGoogleMap() {
		return this.googleMap;
	}

	public static final int DIALOG_ID_START_GAME = 0;
	public static final int DIALOG_ID_DOWNLOAD_GAME = 1;
	public static final int DIALOG_ID_DOWNLOAD_REPO_DATA = 2;

	private static final String TAG = GeoQuestApp.class.getCanonicalName();
	public static final String MAIN_PREF_FILE_NAME = "GeoQuestPreferences";
	public static final String GQ_MANUAL_LOCATION_PROVIDER = "GeoQuest Manual Location Provider";
	private static GeoQuestApp theApp = null;

	private ArrayList<Activity> activities = new ArrayList<Activity>();
	private Map<String, MissionOrToolActivity> missionActivities = new HashMap<String, MissionOrToolActivity>();
	private Bitmap missingBitmap;

	public Bitmap getMissingBitmap() {
		return missingBitmap;
	}

	private static File currentGameDir = null;

	public static int currentSortMode = GameItem.SORT_GAMELIST_BY_DEFAULT;

	private static Activity currentActivity;

	public static Activity getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * @param currentActivity
	 *            the currently activated mission or tool activity
	 */
	public static void setCurrentActivity(Activity activity) {
		currentActivity = activity;
	}

	public Activity getRunningActivityByID(String id) {
		if (!missionActivities.containsKey(id))
			return null;
		else
			return (Activity) missionActivities.get(id);
	}

	public static GeoQuestApp getInstance() {
		if (theApp == null)
			Log.e(TAG, "GeoQuestApp instance accessed but not created yet!");
		return theApp;
	}

	GeoQuestLocationListener locationListener;

	@Override
	public void onCreate() {
		super.onCreate();
		theApp = this;

		missingBitmap = BitmapUtil.loadBitmapFromResource(
				R.drawable.missingbitmap, 100, 100, false);
		BitmapUtil.initializePools();

		// read configs from assets file:
		Configuration.initialize();

		// TODO: initialize all preferences to their defaults if they are not
		// already set
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (!prefs.contains(Preferences.PREF_KEY_SERVER_URL)) {
			prefs.edit()
					.putString(Preferences.PREF_KEY_SERVER_URL,
							getString(R.string.geoquest_server_url)).commit();
		}
		currentSortMode = getRecentSortingMode();
		setImprint(new Imprint(null));

		// register location changed listener:
		locationListener = new GeoQuestLocationListener(this) {
			public void onRelevantLocationChanged(Location location) {
				super.onRelevantLocationChanged(location);

				// update global variables:
				Variables.setValue(Variables.LOCATION_LAT,
						Math.round(location.getLatitude() * 1E6));
				Variables.setValue(Variables.LOCATION_LONG,
						Math.round(location.getLongitude() * 1E6));

				// calculate distance to hotspots
				for (Iterator<Hotspot> i = HotspotManager.getInstance()
						.getListOfHotspots().listIterator(); i.hasNext();) {
					Hotspot hotspot = i.next();
					if (hotspot.isActive())
						hotspot.inRange(location);
				}
			}
		};
	}

	public void addActivity(Activity newActivityOfThisApp) {
		if (newActivityOfThisApp instanceof MissionOrToolActivity) {
			MissionOrToolActivity missionActivity = (MissionOrToolActivity) newActivityOfThisApp;
			String id = missionActivity.getMissionID();
			if (missionActivities.containsKey(id)) {
				Activity oldActivity = (Activity) missionActivities.get(id);
				getActivities().remove(oldActivity);
				oldActivity.finish();
			}
			missionActivities.put(id, missionActivity);
		}
		if (!getActivities().contains(newActivityOfThisApp))
			getActivities().add(newActivityOfThisApp);
	}

	void removeActivity(Activity finishedActivity) {
		if (getActivities().contains(finishedActivity))
			getActivities().remove(finishedActivity);
	}

	public void removeMissionActivity(String missionID) {
		if (missionActivities.containsKey(missionID)) {
			missionActivities.remove(missionID);
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		terminateApp();
		super.onTerminate();
	}

	public static Context getContext() {
		return theApp;
	}

	/**
	 * TODO: rework to serverAvailable().
	 * 
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected();
	}

	/**
	 * Really stops the app and all its activities.
	 * 
	 * Should be invoked when user explicitly selects menu item etc.
	 * 
	 * Should NOT be invoked e.g. when Home Button is pressed.
	 * 
	 */
	public void terminateApp() {
		Activity[] allActivities = new Activity[getActivities().size()];
		getActivities().toArray(allActivities);
		for (int i = 0; i < allActivities.length; i++) {
			getActivities().remove(allActivities[i]);
			allActivities[i].finish();
		}
		GameDataManager.cleanMediaPlayer();
		locationListener.disconnect();
		System.exit(0);
	}

	public void showImprint() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				(Context) GeoQuestApp.getCurrentActivity());
		builder.setTitle(R.string.imprintTitle);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage(GeoQuestApp.getImprint().getCompleteText());
		builder.show();
	}

	public static void setRecentGame(String recentRepo, String recentGame,
			String recentGameFileName) {
		SharedPreferences prefs = theApp.getSharedPreferences(
				GeoQuestApp.MAIN_PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Preferences.PREF_KEY_LAST_USED_REPOSITORY, recentRepo);
		editor.putString(Preferences.PREF_KEY_LAST_PLAYED_GAME_NAME, recentGame);
		editor.putString(Preferences.PREF_KEY_LAST_PLAYED_GAME_FILE_NAME,
				recentGameFileName);
		editor.commit();
	}

	public static String getRecentRepo() {
		return theApp.getSharedPreferences(GeoQuestApp.MAIN_PREF_FILE_NAME,
				Context.MODE_PRIVATE).getString(
				Preferences.PREF_KEY_LAST_USED_REPOSITORY,
				theApp.getText(R.string.local_default_repository).toString());
	}

	public static int getRecentSortingMode() {
		return theApp.getSharedPreferences(GeoQuestApp.MAIN_PREF_FILE_NAME,
				Context.MODE_PRIVATE).getInt(Preferences.PREF_KEY_SORTING_MODE,
				GameItem.SORT_GAMELIST_BY_DEFAULT);
	}

	public static String getRecentGameFileName() {
		return theApp.getSharedPreferences(GeoQuestApp.MAIN_PREF_FILE_NAME,
				Context.MODE_PRIVATE).getString(
				Preferences.PREF_KEY_LAST_PLAYED_GAME_FILE_NAME, null);
	}

	public static String getRecentGame() {
		return theApp.getSharedPreferences(GeoQuestApp.MAIN_PREF_FILE_NAME,
				Context.MODE_PRIVATE).getString(
				Preferences.PREF_KEY_LAST_PLAYED_GAME_NAME, null);
	}

	/**
	 * 
	 * @return the directory in which all files of the currently loaded (and
	 *         played) game are stored.
	 */
	public static File getCurrentGameDir() {
		return currentGameDir;
	}

	public static void setCurrentGameDir(File currentGameDir) {
		GeoQuestApp.currentGameDir = currentGameDir;
	}

	public static List<String> getRepositoryNamesAsList() {
		return new ArrayList<String>(repositoryItems.keySet());
	}

	public static List<String> getNotEmptyRepositoryNamesAsList() {
		List<String> namesOfNonEmptyRepos = new ArrayList<String>();
		for (Iterator<RepositoryItem> iterator = repositoryItems.values()
				.iterator(); iterator.hasNext();) {
			RepositoryItem curRepoItem = iterator.next();
			if (curRepoItem.gameNames().size() > 0)
				namesOfNonEmptyRepos.add(curRepoItem.getName());
		}
		// sort repolist by name
		Collections.sort(namesOfNonEmptyRepos);
		return namesOfNonEmptyRepos;
	}

	public static List<String> getLocalRepositories() {
		return Arrays.asList(getLocalRepoDir(null).list());
	}

	public static List<String> getGameNamesForRepository(String repositoryName) {
		if (repositoryItems == null)
			return new ArrayList<String>();
		RepositoryItem repoItem = repositoryItems.get(repositoryName);
		if (repoItem == null) {
			Log.e(TAG, "Error: repoitem is null");
			return null;
		}
		repoItem.sortGameItemsBy(currentSortMode);
		return repoItem.gameNames();
	}

	public static List<String> getGameNamesForRepositoryWithoutLocation(
			String repositoryName) {
		if (repositoryItems == null)
			return new ArrayList<String>();
		RepositoryItem repoItem = repositoryItems.get(repositoryName);
		if (repoItem == null) {
			Log.e(TAG, "Error: repoitem is null");
		}
		GeoQuestLocationListener locationListener = new GeoQuestLocationListener(
				getContext());
		locationListener.connect();
		Location aktLocation = locationListener.getLastLocation();
		locationListener.disconnect();
		if (aktLocation == null) {
			Toast.makeText(getContext(),
					R.string.error_getting_device_location, Toast.LENGTH_SHORT)
					.show();
			repoItem.sortGameItemsBy(currentSortMode, null);
		} else {
			repoItem.sortGameItemsBy(currentSortMode, aktLocation);
		}

		List<String> gameNamesWithoutLoc = new ArrayList<String>();
		Iterator<String> iterator = repoItem.gameNames().iterator();
		for (; iterator.hasNext();) {
			GameItem gi = repoItem.getGameItem(iterator.next());
			if (gi.getLatitude() == 0. && gi.getLongitude() == 0.) {
				gameNamesWithoutLoc.add(gi.getName());
			}
		}
		return gameNamesWithoutLoc;
	}

	public static List<String> getGameNamesForRepositoryWithLocation(
			String repositoryName) {
		if (repositoryItems == null)
			return new ArrayList<String>();
		RepositoryItem repoItem = repositoryItems.get(repositoryName);
		if (repoItem == null) {
			Log.e(TAG, "Error: repoitem is null");
		}
		GeoQuestLocationListener locationListener = new GeoQuestLocationListener(
				getContext());
		locationListener.connect();
		Location aktLocation = locationListener.getLastLocation();
		locationListener.disconnect();
		if (aktLocation == null) {
			Toast.makeText(getContext(),
					R.string.error_getting_device_location, Toast.LENGTH_SHORT)
					.show();
			repoItem.sortGameItemsBy(currentSortMode, null);
		} else {
			repoItem.sortGameItemsBy(currentSortMode, aktLocation);
		}

		List<String> gameNamesWithLoc = new ArrayList<String>();
		Iterator<String> iterator = repoItem.gameNames().iterator();
		for (; iterator.hasNext();) {
			GameItem gi = repoItem.getGameItem(iterator.next());
			if (gi.getLatitude() != 0. || gi.getLongitude() != 0.) {
				gameNamesWithLoc.add(gi.getName());
			}
		}
		return gameNamesWithLoc;
	}

	public static List<String> getLocalGamesInRepository(String repositoryName) {
		return Arrays.asList(getLocalRepoDir(repositoryName).list());
	}

	public static CharSequence[] getRepositoryNames() {
		CharSequence[] resultArray = new CharSequence[repositoryItems.size()];
		int i = 0;
		for (Iterator<String> iterator = repositoryItems.keySet().iterator(); iterator
				.hasNext();) {
			resultArray[i++] = (CharSequence) iterator.next();
		}
		return resultArray;
	}

	private static Map<String, RepositoryItem> repositoryItems = new HashMap<String, RepositoryItem>();
	private static File runningGameDir;
	private boolean repoDataAvailable = false;
	private org.osmdroid.views.MapView osmap;
	private boolean debugMode;

	public org.osmdroid.views.MapView getOsmap() {
		return osmap;
	}

	public void setOSMap(org.osmdroid.views.MapView osmap) {
		this.osmap = osmap;
	}

	/**
	 * @param handler
	 * @return true if at least one repository and quest has been loaded.
	 */
	public static boolean loadRepoData(GeoQuestProgressHandler handler) {
		repositoryItems.clear();
		// boolean result = loadRepoDataFromServer(handler);
		boolean result = false;
		result |= GameLoader.loadIncludedQuests();
		result |= loadRepoDataFromClient(handler);

		if (handler != null)
			handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_FINISHED);

		if (getInstance() != null) {
			getInstance().setRepoDataAvailable(result);
		}

		return result;
	}

	private static boolean loadRepoDataFromClient(
			GeoQuestProgressHandler handler) {
		boolean success = false;
		repositoryItems.clear();

		try {
			String[] localRepoNames = GameDataManager.getLocalRepoDir(null)
					.list();
			RepositoryItem curRepoItem;
			if (localRepoNames == null)
				return success;
			for (int i = 0; i < localRepoNames.length; i++) {
				if (handler != null)
					handler.sendEmptyMessage(GeoQuestProgressHandler.MSG_PROGRESS);
				boolean alsoOnServer = false;
				for (Iterator<Map.Entry<String, RepositoryItem>> iterator = repositoryItems
						.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, RepositoryItem> entry = iterator.next();
					if (entry.getKey().equals(localRepoNames[i])) {
						alsoOnServer = true;
						success |= completeLocalRepoData(entry.getValue());
						break;
					}
				}

				if (!alsoOnServer) {
					curRepoItem = new RepositoryItem(localRepoNames[i]);
					success |= completeLocalRepoData(curRepoItem);
					curRepoItem.setOnClient();
					repositoryItems.put(localRepoNames[i], curRepoItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.arg1 = R.string.error_fetching_local_repo_list;
			msg.what = GeoQuestProgressHandler.MSG_ABORT_BY_ERROR;
			if (handler != null)
				handler.sendMessage(msg);
			success = false;
		}

		return success;
	}

	/**
	 * Adds games which are only found locally and enhance game info for games
	 * which are found both server- and client-side.
	 * 
	 * @param repoItem
	 * @return true iff there is at least one additional game found locally.
	 */
	private static boolean completeLocalRepoData(RepositoryItem repoItem) {
		boolean additionalGameLocallyFound = false;
		repoItem.setOnClient();
		File localRepoDir = GameDataManager.getLocalRepoDir(repoItem.getName());
		File[] gameDirs = localRepoDir.listFiles(new FileFilter() {

			public boolean accept(File file) {
				if (!file.exists() || !file.isDirectory())
					return false;
				String[] gameFileNames = file.list();
				for (int i = 0; i < gameFileNames.length; i++) {
					if (gameFileNames[i].equals("game.xml"))
						return true;
				}
				return false;
			}

		});

		if (gameDirs == null)
			return false;

		SAXReader xmlReader = new SAXReader();
		for (int i = 0; i < gameDirs.length; i++) {
			try {
				File localGameFile = gameDirs[i].listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.getName().equals("game.xml");
					}
				})[0];
				Document doc = xmlReader.read(localGameFile);
				XPath xpathSelector = DocumentHelper.createXPath("//game");
				Element gameNode = (Element) xpathSelector
						.selectSingleNode(doc);
				String localGameName = gameNode.attributeValue("name");

				if (repoItem.gameNames().contains(localGameName)) {
					// local game also on server => check for update:
					GameItem existingGameItem = repoItem
							.getGameItem(localGameName);
					existingGameItem.setOnClient(true);
					existingGameItem.setLastmodifiedClientSide(localGameFile
							.lastModified());
				} else {
					// game only locally found, i.e. this is an additional game:
					repoItem.addGame(GameItem.createFromGameFileGameNode(
							gameNode, localGameFile.lastModified(),
							gameDirs[i].getName(), repoItem));
					additionalGameLocallyFound = true;
				}
			} catch (DocumentException e) {
				Log.d(TAG, e.toString());
			}

		}

		return additionalGameLocallyFound;
	}

	public static void showMessage(CharSequence text) {
		text = StringTools.replaceVariables((String) text);
		Toast t = Toast.makeText(GeoQuestApp.getContext(), text,
				Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	/**
	 * Creates the URL to the game zip file on the server.
	 * 
	 * @param repositoryName
	 *            the plain name of the repository, e.g. "default",
	 *            "stattreisen" etc.
	 * @param gameName
	 *            the plain file name of the game's zip file, but without the
	 *            extension ".zip", e.g. "wccb" or "tauftour".
	 * @return
	 */
	public static URL makeGameURL(CharSequence repositoryName, String gameName) {
		URL url = null;

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());

		String gamelistURL = prefs.getString(Preferences.PREF_KEY_SERVER_URL,
				GeoQuestApp.getContext()
						.getString(R.string.geoquest_server_url))
				+ "/" + "repositories";

		try {
			url = new URL(gamelistURL + "/" + repositoryName + "/games/"
					+ gameName + ".zip");
		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException: " + gamelistURL + "/"
					+ repositoryName + "/games/" + gameName + ".zip");
		} catch (Exception e) {
			Log.d(TAG, "Exception: " + gamelistURL + "/" + repositoryName
					+ "/games/" + gameName + ".zip");
		}
		return url;
	}

	public void endGame() {
		EndGame command = new EndGame();
		// command.makeUserConfirmAndDoIt(); // TODO
		command.doIt();
	}

	/**
	 * TODO replace by asking for implementing an interface (similar to
	 * NeedsNFC...)
	 */
	public boolean isGameActivity(Activity activity) {
		@SuppressWarnings("rawtypes")
		Class actClass = activity.getClass();
		if (actClass.equals(LandingScreen.class))
			return false;
		else
			return true;
	}

	public static GameItem getGameItem(CharSequence repoName, String gameName) {
		return repositoryItems.get(repoName).getGameItem(gameName);
	}

	public static File getGameXMLFile(CharSequence repoName, String gameFileName) {
		return new File(getLocalRepoDir(repoName), gameFileName + "/game.xml");
		// TODO deal with the case that thie game xml file does not exist or
		// even the game dir.
	}

	public static void setRunningGameDir(File dir) {
		GeoQuestApp.runningGameDir = dir;
	}

	public static File getRunningGameDir() {
		return GeoQuestApp.runningGameDir;
	}

	public static File getGameRessourceFile(String ressourceFilePath) {
		File runningGamedDir = getRunningGameDir();
		if (runningGamedDir == null)
			throw new IllegalArgumentException("Running game is null.");
		String resourcePath = runningGamedDir.getAbsolutePath() + "/"
				+ ressourceFilePath;
		File file = new File(resourcePath);
		if (file.exists() && file.canRead())
			return file;
		else
			throw new IllegalArgumentException(
					"No ressource file found at path \"" + resourcePath + "\".");
	}

	// SOUND STUFF FOLLOWS:

	private static MediaPlayer mPlayer = null;
	private static Imprint imprint;
	private static String gameID;
	private static String gameSession;

	public static Imprint getImprint() {
		return imprint;
	}

	public static void cleanMediaPlayer() {
		if (mPlayer != null && mPlayer.isLooping()) {
			Log.d(TAG, "MediaPlayer Resources were cleaned");
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
		}
	}

	public static void stopMediaPlayer() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			Log.d(TAG, "MediaPlayer was stoped");
			mPlayer.stop();
		}
	}

	public static boolean mediaPlayerIsPlaying() {
		if (mPlayer != null) {
			return mPlayer.isPlaying();
		}
		return false;
	}

	/**
	 * Plays a resource sound file either blocking or non-blocking regarding the
	 * user interaction options on the currently active mission or tool.
	 * 
	 * @param path
	 *            is relative as specified in the game.xml (e.g.
	 *            "sounds/beep.mp3").
	 * @param blocking
	 *            determines whether the interaction is blocked until the media
	 *            file has been played completely.
	 * @return false if player could not start for some reason.
	 */
	public static boolean playAudio(String path, boolean blocking) {
		stopAudio();
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setDataSource(ResourceManager.getResourcePath(path,
					ResourceType.AUDIO));
			mPlayer.prepare();
			mPlayer.start();
			if (blocking)
				blockInteractionOnCurrentActivityByMediaPlayer();
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Could not start Media Player. " + e);
			return false;
		} catch (IllegalStateException e) {
			Log.e(TAG, "Could not start Media Player. " + e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, "Could not start Media Player. " + e);
			return false;
		}
		return true;
	}

	public static void stopAudio() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
		}
	}

	/**
	 * Blocks the user interaction on the currently active mission or tool until
	 * the media player signals completion. This is used for example to prevent
	 * the user to click on proceed buttons before the audio file has not
	 * completely played yet.
	 */
	public static void blockInteractionOnCurrentActivityByMediaPlayer() {
		final BlockableAndReleasable releaseCallBack;
		MissionOrToolActivity missionActivity;
		try {
			missionActivity = (MissionOrToolActivity) getCurrentActivity();
		} catch (ClassCastException cce) {
			Log.e(TAG, getCurrentActivity() + "can not be used for blocking.");
			return;
		}
		if (missionActivity.getUI() != null)
			releaseCallBack = missionActivity.getUI().blockInteraction(
					getInstance());
		else
			releaseCallBack = missionActivity.blockInteraction(getInstance());
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				releaseCallBack.releaseInteraction(getInstance());
			}
		});
	}

	public boolean isInGame() {
		return isInGame;
	}

	/*
	 * TODO: Sollte dies nicht eine "live" Überprüfung der Verbindung zum Server
	 * durchführen? (hm)
	 */
	public boolean isRepoDataLoaded() {
		return repoDataAvailable;
	}

	public static void resetAdaptionEngine() {
		if (adaptionEngineLibAvailable) {
			Log.d(TAG, "Resetting AdaptionEngine.");
			useAdaptionEngine = true;
		}
	}

	public void setInGame(boolean isInGame) {
		this.isInGame = isInGame;
	}

	public void setRepoDataAvailable(boolean repoDataAvailable) {
		this.repoDataAvailable = repoDataAvailable;
	}

	/**
	 * 
	 * @param repositoryName
	 *            the name of the repository directory you want the File to
	 *            (optional parameter). If null is given, the root directory of
	 *            all local game repositories is returned.
	 * @return either the directory of the goven game repository or (if
	 *         repositoryName is null) the root directory of all game
	 *         repositories.
	 * @deprecated use {@link GameDataManager#getLocalRepoDir(CharSequence)}
	 *             instead.
	 */
	static File getLocalRepoDir(CharSequence repositoryName) {
		String relativePath = repoBaseDirPath();
		File repoDir;
		if (repositoryName == null) {
			repoDir = new File(Environment.getExternalStorageDirectory(),
					relativePath);
		} else {
			repoDir = new File(Environment.getExternalStorageDirectory(),
					relativePath + repositoryName + "/");
		}
		return repoDir;
	}

	/**
	 * @deprecated
	 * 
	 * @return the path of the directory containing all repositories.
	 */
	static String repoBaseDirPath() {
		return "/Android/data/" + getInstance().getPackageName()
				+ "/repositories/";
	}

	public void refreshMapDisplay() {
		if (getGoogleMap() != null)
			getGoogleMap().invalidate();

		if (getOsmap() != null)
			getOsmap().invalidate();
	}

	public static boolean isMissionRunning(String id) {
		return getInstance().missionActivities.containsKey(id);
	}

	public static boolean stopMission(String id, Double status) {
		if (!isMissionRunning(id))
			return false;
		else {
			getInstance().missionActivities.get(id).finish(status);
			return true;
		}
	}

	public static void setImprint(Imprint imprint) {
		GeoQuestApp.imprint = imprint;
	}

	public void clean() {
		missionActivities.clear();
	}

	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
	}

	public static void setRunningGameID(String id) {
		gameID = id;
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss", Locale.US);
		gameSession = s.format(new Date());
	}

	public String getRunningGameID() {
		return gameID;
	}

	public String getGameSessionString() {
		return gameSession;
	}

	public void setDebugMode(boolean newState) {
		debugMode = newState;
	}

	public boolean isInDebugmode() {
		return debugMode;
	}

	public void showInfo() {
		String infotext = GeoQuestApp.getContext()
				.getString(R.string.info_text)
				+ " "
				+ GeoQuestApp.getContext().getString(R.string.version);
		GeoQuestApp.showMessage(infotext);
	}

	// public static void recycleImagesFromView(View view) {
	// if (view instanceof ImageView) {
	// Drawable drawable = ((ImageView) view).getDrawable();
	// if (drawable instanceof BitmapDrawable) {
	// BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
	// BitmapUtil
	// .addBitmapToSetOfReusables(bitmapDrawable.getBitmap());
	// // bitmapDrawable.getBitmap().recycle();
	// // bitmapDrawable = null;
	// }
	// } else if (view instanceof ViewGroup) {
	// for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	// recycleImagesFromView(((ViewGroup) view).getChildAt(i));
	// }
	// }
	//
	// }
}