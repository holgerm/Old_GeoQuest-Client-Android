package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

import edu.bonn.mobilegaming.geoquest.Variables;

@RunWith(GQTestRunner.class)
public class BreakTest extends RuleTest {

	@Test
	public void testBreakInWhile() {
		loadXMLFile("testBreakInWhile.xml");

		assertFalse(Variables.isDefined("counter"));

		applyRule();
		assertEquals(2.0d, Variables.getValue("counter"));
	}

	@Test
	public void testBreakInSequenceOfActions() {
		// GIVEN:
		loadXMLFile("testBreakInSequenceOfActions.xml");

		assertFalse(Variables.isDefined("counter"));

		// WHEN:
		applyRule();

		// THEN:
		shouldNotHaveBrokenSequence();
	}

	void shouldNotHaveBrokenSequence() {
		assertEquals(6.0d, Variables.getValue("counter"));
	}

}
