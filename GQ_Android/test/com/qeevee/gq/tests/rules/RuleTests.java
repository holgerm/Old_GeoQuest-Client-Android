package com.qeevee.gq.tests.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.tests.robolectric.GQTestRunner;

import edu.bonn.mobilegaming.geoquest.Variables;

@RunWith(GQTestRunner.class)
public class RuleTests {

	Element root;
	@SuppressWarnings("rawtypes")
	List rules;

	private void loadXMLFile(String fileName) {
		SAXReader reader = new SAXReader();
		URL xmlFileURL = this.getClass().getResource("/testrules/" + fileName);
		Document document;
		try {
			document = reader.read(new File(xmlFileURL.getFile()));
			root = document.getRootElement();
			rules = root.selectNodes("/test/rule");
		} catch (DocumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void applyRule(int i) {
		Rule rule = Rule.createFromXMLElement((Element) rules.get(i));
		rule.apply();
	}

	@Test
	public void testVariableAccess() {
		loadXMLFile("testVariableAccess.xml");
		assertFalse(Variables.isDefined("v1"));
		/*
		 * Take rule 0 where v1 will be set:
		 */
		applyRule(0);
		assertTrue(Variables.isDefined("v1"));
		assertEquals(3.0d, Variables.getValue("v1"));
		/*
		 * Applying rule 1 should increment v by 1, so that it evaluates to 4.0
		 * now:
		 */
		applyRule(1);
		assertEquals(4.0d, Variables.getValue("v1"));
		/*
		 * Applying rule 2 should decrement v by 1, so that it evaluates to 3.0
		 * again:
		 */
		applyRule(2);
		assertEquals(3.0d, Variables.getValue("v1"));
		/*
		 * Applying rule 3 redefines v1 to evaluate to 20.0:
		 */
		applyRule(3);
		assertEquals(20.0d, Variables.getValue("v1"));
	}

	@Test
	public void testNumberComparisonConditions() {
		loadXMLFile("testNumberComparisonConditions.xml");
		/*
		 * Set variable a to 1.0:
		 */
		applyRule(0);
		/*
		 * Rule 1 should only trigger as long as a is smaller than 1.5, hence
		 * the first increment should be applied but the second not:
		 */
		assertEquals(1.0d, Variables.getValue("a"));
		applyRule(1);
		assertEquals(2.0d, Variables.getValue("a"));
		applyRule(1);
		assertEquals(
				"rule 1 should not be applied, since a is not lower than 1.5",
				2.0d, Variables.getValue("a"));
		/*
		 * Rule 2 should now be applied only once, since it tests if a is
		 * greater then 1.5 and if so, decreases its value by 1.0.
		 */
		assertEquals(2.0d, Variables.getValue("a"));
		applyRule(2);
		assertEquals(1.0d, Variables.getValue("a"));
		applyRule(2);
		assertEquals(
				"rule 2 should not be applied, since a is not greater than 1.5",
				1.0d, Variables.getValue("a"));
	}

	@Test
	public void testStringComparisonConditions() {
		loadXMLFile("testStringComparisonConditions.xml");
		/*
		 * Set variable text to "hallo" and variable a to 0:
		 */
		applyRule(0);
		/*
		 * Rule 1 should only trigger when text equals "hallo" which is true,
		 * hence a should be incremented to 1:
		 */
		assertEquals(0.0d, Variables.getValue("a"));
		applyRule(1);
		assertEquals(
				"rule 1 should not be applied, since text equals \"hallo\"",
				1.0d, Variables.getValue("a"));
	}

	@Test
	public void testComparisonAroundEquality() {
		loadXMLFile("testComparisonAroundEquality.xml");
		/*
		 * Set variable a to 1.0:
		 */
		applyRule(0);
		assertEquals(1.0d, Variables.getValue("a"));
		/*
		 * Now "a leq 1" holds, hence it will be incremented to 2.0
		 */
		applyRule(1);
		assertEquals(2.0d, Variables.getValue("a"));
		/*
		 * Now "a geq 2" holds, hence it will be incremented to 3.0
		 */
		applyRule(2);
		assertEquals(3.0d, Variables.getValue("a"));
		/*
		 * Now "a eq 3" holds, hence it will be incremented to 4.0
		 */
		applyRule(3);
		assertEquals(4.0d, Variables.getValue("a"));
	}

	@Test
	public void testAndConditions() {
		loadXMLFile("testAndConditions.xml");
		/*
		 * Set variable a and b to 1.0:
		 */
		applyRule(0);
		assertEquals(1.0d, Variables.getValue("a"));
		assertEquals(1.0d, Variables.getValue("b"));
		/*
		 * Now "a leq 1 and b leq 1" holds, hence both a and b will be
		 * incremented to 2.0
		 */
		applyRule(1);
		assertEquals(2.0d, Variables.getValue("a"));
		assertEquals(2.0d, Variables.getValue("b"));
		/*
		 * Now "a geq 2 and b geq 2" holds, hence both a and b will be
		 * incremented to 3.0
		 */
		applyRule(2);
		assertEquals(3.0d, Variables.getValue("a"));
		assertEquals(3.0d, Variables.getValue("b"));
		/*
		 * Now "a eq 3 and b eq 3" holds, hence both a and b will be incremented
		 * to 4.0
		 */
		applyRule(3);
		assertEquals(4.0d, Variables.getValue("a"));
		assertEquals(4.0d, Variables.getValue("b"));
	}

	@Test
	public void testOrConditions() {
		loadXMLFile("testOrConditions.xml");
		/*
		 * Set variable a and b to 1.0 and 0.0 respectively:
		 */
		applyRule(0);
		assertEquals(1.0d, Variables.getValue("a"));
		assertEquals(0.0d, Variables.getValue("b"));
		/*
		 * Now "a lt 1 or b lt 1" holds, hence both a and b will be incremented
		 * to 2.0 and 1.0 respectively
		 */
		applyRule(1);
		assertEquals(2.0d, Variables.getValue("a"));
		assertEquals(1.0d, Variables.getValue("b"));
	}

	@Test
	public void testNotCondition() {
		loadXMLFile("testNotCondition.xml");
		/*
		 * Set variable a to 1.0:
		 */
		applyRule(0);
		assertEquals(1.0d, Variables.getValue("a"));
		/*
		 * Now "not(a lt 1)" holds, hence a will be incremented to 2.0
		 */
		applyRule(1);
		assertEquals(2.0d, Variables.getValue("a"));
	}

	@Test
	public void testMultipleNumberComparisonConditions() {
		loadXMLFile("testMultipleNumberComparisonConditions.xml");
		/*
		 * Set variable a to 2.0:
		 */
		applyRule(0);
		assertEquals(2.0d, Variables.getValue("a"));
		/*
		 * Now "1 < a < 3" holds, hence a will be incremented to 3.0
		 */
		applyRule(1);
		assertEquals(3.0d, Variables.getValue("a"));
		/*
		 * Now "3 < a < 6" does NOT hold, hence a will NOT be incremented but
		 * still have value 3.0
		 */
		applyRule(2);
		assertEquals(3.0d, Variables.getValue("a"));
	}

	@Test
	public void testMissionStateConditions() {
		loadXMLFile("testMissionStateConditions.xml");
		applyRule(0); // init variables for mocked missions

		assertFalse(Variables.isDefined("v_new"));
		assertFalse(Variables.isDefined("v_success"));
		assertFalse(Variables.isDefined("v_failed"));
		assertFalse(Variables.isDefined("v_running"));

		applyRule(1);
		assertTrue(Variables.isDefined("v_new"));
		assertFalse(Variables.isDefined("v_success"));
		assertFalse(Variables.isDefined("v_failed"));
		assertFalse(Variables.isDefined("v_running"));

		applyRule(2);
		assertTrue(Variables.isDefined("v_new"));
		assertTrue(Variables.isDefined("v_success"));
		assertFalse(Variables.isDefined("v_failed"));
		assertFalse(Variables.isDefined("v_running"));

		applyRule(3);
		assertTrue(Variables.isDefined("v_new"));
		assertTrue(Variables.isDefined("v_success"));
		assertTrue(Variables.isDefined("v_failed"));
		assertFalse(Variables.isDefined("v_running"));

		applyRule(4);
		assertTrue(Variables.isDefined("v_new"));
		assertTrue(Variables.isDefined("v_success"));
		assertTrue(Variables.isDefined("v_failed"));
		assertTrue(Variables.isDefined("v_running"));
	}
}
