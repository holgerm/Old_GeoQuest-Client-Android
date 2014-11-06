package com.qeevee.gq.res;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.qeevee.gq.R;
import com.qeevee.gq.base.GeoQuestApp;

public class ResourceManager {

	private static final String TAG = ResourceManager.class.getCanonicalName();

	public enum ResourceType {
		IMAGE, AUDIO, VIDEO, TEXT, HTML;

		File getExternalStorageDir() {
			File storageDir = null;
			switch (this) {
			case AUDIO:
				storageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
				break;
			case HTML:
				storageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				break;
			case IMAGE:
				storageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				break;
			case TEXT:
				storageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				break;
			case VIDEO:
				storageDir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
				break;
			default:
				break;
			}
			return storageDir;
		}

		String getSuffix() {
			String suffix = null;
			switch (this) {
			case AUDIO:
				suffix = ".3gp";
				break;
			case HTML:
				suffix = ".html";
				break;
			case IMAGE:
				suffix = ".jpg";
				break;
			case TEXT:
				suffix = ".txt";
				break;
			case VIDEO:
				suffix = ".mp4";
				break;
			default:
				break;

			}
			return suffix;
		}
	};

	public static String getResourcePath(String specifiedPath, ResourceType type) {
		if (specifiedPath == null) {
			Log.e(TAG, "Resource path is null.");
			return null;
		}

		if (specifiedPath.startsWith(ResourceManager.RUNTIME_RESOURCE_PREFIX)) {
			String filepath = specifiedPath
					.substring(ResourceManager.RUNTIME_RESOURCE_PREFIX.length());
			try {
				return getFile(filepath, type).getAbsolutePath();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				return null;
			}
		}

		else {
			return GeoQuestApp.getGameRessourceFile(specifiedPath)
					.getAbsolutePath();
		}
	}

	public static File getFile(String fileName, ResourceType type)
			throws IOException {
		File albumF = getAlbumDir(type);
		File imageF = new File(albumF, fileName + type.getSuffix());
		return imageF;
	}

	/**
	 * @return the album path which is special for the app, the quest and the
	 *         session. E.g. GeoQuest/1234/201405111345200
	 */
	private static String getAlbumBaseName() {
		return GeoQuestApp.getContext().getString(R.string.album_base_name);
	}

	private static File getAlbumDir(ResourceType type) {
		File albumDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			albumDir = new File(type.getExternalStorageDir(),
					getAlbumBaseName());
			String subAlbumName = GeoQuestApp.getContext().getString(
					R.string.album_prefix)
					+ "_"
					+ GeoQuestApp.getInstance().getRunningGameID()
					+ "_"
					+ GeoQuestApp.getInstance().getGameSessionString();
			albumDir = new File(albumDir, subAlbumName);

			if (albumDir != null) {
				if (!albumDir.mkdirs()) {
					if (!albumDir.exists()) {
						Log.d(TAG,
								"failed to create directory "
										+ albumDir.getAbsolutePath());
						return null;
					}
				}
			}

		} else {
			Log.v(TAG, "External storage is not mounted READ/WRITE.");
		}

		return albumDir;
	}

	public static final String RUNTIME_RESOURCE_PREFIX = "@_";

}
