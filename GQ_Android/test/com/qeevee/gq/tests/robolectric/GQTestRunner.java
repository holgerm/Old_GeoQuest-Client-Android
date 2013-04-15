package com.qeevee.gq.tests.robolectric;

import java.io.File;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class GQTestRunner extends RobolectricTestRunner {

	public static final File MANIFEST_FILE = new File(
			"../GQ_Android/AndroidManifest.xml");
	public static final File RESOURCE_DIR = new File("../GQ_Android/res/");
	public static final File ASSETS_DIR = new File("../GQ_Android/assets/");

	public GQTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass, new RobolectricConfig(MANIFEST_FILE, RESOURCE_DIR,
				ASSETS_DIR));
	}

	@Override
	protected void bindShadowClasses() {
		Robolectric.bindShadowClass(ShadowGQBitmap.class);
	}

}
