package com.qeevee.gq.tests.gamedata;

import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.repoShouldHaveQuests;
import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.shouldHaveRepositories;
import static com.qeevee.gq.tests.util.TestUtils.startApp;

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
@RunWith(GQTestRunner.class)
@WithAssets("../GQ_Android/test/testassets/r3q312/")
public class InAppGameDeployment_r3q312_Test {

	// === TESTS FOLLOW =============================================

	@Test
	public void findOneRepo() {
		// GIVEN:
		// nothing

		// WHEN:
		startApp();

		// THEN:
		shouldHaveRepositories(3, new String[] { "repo1", "repo2", "repo3" });
		repoShouldHaveQuests("repo1", 3, new String[] { "r3q302-test-game-1_1",
				"r3q302-test-game-1_2", "r3q302-test-game-1_3" });
		repoShouldHaveQuests("repo2", 1,
				new String[] { "r3q302-test-game-2_1" });
		repoShouldHaveQuests("repo3", 2, new String[] { "r3q302-test-game-3_1",
				"r3q302-test-game-3_2" });
	}

	// === HELPERS FOLLOW =============================================

}
