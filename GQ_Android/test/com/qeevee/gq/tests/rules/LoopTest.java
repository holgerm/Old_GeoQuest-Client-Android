package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.base.Variables;
import com.qeevee.gq.tests.robolectric.GQTestRunner;


@RunWith(GQTestRunner.class)
public class LoopTest extends RuleTest {

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
