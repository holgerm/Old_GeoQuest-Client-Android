package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.Variables;
import com.qeevee.gq.tests.robolectric.GQTestRunner;


@RunWith(GQTestRunner.class)
public class MultiplyExpressionTest extends RuleTest {

	@Test
	public void testMultiplyOfNumbers() {
		loadXMLFile("testMultiplyExpression.xml");

		assertFalse(Variables.isDefined("numbers"));

		applyRule(0);
		assertTrue(Variables.isDefined("numbers"));
		assertEquals(200.0d, Variables.getValue("numbers"));
	}

	@Test
	public void testEmptyProduct() {
		loadXMLFile("testMultiplyExpression.xml");

		assertFalse(Variables.isDefined("empty"));

		applyRule(1);
		assertTrue(Variables.isDefined("empty"));
		assertEquals(1.0d, Variables.getValue("empty"));
	}

	@Test
	public void testMultiplyOfStrings() {
		loadXMLFile("testMultiplyExpression.xml");

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
	public void testMultiplyOfMixed() {
		loadXMLFile("testMultiplyExpression.xml");

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
	public void testMultiplyOfVars() {
		loadXMLFile("testMultiplyExpression.xml");

		assertFalse(Variables.isDefined("vars"));

		applyRule(4);
		assertTrue(Variables.isDefined("vars"));
		assertEquals(100.0d, Variables.getValue("vars"));
	}

}
