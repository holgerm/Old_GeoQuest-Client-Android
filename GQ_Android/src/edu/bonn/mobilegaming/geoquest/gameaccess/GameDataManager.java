/**
 * 
 */
package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.io.File;
import java.util.List;

import android.os.Environment;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

/**
 * This class is responsible for loading, updating and persisting the
 * repositories and games themselves. Meta data is managed by the
 * {@link GameMetadataManager} class.
 * 
 * @author muegge
 * 
 */
public class GameDataManager {

	/**
	 * 
	 * TODO does it have to be public? (hm:) No as soon as we have all game
	 * loading stuff moved into this package we can make it default visible.
	 * 
	 * TODO we (hm) will replace this method by a more abstract
	 * getRepositories() and getGames().
	 * 
	 * @param repositoryName
	 *            the name of the repository directory you want the File to
	 *            (optional parameter). If null is given, the root directory of
	 *            all local game repositories is returned.
	 * @return either the directory of the given game repository or (if
	 *         repositoryName is null) the root directory of all game
	 *         repositories.
	 */
	public static File getLocalRepoDir(CharSequence repositoryName) {
		String relativePath = repoBaseDirPath();
		File repoDir;
		if (repositoryName == null) {
			repoDir = new File(Environment.getExternalStorageDirectory(),
					relativePath);
		} else {
			repoDir = new File(Environment.getExternalStorageDirectory(),
					relativePath + repositoryName + "/");
		}
		return repoDir;
	}

	/**
	 * @return the path of the directory containing all repositories.
	 */
	private static String repoBaseDirPath() {
		return "/Android/data/" + GeoQuestApp.getInstance().getPackageName()
				+ "/repositories/";
	}

	/**
	 * This method collects all accessible repositories, including those
	 * delivered statically with the app, those that the user has downloaded and
	 * which are locally stored on the device as well as those that are
	 * available from the server.
	 * 
	 * @return a list of all accessible repositories.
	 */
	public static List<RepositoryItem> getRepositories() {
		return null;
	}

}
