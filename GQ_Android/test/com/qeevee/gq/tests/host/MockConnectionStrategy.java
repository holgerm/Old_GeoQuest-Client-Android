package com.qeevee.gq.tests.host;

import static org.junit.Assert.fail;

import java.io.IOException;

import com.qeevee.gq.host.ConnectionStrategy;
import com.qeevee.gq.tests.util.TestUtils;

public class MockConnectionStrategy implements ConnectionStrategy {

	private String mockresponse;

	public MockConnectionStrategy(String jsonFile) {
		try {
			mockresponse = TestUtils.readFile(jsonFile);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Override
	public String getGamesJSONString() {
		return mockresponse;
	}

	public String getDownloadURL(String id) {
		return null;
	}

}
