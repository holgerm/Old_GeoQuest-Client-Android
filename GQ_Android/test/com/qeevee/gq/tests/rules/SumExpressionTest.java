package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.base.Variables;
import com.qeevee.gq.tests.robolectric.GQTestRunner;


@RunWith(GQTestRunner.class)
public class SumExpressionTest extends RuleTest {

	@Test
	public void testSumOfNumbers() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("numbers"));

		applyRule(0);
		assertTrue(Variables.isDefined("numbers"));
		assertEquals(60.0d, Variables.getValue("numbers"));
	}

	@Test
	public void testEmptySum() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("empty"));

		applyRule(1);
		assertTrue(Variables.isDefined("empty"));
		assertEquals(0.0d, Variables.getValue("empty"));
	}

	@Test
	public void testSumOfStrings() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("strings"));

		applyRule(2);
		assertTrue(Variables.isDefined("strings"));
		assertEquals("Hier kommt ein Text.", Variables.getValue("strings"));
	}

	@Test
	public void testSumOfMixed() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("mixedStartingWithNum"));

		applyRule(3);
		assertTrue(Variables.isDefined("mixedStartingWithNum"));
		assertEquals("3 Söhne hatte König Witzbold, der 4.",
				Variables.getValue("mixedStartingWithNum"));

		assertFalse(Variables.isDefined("mixedStartingWithString"));

		applyRule(4);
		assertTrue(Variables.isDefined("mixedStartingWithString"));
		assertEquals("Es war einmal 1 König Witzbold, der hatte 3 Söhne.",
				Variables.getValue("mixedStartingWithString"));
	}

	@Test
	public void testSumOfVars() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("vars"));

		applyRule(5);
		assertTrue(Variables.isDefined("vars"));
		assertEquals(30.0d, Variables.getValue("vars"));
	}

	@Test
	public void testSumOfRandoms() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("randoms"));

		applyRule(6);
		assertTrue(Variables.isDefined("randoms"));
		assertTrue(110.0d <= (Double) Variables.getValue("randoms"));
		assertTrue(140.0d >= (Double) Variables.getValue("randoms"));
	}

	@Test
	public void testSumOfNumVarRandom() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("mixedNumVarRandom"));

		applyRule(7);
		assertTrue(Variables.isDefined("mixedNumVarRandom"));
		assertTrue(1600.0d <= (Double) Variables.getValue("mixedNumVarRandom"));
		assertTrue(1620.0d >= (Double) Variables.getValue("mixedNumVarRandom"));
	}

	@Test
	public void testSumWithBoolean() {
		loadXMLFile("testSumExpression.xml");

		assertFalse(Variables.isDefined("withBoolean"));

		try {
			applyRule(8);
		} catch (IllegalArgumentException e) {
			assertEquals("Boolean not allowed in sum expression.",
					e.getMessage());
			assertFalse(Variables.isDefined("withBoolean"));
			return;
		}
		fail("Should have thrown IllegalArgumentException");
	}

}
