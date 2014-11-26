package com.qeevee.gq.tests.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;
import com.qeevee.gq.ui.ScreenArea;

@RunWith(GQTestRunner.class)
public class ScreenAreaTests {

	// === TESTS FOLLOW =============================================

	@Test
	public void testConstructor() {
		// GIVEN:
		String screenAreaDefinition = "ScreenArea(10,20,300,500)";

		// WHEN:
		ScreenArea sca = new ScreenArea(screenAreaDefinition);

		// THEN:
		assertEquals((Integer) 10,
				(Integer) TestUtils.getFieldValue(sca, "xMin"));
		assertEquals((Integer) 20,
				(Integer) TestUtils.getFieldValue(sca, "xMax"));
		assertEquals((Integer) 300,
				(Integer) TestUtils.getFieldValue(sca, "yMin"));
		assertEquals((Integer) 500,
				(Integer) TestUtils.getFieldValue(sca, "yMax"));
	}

	// === HELPER METHODS FOLLOW =============================================

}
