package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.base.Variablesables;
import com.qeevee.gq.tests.robolectric.GQTestRunner;


@RunWith(GQTestRunner.class)
public class IfTest extends RuleTest {

	@Test
	public void testIfConditionThen() {
		loadXMLFile("testIf.xml");

		assertFalse(Variables.isDefined("then"));
		assertFalse(Variables.isDefined("else"));

		applyRule(0);
		assertTrue(Variables.isDefined("then"));
		assertEquals(1.0d, Variables.getValue("then"));
		assertFalse(Variables.isDefined("else"));
	}

	@Test
	public void testIfConditionElse() {
		loadXMLFile("testIf.xml");

		assertFalse(Variables.isDefined("then"));
		assertFalse(Variables.isDefined("else"));

		applyRule(1);
		assertFalse(Variables.isDefined("then"));
		assertTrue(Variables.isDefined("else"));
		assertEquals(1.0d, Variables.getValue("else"));
	}
}
