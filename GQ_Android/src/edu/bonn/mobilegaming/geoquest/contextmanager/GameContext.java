/**
 * Implementation for the diploma thesis "Adaption in digitalen mobilen Lernspielen - Anwendung in GeoQuest"
 * 
 * @author Sabine Polko
 */

package edu.bonn.mobilegaming.geoquest.contextmanager;

public class GameContext {

	private static PlayerContext staticContext = new PlayerContext();
	private LoggingContext loggingContext;
	private MissionContext dynamicContext;

	public GameContext() {
		loggingContext = new LoggingContext();
		dynamicContext = new MissionContext();
	}

	public boolean isEmpty() {
		return loggingContext.isEmpty() || dynamicContext.isEmpty()
				|| staticContext.isEmpty();
	}

	public static boolean isStaticContextEmpty() {
		return staticContext.isEmpty();
	}

	public boolean isDynamicContextEmpty() {
		return dynamicContext.isEmpty();
	}

	public boolean isLoggingContextEmpty() {
		return loggingContext.isEmpty();
	}

	public static void setStaticContext(PlayerContext context) {
		if (context == null) {
			staticContext = new PlayerContext();
		} else {
			staticContext = context;
		}
	}

	public static PlayerContext getStaticContext() {
		return staticContext;
	}

	public void setDynamicContext(MissionContext context) {
		if (context == null) {
			dynamicContext = new MissionContext();
		} else {
			dynamicContext = context;
		}
	}

	public MissionContext getDynamicContext() {
		return dynamicContext;
	}

	public LoggingContext getLoggingContext() {
		return loggingContext;
	}

	public void setLoggingContext(LoggingContext context) {
		if (context == null) {
			loggingContext = new LoggingContext();
		} else {
			loggingContext = context;
		}
	}

}
