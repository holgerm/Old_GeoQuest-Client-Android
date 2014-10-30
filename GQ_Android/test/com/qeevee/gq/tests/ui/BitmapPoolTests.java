package com.qeevee.gq.tests.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Bitmap;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.ui.BitmapPool;
import com.qeevee.ui.BitmapType;
import com.qeevee.ui.BitmapUtil;

@RunWith(GQTestRunner.class)
public class BitmapPoolTests {

	// === TESTS FOLLOW =============================================

	@Test
	public void testInitialCapacityAndSize() {
		// GIVEN:
		int width = 100, height = 100;

		// WHEN:
		BitmapPool pool1 = new BitmapPool(1, width, height);
		BitmapPool pool2 = new BitmapPool(2, width, height);
		BitmapPool pool10 = new BitmapPool(10, width, height);

		// THEN:
		assertEquals(1, pool1.size());
		assertEquals(1, pool1.capacity());
		assertEquals(2, pool2.size());
		assertEquals(2, pool2.capacity());
		assertEquals(10, pool10.size());
		assertEquals(10, pool10.capacity());
	}

	@Test
	public void getCorrectlySizedBitmaps() {
		// GIVEN:
		int width100 = 100, height100 = 100;
		BitmapPool pool100 = new BitmapPool(1, width100, height100);
		int width200 = 100, height200 = 200;
		BitmapPool pool200 = new BitmapPool(1, width200, height200);

		// WHEN:
		Bitmap b100 = pool100.consume();
		Bitmap b200 = pool200.consume();

		// THEN:
		assertEquals(width100, b100.getWidth());
		assertEquals(height100, b100.getHeight());
		assertEquals(width200, b200.getWidth());
		assertEquals(height200, b200.getHeight());
	}

	@Test
	public void consumingReducesSize() {
		// GIVEN:
		int width = 100, height = 100;
		BitmapPool pool = new BitmapPool(3, width, height);
		assertEquals(3, pool.capacity());

		// WHEN:
		pool.consume();
		// THEN:
		assertEquals(2, pool.size());
		assertEquals(3, pool.capacity());

		// WHEN:
		pool.consume();
		// THEN:
		assertEquals(1, pool.size());
		assertEquals(3, pool.capacity());

		// WHEN:
		pool.consume();
		// THEN:
		assertEquals(0, pool.size());
		assertEquals(3, pool.capacity());
	}

	@Test
	public void consumingAboveLimitIsPossible() {
		// GIVEN:
		int width = 100, height = 100;
		BitmapPool pool = new BitmapPool(1, width, height);

		// WHEN:
		Bitmap b = pool.consume();
		// THEN:
		assertEquals(0, pool.size());
		assertNotNull(b);

		// WHEN:
		b = pool.consume();
		// THEN:
		assertEquals(0, pool.size());
		assertNotNull(b);
	}

	@Test
	public void checkReuseForCapacity1() {
		// GIVEN:
		int width = 100, height = 100;
		BitmapPool pool = new BitmapPool(1, width, height);

		// WHEN:
		Bitmap b1 = pool.consume();
		Bitmap b2 = pool.consume();

		// THEN:
		assertEquals(b1, b2);
	}

	@Test
	public void checkFIFOReuseStrategy() {
		// GIVEN:
		int width = 100, height = 100;
		BitmapPool pool = new BitmapPool(3, width, height);

		// WHEN:
		Bitmap b1 = pool.consume();
		Bitmap b2 = pool.consume();
		Bitmap b3 = pool.consume();
		// THEN:
		assertNotSame(b1, b2);
		assertNotSame(b2, b3);
		assertNotSame(b1, b3);

		// WHEN:
		Bitmap b4 = pool.consume();
		// THEN:
		assertNotSame(b4, b2);
		assertNotSame(b2, b3);
		assertNotSame(b4, b3);
		assertEquals(b1, b4);

		// WHEN:
		Bitmap b5 = pool.consume();
		// THEN:
		assertNotSame(b3, b4);
		// assertNotSame(b4, b5);
		assertNotSame(b3, b5);
		assertEquals(b2, b5);

		// WHEN:
		Bitmap b6 = pool.consume();
		// THEN:
		assertEquals(b3, b6);
	}

	@Test
	public void checkInitializedPoolsInApp() {
		// GIVEN:

		// WHEN:
		TestUtils.startApp();

		// THEN:
		assertEquals(3, BitmapUtil.getPool(BitmapType.FULLSCREEN).capacity());
	}

	// === HELPER METHODS FOLLOW =============================================

}
