package com.qeevee.gq.host;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.qeevee.util.IO;

import android.os.AsyncTask;
import android.util.Log;

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
				return connect(Host.GQ_HOST_BASE_URL + "/json/"
						+ connectionStrategy.getPortalID() + "/publicgames");
			} catch (Exception e) {
				Log.e(PublicGamesConnectionStrategy.TAG, e.getMessage());
				return null;
			}
		}

		private String connect(String url) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				// Log.i(TAG,response.getStatusLine().toString());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					String result = IO.readStreamIntoString(instream);
					instream.close();
					return result;
				}
			} catch (ClientProtocolException e) {
				Log.e(PublicGamesConnectionStrategy.TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(PublicGamesConnectionStrategy.TAG, e.getMessage());
			}
			return null;
		}

	}
}
