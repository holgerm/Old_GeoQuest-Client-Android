package com.qeevee.gq.tests.rules.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.base.Variablesables;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.util.StringTools;


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

	@Test
	public void replaceMultipleOccurences() {
		// GIVEN:
		Variables.setValue("myVar", "hello");

		// WHEN:
		String input = "I say @myVar@, @myVar@ and @myVar@ again.";

		// THEN:
		assertEquals("I say hello, hello and hello again.",
				StringTools.replaceVariables(input));
	}

	@Test
	public void useGQSystemVariables() {
		// GIVEN:
		Variables.setValue("$_gqVar.state", "the value");

		// WHEN:
		String input = "Pretext @$_gqVar.state@ posttext";

		// THEN:
		assertEquals("Pretext the value posttext",
				StringTools.replaceVariables(input));
	}

	@Test
	public void logUsageOfUndefinedVariables() {
		// GIVEN:
		Variables.setValue("greeting", "Good day");

		// WHEN:
		String input = "I say @greeting@ @undefinedVar@!";

		// THEN:
		assertEquals("I say Good day @undefinedVar@!",
				StringTools.replaceVariables(input));
		// TODO check that access has been logged
	}

	// === HELPER METHODS FOLLOW =============================================

}
