package com.qeevee.gq.tests.gamedata;

import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Bitmap;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.ui.BitmapUtil;

@RunWith(GQTestRunner.class)
public class ImageResourceTests {

	Throwable thrown;
	Bitmap searchedBitmap;

	@Before
	public void init() {
		thrown = null;
		searchedBitmap = null;
	}

	// === TESTS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	@Test
	public void explicitlyReferingJPG() {
		// GIVEN:
		// nothing

		// WHEN:
		startGameForTest("ImageResourceTest");

		// THEN:
		shouldFindBitmap("drawable/jpgFile.jpg");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void explicitlyReferingPNG() {
		// GIVEN:
		// nothing

		// WHEN:
		startGameForTest("ImageResourceTest");

		// THEN:
		shouldFindBitmap("drawable/pngFile.png");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void implicitlyReferingPNG() {
		// GIVEN:
		// nothing

		// WHEN:
		startGameForTest("ImageResourceTest");

		// THEN:
		shouldFindBitmap("drawable/pngFile", "drawable/pngFile.png");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void implicitlyReferingJPG() {
		// GIVEN:
		// nothing

		// WHEN:
		startGameForTest("ImageResourceTest");

		// THEN:
		shouldFindBitmap("drawable/jpgFile", "drawable/jpgFile.jpg");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ignoreTXTFile() {
		// GIVEN:
		// nothing

		// WHEN:
		startGameForTest("ImageResourceTest");

		// THEN:
		shouldIgnoreBitmap("drawable/txtFile");
		shouldIgnoreBitmap("drawable/txtFile.txt");
	}

	// === HELPERS FOLLOW =============================================

	private void shouldIgnoreBitmap(String pathToNONBitmapFile) {
		try {
			searchedBitmap = BitmapUtil.loadBitmap(pathToNONBitmapFile, false);
		} catch (Throwable t) {
			thrown = t;
		}
		assertEquals(IllegalArgumentException.class, thrown.getClass());
		assertEquals(null, searchedBitmap);
	}

	private void shouldFindBitmap(String searchedBitmapPath,
			String pathToReferenceBitmap) {
		Bitmap referenceBitmap = null;
		try {
			searchedBitmap = BitmapUtil.loadBitmap(searchedBitmapPath, false);
			referenceBitmap = BitmapUtil.loadBitmap(pathToReferenceBitmap,
					false);
		} catch (Throwable t) {
			thrown = t;
		}
		assertEquals(null, thrown);
		assertEquals(referenceBitmap, searchedBitmap);
	}

	private void shouldFindBitmap(String searchedBitmapPath) {
		try {
			searchedBitmap = BitmapUtil.loadBitmap(searchedBitmapPath, false);
		} catch (Throwable t) {
			thrown = t;
		}
		assertEquals(null, thrown);
		assertTrue(searchedBitmap != null);
	}
}
