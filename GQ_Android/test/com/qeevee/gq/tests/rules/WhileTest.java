package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

import edu.bonn.mobilegaming.geoquest.Variables;

@RunWith(GQTestRunner.class)
public class WhileTest extends RuleTest {

	@Test
	public void testWhileCondition() {
		loadXMLFile("testWhile.xml");

		assertFalse(Variables.isDefined("counter"));

		applyRule(0);
		assertEquals(6.0d, Variables.getValue("counter"));
	}

	@Test
	public void testEmptyWhileCondition() {
		loadXMLFile("testWhile.xml");

		assertFalse(Variables.isDefined("staysEmpty"));

		applyRule(1);
		assertFalse(Variables.isDefined("staysEmpty"));
	}
}
