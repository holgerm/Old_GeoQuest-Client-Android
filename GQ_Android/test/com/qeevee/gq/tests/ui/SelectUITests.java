package com.qeevee.gq.tests.ui;

import static com.qeevee.gq.tests.util.TestUtils.startGameForTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.ui.mock.UseGameSpecUIFactory;
import com.qeevee.gq.ui.web.WebUIFactory;


@RunWith(GQTestRunner.class)
public class SelectUITests {

	@SuppressWarnings("unchecked")
	@Test
	public void selectWebUI() {
		// GIVEN:

		// WHEN:
		startGameForTest("ui/GameUsingWebUI", UseGameSpecUIFactory.class);

		// THEN:
		DefaultUIFactoryTests.shouldUseUI(WebUIFactory.class);
	}
}
