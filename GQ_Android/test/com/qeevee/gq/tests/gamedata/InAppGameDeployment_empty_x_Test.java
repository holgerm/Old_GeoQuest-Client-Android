package com.qeevee.gq.tests.gamedata;

import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.shouldHaveRepositories;
import static com.qeevee.gq.tests.util.TestUtils.startApp;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.robolectric.WithAssets;

/**
 * Tests the case when the GeoQuest app does not come with any preloaded quests.
 * In particular when the assets directory is completely empty.
 * 
 * @author muegge
 * 
 */
@Ignore
@RunWith(GQTestRunner.class)
@WithAssets("../GQ_Android/test/testassets/empty_x/")
public class InAppGameDeployment_empty_x_Test {

	// === TESTS FOLLOW =============================================

	@Test
	public void noRepositoriesFoundInEmptyAssetsDir() {
		// GIVEN:
		// nothing

		// WHEN:
		startApp();

		// THEN:
		shouldHaveRepositories(0);
	}

	// === HELPERS FOLLOW =============================================

}
