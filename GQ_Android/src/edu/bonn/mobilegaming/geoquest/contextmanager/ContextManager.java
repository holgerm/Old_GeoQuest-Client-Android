/**
 * Implementation for the diploma thesis "Adaption in digitalen mobilen Lernspielen - Anwendung in GeoQuest"
 * 
 * @author Sabine Polko
 */

package edu.bonn.mobilegaming.geoquest.contextmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.qeevee.gq.loc.SmartLocationListener;

/**
 * The ContextManager gathers context data during a game.
 * 
 * Automatically collected values: GPS-location, ambient noise Computed values:
 * mission duration, remaining game time, covered distance
 * 
 * Values to set: start time of a mission, end time of a mission, result of a
 * mission, maximal game duration, technical level, activity level
 * 
 * It is possible to use a push service or the given pull methods to get actual
 * context data.
 */
public class ContextManager {

	private static final String TAG = "ContextManager";

	private Context applicationContext = null;

	@SuppressWarnings("unused")
	private SmartLocationListener locationListener;

	private HashMap<String, Vector<GameContext>> history;
	private MissionContext actMissionContext;
	private LoggingContext actLoggingContext;
	private String actMissionId;
	private int actSequenceNumber;
	private long totalCoveredDistance;
	private Location actLocation;

	private long gameStartTime;

	private ArrayList<ContextListener> locListener;
	private ArrayList<ContextListener> missionListener;

	public ContextManager(Context applicationContext) {
		this.applicationContext = applicationContext;
		iniFields();
		startLocationListener();
		Log.d(TAG, "ContextManager was started.");
	}

	public void resetHistory() {
		iniFields();
	}

	/**
	 * Sets the starting time for the mission with the id passed as parameter.
	 * Additionally the location at mission start and the covered distance since
	 * game start are stored. If this method is called the first time in the
	 * game, it sets the starting time for the game.
	 * 
	 * @throws ContextManagerException
	 *             if the missionId is null
	 */
	synchronized public void setStartValues(String missionId) {

		long missionStartTime = SystemClock.elapsedRealtime();

		isNotNull(missionId);
		if (actMissionId.length() <= 0) {
			setGameStartTime(missionStartTime);
			actMissionId = missionId;
			++actSequenceNumber;
			setMissionAndLoggingContext(missionStartTime);
			announceMissionContextChange();

			Log.d(TAG, "Start values were successfully set --- missionId: "
					+ missionId + " squenz number: " + actSequenceNumber);
		}
	}

	/**
	 * Sets the remaining time of the game, the duration and result of a
	 * mission.
	 * 
	 * @throws ContextManagerException
	 *             if the missionId is null, unknown to the ContextManager or
	 *             the end values were already set
	 */
	synchronized public void setEndValues(String missionId, int percent) {

		Long endTime = SystemClock.elapsedRealtime();

		isNotNull(missionId);
		notEqualToActualMissionId(missionId);

		actMissionContext.setResult(percent);
		actMissionContext.setMissionDuration(endTime
				- actLoggingContext.getStartTime());

		announceMissionContextChange();
		storeContextInHistory();

		Log.d(TAG, "End values were successfully set --- missionId: "
					+ missionId + " squenz number: " + actSequenceNumber);
	}

	synchronized public void setStaticContext(PlayerContext staticContext) {
		GameContext.setStaticContext(staticContext);
	}

	synchronized public void setMaximalGameDuration(long milliseconds) {
		LoggingContext.setGameDuration(milliseconds);
	}

	synchronized public PlayerContext getStaticContext() {
		return GameContext.getStaticContext();
	}

	/**
	 * @throws ContextManagerException
	 *             if the missionId is null or unknown to the ContextManager
	 */
	synchronized public Vector<GameContext> getHistory(String missionId) {
		isNotNull(missionId);
		isNotInHistory(missionId);
		return history.get(missionId);
	}

	synchronized public GameContext getActualContext() {
		return createGameContext();
	}

	/**
	 * @return milliseconds
	 * @throws ContextManagerException
	 *             if the maximal duration of the game isn't set.
	 */
	synchronized public long getRemainingGameTime() {
		gameDurationIsNotSet();
		return LoggingContext.getGameDuration()
				- (SystemClock.elapsedRealtime() - gameStartTime);
	}

	synchronized public Location getActLocation() {
		return actLocation;
	}

	/**
	 * @return missionId or an empty String
	 */
	synchronized public String getActMissionId() {
		return actMissionId;
	}

