package com.qeevee.gq.host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class DefaultConnectionStrategy implements ConnectionStrategy {

	public static final int GEOQUEST_PORTAL_ID = 1;

	private static final String TAG = DefaultConnectionStrategy.class
			.getCanonicalName();

	public DefaultConnectionStrategy() {
	}

	public String getGamesJSONString() {
		try {
			String result = new RetrieveGameListAsJSONTask().execute(
					(Void[]) null).get();
			return result;
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	private class RetrieveGameListAsJSONTask extends
			AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... unused) {
			try {
				return connect(HostConnector.GQ_HOST_BASE_URL + "/json/"
						+ GEOQUEST_PORTAL_ID + "/publicgames");
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
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
					String result = convertStreamToString(instream);
					instream.close();
					return result;
				}
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}

		private String convertStreamToString(InputStream is) {
			/*
			 * To convert the InputStream to String we use the
			 * BufferedReader.readLine() method. We iterate until the
			 * BufferedReader return null which means there's no more data to
			 * read. Each line will appended to a StringBuilder and returned as
			 * String.
			 */
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}

	}
}
