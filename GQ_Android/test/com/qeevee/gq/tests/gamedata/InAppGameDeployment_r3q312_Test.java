package com.qeevee.gq.tests.gamedata;

import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.repoShouldHaveQuests;
import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.shouldHaveRepositories;
import static com.qeevee.gq.tests.util.TestUtils.startApp;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import android.widget.ListView;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.robolectric.WithAssets;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.RepoListActivity;
import edu.bonn.mobilegaming.geoquest.Start;

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

	RepoListActivity repoListAct;

	// === TESTS FOLLOW =============================================

	@Test
	public void findReposAndQuests() {
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

	@Test
	public void checkReposShownInRepoView() {
		// GIVEN:
		Start startAct = startApp();

		repoListAct = new RepoListActivity();

		// WHEN:
		repoListAct.onCreate(null);

		// THEN:
		shouldShowRepositories(3, new String[] { "repo1", "repo2", "repo3" });

	}

	// === HELPERS FOLLOW =============================================

	private void shouldShowRepositories(int expectedNumberOfShownRepos,
			String... strings) {
		ListView lv = (ListView) repoListAct.findViewById(R.id.repolistList);
		assertEquals(expectedNumberOfShownRepos, lv.getChildCount());
	}

}
