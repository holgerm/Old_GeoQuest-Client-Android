package com.qeevee.gq.tests.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.util.StringTools;

import edu.bonn.mobilegaming.geoquest.Variables;

@RunWith(GQTestRunner.class)
public class TestVariableReplacementInText {

	@Before
	public void cleanVariables() {
		Variables.clean();
	}

	// === TESTS FOLLOW =============================================

	@Test
	public void letterBasedVarNames() {
		// GIVEN:
		Variables.setValue("myVar", "Hello");
		Variables.setValue("yourVar", "are");
		Variables.setValue("herVar", "?");
		// WHEN:
		String input = "@myVar@ how @yourVar@ you@herVar@";

		// THEN:
		assertEquals("Hello how are you?", StringTools.replaceVariables(input));
	}



	// === HELPER METHODS FOLLOW =============================================

}
