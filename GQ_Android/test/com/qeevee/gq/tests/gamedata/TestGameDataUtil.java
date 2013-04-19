package com.qeevee.gq.tests.gamedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;
import edu.bonn.mobilegaming.geoquest.gameaccess.RepositoryItem;

public class TestGameDataUtil {

	/**
	 * @param expectedNumberOfRepos
	 * @param expectedRepoNames
	 *            - in the order of appearance
	 */
	public static void shouldHaveRepositories(int expectedNumberOfRepos,
			String... expectedRepoNames) {
		assertNotNull(GameDataManager.getRepositories());
		List<RepositoryItem> repos = GameDataManager.getRepositories();
		assertEquals(expectedNumberOfRepos, repos.size());
		int i = 0;
		for (Iterator<RepositoryItem> iterator = repos.iterator(); iterator
				.hasNext() && i <= expectedNumberOfRepos; i++) {
			RepositoryItem repositoryItem = (RepositoryItem) iterator.next();
			assertEquals("Repository Name was expected to be different",
					expectedRepoNames[i], repositoryItem.getName());
		}
	}

	public static void repoShouldHaveQuests(String repoName,
			int expectedNumberOfQuests, String... expectedQuestNames) {
		RepositoryItem repo = GameDataManager.getRepository(repoName);
		assertNotNull(repo);
		List<String> expectedButNotYetFoundQuests = new ArrayList<String>(
				Arrays.asList(expectedQuestNames));
		for (Iterator<GameItem> iterator = repo.getGames().iterator(); iterator
				.hasNext();) {
			GameItem foundQuestItem = iterator.next();
			String foundQuest = foundQuestItem.getName();
			boolean unexpectedQuestFound = !expectedButNotYetFoundQuests
					.remove(foundQuest);
			if (unexpectedQuestFound)
				fail("In repo " + repoName + " the quest " + foundQuest
						+ " was found but not expected!");
		}
		if (expectedButNotYetFoundQuests.size() > 0)
			fail("In repo " + repoName + " "
					+ expectedButNotYetFoundQuests.size()
					+ " quest(s) were not found, namely "
					+ expectedButNotYetFoundQuests.toString());
	}

}
