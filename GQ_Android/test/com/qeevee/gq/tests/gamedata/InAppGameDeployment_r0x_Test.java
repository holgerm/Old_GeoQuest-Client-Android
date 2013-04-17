package com.qeevee.gq.tests.gamedata;

import static com.qeevee.gq.tests.util.TestUtils.startApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test; 
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.robolectric.WithAssets;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;

/**
 * Tests the case when the GeoQuest app does not come with any preloaded quests.
 * In particular when the assets directory is completely empty.
 * 
 * @author muegge
 * 
 */
@RunWith(GQTestRunner.class)
@WithAssets("../GQ_Android/test/testassets/r0x/")
public class InAppGameDeployment_r0x_Test {

	// === TESTS FOLLOW =============================================

	@Test
	public void noRepositoriesFoundInEmptyAssetsDir() {
		// GIVEN:
		// nothing

		// WHEN:
		startApp();

		// THEN:
		shouldHaveNoRepositories();
	}

	// === HELPERS FOLLOW =============================================

	private void shouldHaveNoRepositories() {
		assertNotNull(GameDataManager.getRepositories());
		assertEquals(0, GameDataManager.getRepositories().size());
	}

}
