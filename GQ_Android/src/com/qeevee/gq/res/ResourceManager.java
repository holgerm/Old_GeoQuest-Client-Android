package com.qeevee.gq.res;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;

public class ResourceManager {

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String TAG = ResourceManager.class.getCanonicalName();

	public static String getResourcePath(String specifiedPath) {

		if (specifiedPath.startsWith(Globals.RUNTIME_RESOURCE_PREFIX)) {
			String filepath = specifiedPath
					.substring(Globals.RUNTIME_RESOURCE_PREFIX.length());
			try {
				return getImageFile(filepath).getAbsolutePath();
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

	@SuppressLint("SimpleDateFormat")
	public static File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		return getImageFile(imageFileName);
	}

	public static File getImageFile(String imageFileName) throws IOException {
		File albumF = getAlbumDir();
		File imageF = new File(albumF, imageFileName + JPEG_FILE_SUFFIX);
		// File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
		// albumF);
		return imageF;
	}

	/* Photo album for this application */
	private static String getAlbumName() {
		return GeoQuestApp.getContext().getString(R.string.album_name);
	}

	private static File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(GeoQuestApp.getContext().getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	public static File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				albumName);
	}

}
