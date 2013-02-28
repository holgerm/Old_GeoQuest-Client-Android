/**
 * Implementation for the diploma thesis "Adaption in digitalen mobilen Lernspielen - Anwendung in GeoQuest"
 * 
 * @author Sabine Polko
 */
package com.qeevee.gq.tests.adaptation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.os.SystemClock;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.robolectric.ShadowSystemClock;
import com.xtremelabs.robolectric.Robolectric;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManager;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManagerException;
import edu.bonn.mobilegaming.geoquest.contextmanager.GameContext;
import edu.bonn.mobilegaming.geoquest.contextmanager.LoggingContext;
import edu.bonn.mobilegaming.geoquest.contextmanager.MissionContext;
import edu.bonn.mobilegaming.geoquest.contextmanager.PlayerContext;

@RunWith(GQTestRunner.class)
public class ContextManagerTests {
    PlayerContext staticContext;
    Context applicationContext;

    @Before
    public void setUp() {
	staticContext = new PlayerContext();
	staticContext.setActivLevel(5);
	staticContext.setAge(50);
	staticContext.setKnowledgeLevel(5);
	staticContext.setTechLevel(5);
	GeoQuestApp app = new GeoQuestApp();
	applicationContext = app.getApplicationContext();

    }

    @Test
    public void testContextManagerCreation() {
	ContextManager cManager = new ContextManager(applicationContext);
	assertNotNull(cManager);
    }

    @Test
    public void testStaticContext() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStaticContext(staticContext);
	PlayerContext context = cManager.getStaticContext();
	assertNotNull(context);
	assertFalse(context.isEmpty());
	assertEquals(staticContext.getAge(),
		     context.getAge());
	assertEquals(staticContext.getActivLevel(),
		     context.getActivLevel());
	assertEquals(staticContext.getKnowledgeLevel(),
		     context.getKnowledgeLevel());
	assertEquals(staticContext.getTechLevel(),
		     context.getTechLevel());

	cManager = new ContextManager(applicationContext);
	cManager.setStaticContext(null);
	context = cManager.getStaticContext();
	assertNotNull(context);
	assertTrue(context.isEmpty());
    }

    @Test(expected = ContextManagerException.class)
    public void testSetStartValuesMissionIdIsNull() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStartValues(null);
    }

    @Test(expected = ContextManagerException.class)
    public void testGetHistoryMissionIdIsNull() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStartValues("m1");
	cManager.setEndValues("m1",
			      20);
	cManager.getHistory(null);
    }

    @Test(expected = ContextManagerException.class)
    public void testGetHistoryUnknownMissionId() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStartValues("m1");
	cManager.setEndValues("m1",
			      20);
	cManager.getHistory("m2");
    }

    // @Test
    public void testHistoryContent() {
	Robolectric.bindShadowClass(ShadowSystemClock.class);
	String missionId = "Mission_1";
	long gameDuration = 100;
	long startTime = 10;
	long midTime = 15;
	long endTime = 40;
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStaticContext(staticContext);
	cManager.setMaximalGameDuration(gameDuration);

	SystemClock.setCurrentTimeMillis(startTime);
	cManager.setStartValues(missionId);
	SystemClock.setCurrentTimeMillis(midTime);
	cManager.setEndValues(missionId,
			      50);
	SystemClock.setCurrentTimeMillis(midTime);
	cManager.setStartValues(missionId);
	SystemClock.setCurrentTimeMillis(endTime);
	cManager.setEndValues(missionId,
			      0);

	Vector<GameContext> missionHistory = cManager.getHistory(missionId);
	assertNotNull(missionHistory);
	assertTrue(missionHistory.size() == 2);

	LoggingContext logContext = missionHistory.get(0).getLoggingContext();
	assertNotNull(logContext);
	assertTrue(startTime == logContext.getStartTime());
	assertTrue(1 == logContext.getSequenceNumber());
	assertEquals(gameDuration,
		     LoggingContext.getGameDuration());

	MissionContext mContext = missionHistory.get(0).getDynamicContext();
	assertNotNull(mContext);
	assertEquals(50,
		     mContext.getResult());
	assertEquals(midTime - startTime,
		     mContext.getMissionDuration());
	assertEquals(midTime - startTime,
		     mContext.getElapsedTime());

	logContext = missionHistory.get(1).getLoggingContext();
	assertNotNull(logContext);
	assertTrue(midTime == logContext.getStartTime());
	assertTrue(2 == logContext.getSequenceNumber());
	assertEquals(gameDuration,
		     LoggingContext.getGameDuration());

	mContext = missionHistory.get(1).getDynamicContext();
	assertNotNull(mContext);
	assertEquals(0,
		     mContext.getResult());
	assertEquals(endTime - midTime,
		     mContext.getMissionDuration());
	assertEquals(endTime - startTime,
		     mContext.getElapsedTime());

    }

    @Test(expected = ContextManagerException.class)
    public void testSetEndValuesMissionIdIsNull() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStartValues("missionId");
	cManager.setEndValues(null,
			      50);
    }

    @Test(expected = ContextManagerException.class)
    public void testSetEndValuesUnknownMissionId() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStartValues("Mission_1");
	cManager.setEndValues("Mission_2",
			      50);
    }

    @Test(expected = ContextManagerException.class)
    public void testSetEndValuesTwice() {
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setStartValues("Mission_1");
	cManager.setEndValues("Mission_1",
			      50);
	cManager.setEndValues("Mission_1",
			      50);
    }

    @Test
    public void testRemainingGameTime() {
	Robolectric.bindShadowClass(ShadowSystemClock.class);
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setMaximalGameDuration(100);
	SystemClock.setCurrentTimeMillis(0);
	assertEquals(100,
		     cManager.getRemainingGameTime());
	SystemClock.setCurrentTimeMillis(50);
	assertEquals(50,
		     cManager.getRemainingGameTime());
    }

    @Test(expected = ContextManagerException.class)
    public void testRemainingGameTimeGameDurationWrongValue() {
	Robolectric.bindShadowClass(ShadowSystemClock.class);
	ContextManager cManager = new ContextManager(applicationContext);
	cManager.setMaximalGameDuration(-100);
	cManager.getRemainingGameTime();
    }

    // @Test
    public void testActualcontext() {
	ContextManager cManager = new ContextManager(applicationContext);
	assertTrue(cManager.getActMissionId().length() == 0);

	GameContext actContext = cManager.getActualContext();
	assertNotNull(actContext);
	assertTrue(actContext.isEmpty());

	cManager.setStartValues("Mission_1");
	assertTrue(cManager.getActMissionId().equals("Mission_1"));
	actContext = cManager.getActualContext();
	assertNotNull(actContext);
	assertTrue(!actContext.isEmpty());

	cManager.setEndValues("Mission_1",
			      50);
	assertTrue(cManager.getActMissionId().length() == 0);
	actContext = cManager.getActualContext();
	assertNotNull(actContext);
	assertTrue(!actContext.isEmpty());
    }

    @Test
    public void testLocation() {
	// TODO -- Sabine -- siehe dummyLocation in LocationSource
    }
}
