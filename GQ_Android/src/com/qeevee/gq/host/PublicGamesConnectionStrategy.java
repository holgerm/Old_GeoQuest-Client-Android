package com.qeevee.gq.host;

import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.util.Log;

import com.qeevee.util.IO;

public class PublicGamesConnectionStrategy extends AbstractConnectionStrategy {

	static final String TAG = PublicGamesConnectionStrategy.class
			.getCanonicalName();

	/**
	 * Creates a connection strategy to retrieve the games from the standard
	 * host on the given portal.
	 * 
	 * @param portalID
	 */
	public PublicGamesConnectionStrategy(int portalID) {
		this.portalID = portalID;
	}

	public String getGamesJSONString() {
		try {
			String result = new RetrieveJSONforPublicGames().execute(this)
					.get();
			return result;
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	private class RetrieveJSONforPublicGames extends
			AsyncTask<ConnectionStrategy, Void, String> {

		protected String doInBackground(ConnectionStrategy... params) {
			ConnectionStrategy connectionStrategy = params[0];
			try {
				return IO.getHTTPResult(Host.GQ_HOST_BASE_URL + "/json/"
						+ connectionStrategy.getPortalID() + "/publicgames");
			} catch (Exception e) {
				Log.e(PublicGamesConnectionStrategy.TAG, e.getMessage());
				return null;
			}
		}

	}
}
