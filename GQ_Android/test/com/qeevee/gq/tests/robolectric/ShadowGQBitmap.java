package com.qeevee.gq.tests.robolectric;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.shadows.ShadowBitmap;

@Implements(Bitmap.class)
public class ShadowGQBitmap extends ShadowBitmap {

    /**
     * @param source
     * @param x
     * @param y
     * @param width
     * @param height
     * @param m
     * @param filter
     * @return the unchanged source bitmap ;-)
     */
    public static Bitmap createBitmap(Bitmap source,
				      int x,
				      int y,
				      int width,
				      int height,
				      Matrix m,
				      boolean filter) {
	return source;
    }

    @Override
    @Implementation
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null
		|| getClass() != ShadowGQBitmap.class)
	    return false;

	ShadowBitmap that = shadowOf((Bitmap) o);

	if (getHeight() != that.getHeight())
	    return false;
	if (getWidth() != that.getWidth())
	    return false;
	if (getDescription() != null ? !getDescription().equals(that
		.getDescription()) : that.getDescription() != null)
	    return false;

	return true;
    }

}
