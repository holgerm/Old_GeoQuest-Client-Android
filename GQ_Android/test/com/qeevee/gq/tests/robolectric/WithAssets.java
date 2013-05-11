package com.qeevee.gq.tests.robolectric;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithAssets {

	/**
	 * @return the relative path to the assets directory to be used for the
	 *         annotated test class.
	 */
	String value();
}
