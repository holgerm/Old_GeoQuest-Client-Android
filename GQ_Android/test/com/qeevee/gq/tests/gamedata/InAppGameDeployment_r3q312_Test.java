package com.qeevee.gq.tests.gamedata;

import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.repoShouldHaveQuests;
import static com.qeevee.gq.tests.gamedata.TestGameDataUtil.shouldHaveRepositories;
import static com.qeevee.gq.tests.util.TestUtils.callMethod;
import static com.qeevee.gq.tests.util.TestUtils.startApp;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.widget.ListView;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.robolectric.WithAssets;
import com.xtremelabs.robolectric.Robolectric;

import edu.bonn.mobilegaming.geoquest.GameListActivity;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.RepoListActivity;

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
	GameListActivity gameListAct;

	// === TESTS FOLLOW =============================================

	@Test
	public void findReposAndQuests() {
		// GIVEN:
		// nothing

		// WHEN:
		startApp();

		// THEN:
		shouldHaveRepositories(3, new String[] { "repo1", "repo2", "repo3" });
		repoShouldHaveQuests("repo1", 3, new String[] { "r3q312-test-game-1_1",
				"r3q312-test-game-1_2", "r3q312-test-game-1_3" });
		repoShouldHaveQuests("repo2", 1,
				new String[] { "r3q312-test-game-2_1" });
		repoShouldHaveQuests("repo3", 2, new String[] { "r3q312-test-game-3_1",
				"r3q312-test-game-3_2" });
	}

	@Test
	public void checkReposShownInRepoView() {
		// GIVEN:
		startApp();
		repoListAct = new RepoListActivity();

		// WHEN:
		repoListAct.onCreate(null);

		// THEN:
		shouldShowRepositories(3, new String[] { "repo1", "repo2", "repo3" });
	}

	@Test
	public void checkQuestsShownInGameListView() {
		// GIVEN:
		startApp();
		repoListAct = new RepoListActivity();
		repoListAct.onCreate(null);

		// WHEN:
		selectRepo("repo1");

		// THEN:
		shoudShowQuests(3, new String[] { "r3q312-test-game-1_1",
				"r3q312-test-game-1_2", "r3q312-test-game-1_3" });

		// WHEN:
		selectRepo("repo2");

		// THEN:
		shoudShowQuests(1, new String[] { "r3q312-test-game-2_1" });

		// WHEN:
		selectRepo("repo3");

		// THEN:
		shoudShowQuests(2, new String[] { "r3q312-test-game-3_1",
				"r3q312-test-game-3_2" });

	}

	// === HELPERS FOLLOW =============================================

	private void shouldShowRepositories(int expectedNumberOfShownRepos,
			String... expectedRepoNames) {
		if (expectedRepoNames.length != expectedNumberOfShownRepos)
			throw new IllegalArgumentException(
					"Number of expected repos differs from given array of expected repo names.");
		ListView lv = (ListView) repoListAct.findViewById(R.id.repolistList);
		assertEquals(expectedNumberOfShownRepos, lv.getChildCount());
		for (int i = 0; i < expectedRepoNames.length; i++) {
			assertEquals(expectedRepoNames[i], lv.getItemAtPosition(i)
					.toString());
		}
	}

	private void selectRepo(String repoName) {
		Intent intent = new Intent(GeoQuestApp.getContext(),
				edu.bonn.mobilegaming.geoquest.GameListActivity.class);
		intent.putExtra("edu.bonn.mobilegaming.geoquest.REPO", repoName);
		gameListAct = new GameListActivity();
		Robolectric.shadowOf(gameListAct).setIntent(intent);
		gameListAct.onCreate(null);
		callMethod(gameListAct, "onResume", null, null);
	}

	private void shoudShowQuests(int expectedNumberOfShownGames,
			String... expectedGameNames) {
		if (expectedGameNames.length != expectedNumberOfShownGames)
			throw new IllegalArgumentException(
					"Number of expected games differs from given array of expected game names.");
		ListView lv = (ListView) gameListAct.findViewById(R.id.gamelistList);
		assertEquals(expectedNumberOfShownGames, lv.getChildCount());
	}

}
