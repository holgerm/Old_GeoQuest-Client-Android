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

import android.os.AsyncTask;
import android.util.Log;

public class PersonalGamesConnectionStrategy extends AbstractConnectionStrategy {

	static final String TAG = PersonalGamesConnectionStrategy.class
			.getCanonicalName();

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
			String responseString = null;

			// Input:
			String email = "mail@holgermuegge.de";
			String password = "!mactop!";
			List<String> cookieStrings = null;
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);

			URL url;
			try {
				url = new URL("http://geo.quest-mill.com:9000/61/login");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("password", password));

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

				url = new URL("http://geo.quest-mill.com/json/61/privategames");
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