	/**
	 * @throws ContextManagerException
	 *             if the type of the Listener isn't set.
	 */
	synchronized public void registerListener(ContextListener listener) {
		listenerTypeIsNotSet(listener);
		switch (listener.getType()) {
		case MISSION:
			if (!missionListener.contains(listener)) {
				missionListener.add(listener);
				Log.d(TAG, "A listener with the type" + listener.getType()
						+ "was successfully registered.");
			}
			break;
		case LOCATION:
			if (!locListener.contains(listener)) {
				locListener.add(listener);
				Log.d(TAG, "A listener with the type" + listener.getType()
						+ "was successfully registered.");
			}
			break;
		default:
			String err = "A type " + listener.getType()
					+ " doesn't exist. The listener couldn't be registered.";
			Log.e(TAG, err);
			throw new ContextManagerException(err);
		}
	}

	/**
	 * @throws ContextManagerException
	 *             if the type of the Listener isn't set.
	 */
	synchronized public void deregisterListener(ContextListener listener) {
		listenerTypeIsNotSet(listener);
		switch (listener.getType()) {
		case MISSION:
			if (!missionListener.isEmpty()) {
				missionListener.remove(listener);
				Log.d(TAG, "A listener with the type" + listener.getType()
						+ "was successfully deregistered.");
			}
			break;
		case LOCATION:
			if (!locListener.isEmpty()) {
				locListener.remove(listener);
				Log.d(TAG, "A listener with the type" + listener.getType()
						+ "was successfully deregistered.");
			}
			break;
		default:
			break;
		}
	}

	private void iniFields() {
		locListener = new ArrayList<ContextListener>();
		missionListener = new ArrayList<ContextListener>();
		history = new HashMap<String, Vector<GameContext>>();
		actMissionContext = new MissionContext();
		actLoggingContext = new LoggingContext();
		actMissionId = new String();
		actSequenceNumber = 0;
		totalCoveredDistance = 0;
		gameStartTime = 0;
	}

	private void startLocationListener() {
		locationListener = new SmartLocationListener(applicationContext) {

			@Override
			protected void onRelevantLocationChanged(Location newLocation) {
				if (actSequenceNumber > 1 && actLocation != null
						&& newLocation != null) {
					totalCoveredDistance += actLocation.distanceTo(newLocation);
					announceLocationChange();
				}
				actLocation = newLocation;
			}

		};
		Log.d(TAG, "LocationListener was initialized.");
	}

	private void setMissionAndLoggingContext(long startTime) {
		actMissionContext = new MissionContext();
		actLoggingContext = new LoggingContext();
		actLoggingContext.setStartTime(startTime);
		actLoggingContext.setSequenceNumber(actSequenceNumber);
		actLoggingContext.setCoveredDistance(totalCoveredDistance);
		actMissionContext.setLocation(actLocation);
	}

	private void setGameStartTime(long startTime) {
		if (actSequenceNumber <= 0) {
			gameStartTime = startTime;
		}
	}

	private void announceLocationChange() {
		for (int i = 0; i < locListener.size(); i++) {
			locListener.get(i).update(actLocation);
		}
	}

	private void announceMissionContextChange() {
		for (int i = 0; i < missionListener.size(); i++) {
			GameContext context = createGameContext();
			missionListener.get(i).update(context);
		}
	}

	private void storeContextInHistory() {
		GameContext context = createGameContext();
		Vector<GameContext> historyContent = history.get(actMissionId);
		if (historyContent == null) {
			historyContent = new Vector<GameContext>();
			history.put(actMissionId, historyContent);
		}
		historyContent.addElement(context);
		actMissionId = new String();
	}

	private GameContext createGameContext() {
		GameContext context = new GameContext();
		context.setDynamicContext(actMissionContext);
		context.setLoggingContext(actLoggingContext);
		return context;
	}

	private void isNotNull(String missionId) {
		if (missionId == null) {
			throw new ContextManagerException(
					"The parameter missionId musn't be null.");
		}
	}

	private void notEqualToActualMissionId(String missionId) {
		if (!actMissionId.equals(missionId)) {
			throw new ContextManagerException("The id " + missionId
					+ " is wrong. The end values weren't set.");
		}
	}

	private void isNotInHistory(String missionId) {
		if (!history.containsKey(missionId)) {
			throw new ContextManagerException(
					"No history exists for the mission " + missionId + ".");
		}
	}

	private void gameDurationIsNotSet() {
		if (LoggingContext.getGameDuration() <= 0) {
			throw new ContextManagerException(
					"The value of the maximal game duration is wrong or not set.");
		}
	}

	private void listenerTypeIsNotSet(ContextListener listener) {
		if (listener.getType() == null) {
			throw new ContextManagerException(
					"The type of the listener isn't set.");
		}
	}
}
