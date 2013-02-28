/**
 * Implementation for the diploma thesis "Adaption in digitalen mobilen Lernspielen - Anwendung in GeoQuest"
 * 
 * @author Sabine Polko
 */

package com.qeevee.gq.tests.adaptation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

import android.content.Context;
import android.location.Location;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextListener;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextListenerType;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManager;
import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManagerException;
import edu.bonn.mobilegaming.geoquest.contextmanager.GameContext;
import edu.bonn.mobilegaming.geoquest.contextmanager.PlayerContext;

@RunWith(GQTestRunner.class)
public class ContextListenerTests {
    PlayerContext staticContext;
    ContextManager cManager;

    @Before
    public void setUp() {
	staticContext = new PlayerContext();
	staticContext.setActivLevel(5);
	staticContext.setAge(50);
	staticContext.setKnowledgeLevel(5);
	staticContext.setTechLevel(5);
	GeoQuestApp app = new GeoQuestApp();
	Context context = app.getApplicationContext();
	cManager = new ContextManager(context);
	cManager.setMaximalGameDuration(100);
	cManager.setStaticContext(staticContext);
    }

    @Test(expected = ContextManagerException.class)
    public void testRegistrationWithEmptyLocationListenerType() {
	LocationListenerDummy locListener = new LocationListenerDummy();
	cManager.registerListener(locListener);
    }

    @Test(expected = ContextManagerException.class)
    public void testRegistrationWithEmptyMissionListenerType() {
	MissionListenerDummy missionListener = new MissionListenerDummy();
	cManager.registerListener(missionListener);
    }

    @Test
    public void testMissionListenerRegistrationAndAnnotation() {
	MissionListenerDummy missionListener = new MissionListenerDummy();
	missionListener.setType(ContextListenerType.MISSION);
	cManager.registerListener(missionListener);

	assertNull(missionListener.getContextData());
	cManager.setStartValues("testMission");
	assertNotNull(missionListener.getContextData());
	cManager.setEndValues("testMission",
			      50);
	assertNotNull(missionListener.getContextData());

    }

    @Test
    public void testLocationListenerRegistrationAndAnnotation() {
	LocationListenerDummy locListener = new LocationListenerDummy();
	locListener.setType(ContextListenerType.LOCATION);

	// TODO location simualtion einfügen

	assertNull(locListener.getLocation());
	cManager.setStartValues("testMission");
	// assertNotNull(locListener.getLocation());
	cManager.setEndValues("testMission",
			      50);
	// assertNotNull(locListener.getLocation());

    }

    @Test(expected = ContextManagerException.class)
    public void testDeregistrationWithEmptyLocationListenerType() {
	LocationListenerDummy locListener = new LocationListenerDummy();
	cManager.deregisterListener(locListener);
    }

    @Test(expected = ContextManagerException.class)
    public void testDeregistrationWithEmptyMissionListenerType() {
	MissionListenerDummy missionListener = new MissionListenerDummy();
	cManager.deregisterListener(missionListener);
    }

    @Test
    public void testDeregistration() {
	MissionListenerDummy missionListener = new MissionListenerDummy();
	missionListener.setType(ContextListenerType.MISSION);
	LocationListenerDummy locListener = new LocationListenerDummy();
	locListener.setType(ContextListenerType.LOCATION);

	// TODO location simualtion einfügen
	assertNull(missionListener.getContextData());
	assertNull(locListener.getLocation());
	cManager.registerListener(missionListener);
	cManager.deregisterListener(missionListener);
	assertNull(missionListener.getContextData());
	assertNull(locListener.getLocation());
	cManager.setStartValues("testMission");
	cManager.setEndValues("testMission",
			      50);
	assertNull(missionListener.getContextData());
	assertNull(locListener.getLocation());
    }

    private class MissionListenerDummy extends ContextListener {

	GameContext cData = null;

	@Override
	public void update(Location loc) {

	}

	@Override
	public void update(GameContext container) {
	    cData = container;
	}

	public GameContext getContextData() {
	    return cData;
	}

	public void setType(ContextListenerType type) {
	    listenerType = type;
	}

    }

    private class LocationListenerDummy extends ContextListener {

	Location loc = null;

	@Override
	public void update(Location loc) {
	    this.loc = loc;
	}

	@Override
	public void update(GameContext container) {
	    // TODO Auto-generated method stub

	}

	public Location getLocation() {
	    return loc;
	}

	public void setType(ContextListenerType type) {
	    listenerType = type;
	}
    }
}
