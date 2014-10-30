package com.qeevee.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileOperations {

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static boolean copyFileOrDir(File origin, File targetContainerDir) {
		if (!targetContainerDir.exists()) {
			// create new dir:
			targetContainerDir.mkdirs();
		} else {
			// already exists something:
			if (!targetContainerDir.canRead())
				// can not use it:
				return false;
			if (!targetContainerDir.isDirectory()) {
				// delete existing file:
				targetContainerDir.delete();
				targetContainerDir.mkdirs();
			} else {
				// delete existing dir:
				deleteDirectory(targetContainerDir);
				targetContainerDir.mkdirs();
			}
		}
		// now we have a usable empty dir:
		return copyFileOrDirToCleanTarget(origin, targetContainerDir);
	}

	private static boolean copyFileOrDirToCleanTarget(File origin,
			File targetContainerDir) {
		boolean ok = true;
		if (origin.isFile()) {
			ok &= _copyFile(origin, targetContainerDir);
		}
		if (origin.isDirectory()) {
			ok &= _copyDir(origin, targetContainerDir);
		}
		return ok;
	}

	private static boolean _copyDir(File origin, File targetContainerDir) {
		boolean ok = true;
		File dirCopy = new File(targetContainerDir, origin.getName());
		ok = dirCopy.mkdir();
		File[] files = origin.listFiles();
		for (int i = 0; i < files.length; i++) {
			ok &= copyFileOrDir(files[i], targetContainerDir);
		}
		return ok;
	}

	private static boolean _copyFile(File origin, File targetContainerDir) {
		try {
			InputStream is = new FileInputStream(origin);
			File targetFile = new File(targetContainerDir, origin.getName());
			OutputStream os = new FileOutputStream(targetFile);

			byte[] buffer = new byte[1024];

			int length;

			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}

			is.close();
			os.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
