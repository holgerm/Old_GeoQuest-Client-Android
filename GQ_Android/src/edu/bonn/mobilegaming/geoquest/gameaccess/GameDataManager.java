/**
 * 
 */
package edu.bonn.mobilegaming.geoquest.gameaccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.qeevee.gq.res.ResourceManager;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
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

	private static final String REPOSITORIES_BASE_PATH = "/repositories/";
	private static final String TAG = GameDataManager.class.getCanonicalName();

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
				+ REPOSITORIES_BASE_PATH;
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
		List<RepositoryItem> repositories = new ArrayList<RepositoryItem>();
		repositories.addAll(StaticallyDeployedGames.getRepositories());
		return repositories;
	}

	/**
	 * @param repoName
	 * @return null iff no repository with given name repoName found.
	 */
	public static RepositoryItem getRepository(String repoName) {
		List<RepositoryItem> repositories = getRepositories();
		for (Iterator<RepositoryItem> iterator = repositories.iterator(); iterator
				.hasNext();) {
			RepositoryItem repositoryItem = (RepositoryItem) iterator.next();
			if (repoName.equals(repositoryItem.getName()))
				return repositoryItem;
		}
		return null;
	}

	/**
	 * Plays a resource sound file either blocking or non-blocking regarding the
	 * user interaction options on the currently active mission or tool.
	 * 
	 * @param path
	 *            is relative as specified in the game.xml (e.g.
	 *            "sounds/beep.mp3").
	 * @param blocking
	 *            determines whether the interaction is blocked until the media
	 *            file has been played completely.
	 * @return false if player could not start for some reason.
	 */
	public static boolean playAudio(String path, boolean blocking) {
		GameDataManager.stopAudio();
		GameDataManager.mPlayer = new MediaPlayer();
		try {
			GameDataManager.mPlayer.setDataSource(ResourceManager.getResourcePath(path));
			GameDataManager.mPlayer.prepare();
			GameDataManager.mPlayer.start();
			if (blocking)
				GeoQuestApp.blockInteractionOnCurrentActivityByMediaPlayer();
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Could not start Media Player. " + e);
			return false;
		} catch (IllegalStateException e) {
			Log.e(TAG, "Could not start Media Player. " + e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, "Could not start Media Player. " + e);
			return false;
		}
		return true;
	}

	public static void cleanMediaPlayer() {
		if (GameDataManager.mPlayer != null && GameDataManager.mPlayer.isLooping()) {
			Log.d(TAG, "MediaPlayer Resources were cleaned");
			GameDataManager.mPlayer.stop();
			GameDataManager.mPlayer.release();
		}
	}

	public static void stopMediaPlayer() {
		if (GameDataManager.mPlayer != null && GameDataManager.mPlayer.isPlaying()) {
			Log.d(TAG, "MediaPlayer was stoped");
			GameDataManager.mPlayer.stop();
		}
	}

	public static boolean mediaPlayerIsPlaying() {
		if (GameDataManager.mPlayer != null) {
			return GameDataManager.mPlayer.isPlaying();
		}
		return false;
	}

	public static void stopAudio() {
		if (GameDataManager.mPlayer != null) {
			if (GameDataManager.mPlayer.isPlaying()) {
				GameDataManager.mPlayer.stop();
			}
			GameDataManager.mPlayer.reset();
		}
	}

	public static MediaPlayer mPlayer = null;
}
