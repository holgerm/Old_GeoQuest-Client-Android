package com.qeevee.gq.tests.robolectric;

import java.io.File;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 * <li>Uses manifest and resources from the GQ_Android project.
 * 
 * <li>You can use @WithAssets annotation in test class to specify the location
 * of the assets directory.
 * 
 * <li>Binds shadow class for Bitmaps
 * 
 * @author muegge
 * 
 */
public class GQTestRunner extends RobolectricTestRunner {

	public static final File MANIFEST_FILE = new File(
			"../GQ_Android/AndroidManifest.xml");
	public static final File RESOURCE_DIR = new File("../GQ_Android/res/");
	public static final File ASSETS_DIR = new File("../GQ_Android/assets/");

	public GQTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass, new RobolectricConfig(MANIFEST_FILE, RESOURCE_DIR,
				getAssetsDir(testClass)));
	}

	private static File getAssetsDir(Class<?> testClass) {
		WithAssets wmd = testClass.getAnnotation(WithAssets.class);
		if (wmd == null)
			return ASSETS_DIR;
		else {
			File assetsDir = new File(wmd.value());
			if (assetsDir.exists() && assetsDir.canRead())
				return assetsDir;
			else
				throw new Error(
						"Test class "
								+ testClass.getSimpleName()
								+ " uses WithAssets annotation with a non readable directory \""
								+ wmd.value() + "\".");
		}
	}

	@Override
	protected void bindShadowClasses() {
		Robolectric.bindShadowClass(ShadowGQBitmap.class);
	}

}
