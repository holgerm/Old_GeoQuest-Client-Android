package com.qeevee.gq.tests.gamedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;
import edu.bonn.mobilegaming.geoquest.gameaccess.RepositoryItem;

public class TestGameDataUtil {

	/**
	 * @param expectedNumberOfRepos
	 * @param expectedRepoNames
	 *            - in the order of appearance
	 */
	public static void shouldHaveRepositories(
			Collection<String> expectedRepoNames) {
		shouldHaveRepositories(expectedRepoNames.size());
		for (Iterator<RepositoryItem> iterator = GameDataManager
				.getRepositories().iterator(); iterator.hasNext();) {
			RepositoryItem repositoryItem = (RepositoryItem) iterator.next();
			assertTrue("Repository " + repositoryItem.getName()
					+ " was found but not expected",
					expectedRepoNames.contains(repositoryItem.getName()));
		}
		List<String> repoNames = new ArrayList<String>();
		for (Iterator<RepositoryItem> iterator = GameDataManager
				.getRepositories().iterator(); iterator.hasNext();) {
			RepositoryItem repoItem = (RepositoryItem) iterator.next();
			repoNames.add(repoItem.getName());
		}
		for (Iterator<String> iterator = expectedRepoNames.iterator(); iterator
				.hasNext();) {
			String expectedRepoName = (String) iterator.next();

			assertTrue("Repository " + expectedRepoName
					+ " was expected but not found",
					repoNames.contains(expectedRepoName));
		}
	}

	public static void shouldHaveRepositories(String[] expectedRepoNames) {
		shouldHaveRepositories(Arrays.asList(expectedRepoNames));
	}

	public static void shouldHaveRepositories(int expectedNrOfRepos) {
		assertNotNull(GameDataManager.getRepositories());
		List<RepositoryItem> repos = GameDataManager.getRepositories();
		assertEquals(expectedNrOfRepos, repos.size());
	}

	public static void checkAllReposAndQuests(
			Map<String, String[]> expectedReposAndQuests) {
		for (Iterator<String> iterator = expectedReposAndQuests.keySet()
				.iterator(); iterator.hasNext();) {
			String repoName = (String) iterator.next();
			repoShouldHaveQuests(repoName, expectedReposAndQuests.get(repoName));
		}
	}

	public static void repoShouldHaveQuests(String repoName,
			String... expectedQuestNames) {
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
