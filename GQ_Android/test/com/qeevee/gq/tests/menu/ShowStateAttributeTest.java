package com.qeevee.gq.tests.menu;

import static com.qeevee.gq.tests.util.TestUtils.startApp;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.menu.GQMenuItem;
import com.qeevee.gq.menu.GQMenuItem.ShowState;
import com.qeevee.gq.tests.robolectric.GQTestRunner;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

@RunWith(GQTestRunner.class)
public class ShowStateAttributeTest {

	@Test
	public void wrongInitialization() {
		// GIVEN:
		startApp();
		GQMenuItem.ShowState showState;

		// WHEN: 
		showState = ShowState.valueOf((CharSequence) "quatsch");

		// THEN:
		shouldContainDefault(showState);
	}

	private void shouldContainDefault(ShowState showState) {
		String expected = GeoQuestApp.getContext()
				.getText(R.string.menuItem_showState_default).toString();
		String given = showState.name();
		assertTrue(given.equalsIgnoreCase(expected));
	}
}
