package com.qeevee.gq.tests.gamedata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.widget.ListView;

import com.qeevee.gq.host.ConnectionStrategy;
import com.qeevee.gq.start.GameList;
import com.qeevee.gq.tests.host.MockConnectionStrategy;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Start;

@RunWith(GQTestRunner.class)
public class GameListTests {

	GameList gl;
	ListView lv;

	@Test
	public void name() {
		// GIVEN:
		startWithServerMock("/testresponse/gamelist.json");

		// WHEN:
		startGameList();

		// THEN:
		shouldShowGames(1);
		shouldShowGameWithName("TestGame");

	}

	// === HELPER METHODS FOLLOW =============================================

	private void shouldShowGameWithName(String string) {
		lv = (ListView) TestUtils.getFieldValue(gl, "listView");
		// lv.getAdapter()
	}

	private void shouldShowGames(int i) {
		lv = (ListView) TestUtils.getFieldValue(gl, "listView");
		assertEquals(i, lv.getCount());
	}

	private void startGameList() {
		// gl =
		// org.robolectric.Robolectric.buildActivity(GameList.class).create()
		// .get();
		gl = new GameList();
		gl.onCreate(null);
	}

	private void startWithServerMock(String mockResponseFile) {
		Start start = new Start();
		GeoQuestApp app = (GeoQuestApp) start.getApplication();
		app.onCreate();
		ConnectionStrategy testConnectionStrategy = new MockConnectionStrategy(
				mockResponseFile);
		app.getHostConnector().setConnectionStrategy(testConnectionStrategy);
	}

}