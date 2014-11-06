package com.qeevee.gq.host;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.qeevee.gq.R;
import com.qeevee.gq.base.GeoQuestApp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class PersonalGamesConnectionStrategy extends AbstractConnectionStrategy {

	static final String TAG = PersonalGamesConnectionStrategy.class
			.getCanonicalName();

	public PersonalGamesConnectionStrategy(int portalID) {
		this.portalID = portalID;
	}

	private static String userEmail;

	static public void setUserEmail(String email) {
		userEmail = email;
	}

	private static String userPassword;

	static public void setUserPassword(String password) {
		userPassword = password;
	}

	static {
		// read email and password from sharedPrefs when this class is loaded:
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(GeoQuestApp.getContext());
		userEmail = sharedPrefs.getString((String) GeoQuestApp.getContext()
				.getText(R.string.pref_server_email_key), null);
		userPassword = sharedPrefs.getString((String) GeoQuestApp.getContext()
				.getText(R.string.pref_server_password_key), null);
	}

	@Override
	public String getGamesJSONString() {
		try {
			String result = (new RetrieveJSONforPersonalGames()).execute()
					.get();
			return result;
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	private class RetrieveJSONforPersonalGames extends
			AsyncTask<Void, Integer, String> {
		protected String doInBackground(Void... unusedParams) {
			if (userEmail == null || userPassword == null)
				return null;

			String responseString = null;

			// Input: TODO get stored user credentials from preferences
			List<String> cookieStrings = null;
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);

			URL url;
			try {
				url = new URL(Host.getURLForLoginOnPortal(portalID));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", userEmail));
				params.add(new BasicNameValuePair("password", userPassword));

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();

				conn.connect();
				cookieStrings = conn.getHeaderFields().get("Set-Cookie");
				URI uri = url.toURI();
				for (String string : cookieStrings) {
					for (HttpCookie cookie : HttpCookie.parse(string)) {
						cookieManager.getCookieStore().add(uri, cookie);
					}
				}

				url = new URL(Host.getURLForPersonalGamesOnPortal(portalID));
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				InputStream in = new BufferedInputStream(conn.getInputStream());
				byte[] contents = new byte[1024];

				int bytesRead = 0;
				StringBuffer readBuffer = new StringBuffer();
				while ((bytesRead = in.read(contents)) != -1) {
					readBuffer.append(new String(contents, 0, bytesRead));
				}
				in.close();
				responseString = readBuffer.toString();

				conn.disconnect();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return responseString;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Long result) {
			// showDialog("Downloaded " + result + " bytes");
		}

		private String getQuery(List<NameValuePair> params)
				throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			boolean first = true;

			for (NameValuePair pair : params) {
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
			}

			return result.toString();
		}

	}

}
