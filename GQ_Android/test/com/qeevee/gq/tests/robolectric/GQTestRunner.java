package com.qeevee.gq.tests.robolectric;

import java.io.File;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class GQTestRunner extends RobolectricTestRunner {

    public static final String GQ_ANDROID_PROJECT_PATH = "../GQ_Android/";

    public GQTestRunner(Class<?> testClass) throws InitializationError {
	super(testClass, new RobolectricConfig(
		new File(GQ_ANDROID_PROJECT_PATH)));
    }

    @Override
    protected void bindShadowClasses() {
	Robolectric.bindShadowClass(ShadowGQBitmap.class);
    }

}
