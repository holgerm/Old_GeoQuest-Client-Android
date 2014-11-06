package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.base.Variablesables;
import com.qeevee.gq.tests.robolectric.GQTestRunner;


@RunWith(GQTestRunner.class)
public class SubtractExpressionTest extends RuleTest {

	@Test
	public void testSubstractionOfNumbers() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("numbers"));

		applyRule(0);
		assertTrue(Variables.isDefined("numbers"));
		assertEquals(20.0d, Variables.getValue("numbers"));
	}

	@Test
	public void testEmptySubtract() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("empty"));

		applyRule(1);
		assertTrue(Variables.isDefined("empty"));
		assertEquals(null, Variables.getValue("empty"));
	}

	@Test
	public void testSubstractionOfStrings() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("strings"));

		try {
			applyRule(2);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"String not allowed in subtract expression. Occured in "
							+ "<string>Does </string> <string>not </string> <string>work.</string>",
					e.getMessage());
			assertFalse(Variables.isDefined("strings"));
			return;
		}
		fail("Should have thrown IllegalArgumentException");
	}

	@Test
	public void testSubstractionOfMixed() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("mixedStartingWithNum"));

		try {
			applyRule(3);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"String not allowed in subtract expression. Occured in "
							+ "<num>3</num> <string>Does not work.</string>",
					e.getMessage());
			assertFalse(Variables.isDefined("mixedStartingWithNum"));
			return;
		}
		fail("Should have thrown IllegalArgumentException");
	}

	@Test
	public void testSubstractionOfVars() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("vars"));

		applyRule(5);
		assertTrue(Variables.isDefined("vars"));
		assertEquals(30.0d, Variables.getValue("vars"));
	}

	@Test
	public void testSubstractionOfRandoms() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("randoms"));

		applyRule(6);
		assertTrue(Variables.isDefined("randoms"));
		assertTrue(80.0d <= (Double) Variables.getValue("randoms"));
		assertTrue(110.0d >= (Double) Variables.getValue("randoms"));
	}

	@Test
	public void testSubstractionOfNumVarRandom() {
		loadXMLFile("testSubtractExpression.xml");

		assertFalse(Variables.isDefined("mixedNumVarRandom"));

		applyRule(7);
		assertTrue(Variables.isDefined("mixedNumVarRandom"));
		assertTrue(380.0d <= (Double) Variables.getValue("mixedNumVarRandom"));
		assertTrue(400.0d >= (Double) Variables.getValue("mixedNumVarRandom"));
	}

}
