package com.qeevee.gq.tests.gamedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.util.FileOperations;

@RunWith(GQTestRunner.class)
public class FileOperationsTest {

	private static final String TEST_ORIGIN_BASE_DIR = "/testutildata/filecopy";
	private static final String TEST_TARGET_PATH = TEST_ORIGIN_BASE_DIR
			+ "/target";

	File origin;
	File targetContainer = TestUtils.getFile(TEST_TARGET_PATH);

	@Before
	public void cleanTarget() {
		File targetDir = TestUtils.getFile(TEST_TARGET_PATH);
		File[] files = targetDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory())
				FileOperations.deleteDirectory(files[i]);
			else
				files[i].delete();
		}
	}

	@Test
	public void targetIsCleanAtSetup() {
		// GIVEN:

		// WHEN:

		// THEN:
		assertEquals(0, targetContainer.list().length);

	}

	@Test
	public void testCopyEmptyDir() {
		// GIVEN:
		origin = TestUtils.getFile(TEST_ORIGIN_BASE_DIR + "/origin_empty");

		// WHEN:
		FileOperations.copyFileOrDir(origin, targetContainer);

		// THEN:
		assertTrue(origin.exists());
		assertTrue(targetContainer.exists());
		assertTrue(targetContainer.isDirectory());
		File[] files = targetContainer.listFiles();
		assertEquals(1, files.length);
		assertEquals("origin_empty", files[0].getName());
	}

	@Test
	public void testCopyOnlyFiles() {
		// GIVEN:
		origin = TestUtils.getFile(TEST_ORIGIN_BASE_DIR + "/origin_only_files");
		shouldBeOriginOnlyFiles(origin);

		// WHEN:
		// FileOperations.copyFileOrDir(origin, targetContainer);

		// THEN:
		shouldBeOriginOnlyFiles(origin);
		// shouldContainOriginOnlyFiles(targetContainer);
	}

	private void shouldContainOriginOnlyFiles(File dir) {
		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
		File[] files = dir.listFiles();
		assertEquals(1, files.length);
		shouldBeOriginOnlyFiles(files[0]);
	}

	private void shouldBeOriginOnlyFiles(File dir) {
		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
		assertTrue(dir.canRead());
		assertTrue(dir.canWrite());
		File[] files = dir.listFiles();
		assertEquals(3, files.length);
		String[] expectedFileNames = { "file1", "file2", "file3" };
		for (int i = 0; i < files.length; i++) {
			assertTrue(files[i].exists());
			assertTrue(files[i].isFile());
			assertTrue(files[i].canRead());
			assertTrue(files[i].canWrite());
			assertEquals(expectedFileNames[i], files[i].getName());
		}
	}
}
