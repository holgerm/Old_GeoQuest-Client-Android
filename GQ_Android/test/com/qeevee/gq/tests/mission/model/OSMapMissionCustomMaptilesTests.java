//package com.qeevee.gq.tests.mission.model;
//
//import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
//import static org.junit.Assert.*;
//
//import java.io.File;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Environment;
//
//import com.qeevee.gq.history.History;
//import com.qeevee.gq.tests.robolectric.GQTestRunner;
//import com.qeevee.gq.tests.util.TestUtils;
//import com.xtremelabs.robolectric.Robolectric;
//
//import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
//import edu.bonn.mobilegaming.geoquest.GeoQuestMapActivity;
//import edu.bonn.mobilegaming.geoquest.Start;
//import edu.bonn.mobilegaming.geoquest.Variables;
//import edu.bonn.mobilegaming.geoquest.mission.MissionActivity;
//import edu.bonn.mobilegaming.geoquest.mission.OSMap;
//
//@RunWith(GQTestRunner.class)
//public class OSMapMissionCustomMaptilesTests {
//    private Start start;
//    private OSMap osMap;
//
//    @After
//    public void cleanUp() {
//	// get rid of all variables that have been set, e.g. for checking
//	// actions.
//	Variables.clean();
//	History.getInstance().clear();
//    }
//
//    @Before
//    public void prepare() {
//	TestUtils.setMockUIFactory();
//    }
//
//    // === TESTS FOLLOW =============================================
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testIfTilesOverlayIsNullIfNoTilefileExists() {
//	// GIVEN:
//	start = startGameForTest("NoCustomMapTilesTest");	
//	osMap = (OSMap) TestUtils.prepareMapMission("OSMap", "Map_Mission", start);
//	
//	// WHEN:
//	osMap.onCreate(null);
//
//	// THEN: TilesOverlay in OSMap should be null
//	assertNull(osMap.getCustomTilesOverlay());
//    }
//    
//    
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testIfTilesOverlayIsNotNullIfTilefileExists() {
//	// GIVEN:
//	start = startGameForTest("CustomMapTilesTest");
//	osMap = (OSMap) TestUtils.prepareMapMission("OSMap", "Map_Mission", start);
//
//	
//	// WHEN:
//	osMap.onCreate(null);
//
//	// THEN: TilesOverlay in OSMap should not be null
//	assertNotNull(osMap.getCustomTilesOverlay());
//    } 
//    
//    
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testIfExistingTilefileIsCoppiedCorrectly() {
//	// GIVEN:
//	start = startGameForTest("CustomMapTilesTest");
//	osMap = (OSMap) TestUtils.prepareMapMission("OSMap", "Map_Mission", start);
//	
//	// WHEN:
//	osMap.onCreate(null);
//
//	// THEN: "/osmdroid/customTiles.zip" should exist
//	File testFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/customTiles.zip");
//	System.out.println(testFile.getAbsolutePath());
//	assertTrue(testFile.exists());	
//    } 
//    
//    
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testIfCoppiedTilefileIsDeletedAtGameend() {
//	// GIVEN:
//	start = startGameForTest("CustomMapTilesTest");
//	osMap = (OSMap) TestUtils.prepareMapMission("OSMap", "Map_Mission", start);
//	
//	// WHEN:
//	osMap.onCreate(null);
//	GeoQuestApp.getInstance().endGame();
//
//	// THEN: "/osmdroid/customTiles.zip" should not exist
//	File testFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/customTiles.zip");
//	assertFalse(testFile.exists());	
//    }    
//}
