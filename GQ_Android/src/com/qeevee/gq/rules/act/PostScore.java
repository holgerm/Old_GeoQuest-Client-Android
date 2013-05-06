package com.qeevee.gq.rules.act;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;

/**
 * @author muegge
 * 
 *         Posts the current score to a php script which is accessible under the
 *         given URL. It uses the score as defined in {@link Score}. The
 *         variable <code>username</code> is used as name of the player.
 * 
 *         Each postscore action must have a parameter <code>script_url</code>
 *         that must contain the basic part of the URL where the script is
 *         located (ending with "/").
 * 
 */
public class PostScore extends Action {

	private static String TAG = PostScore.class.getCanonicalName();

	private static final String USER_NAME_VARIABLE = "user_name";
	private static final String USER_ID_VARIABLE = "user_id";
	private static final String USER_SCORE_RANK_VARIABLE = "user_score_rank";
	private static final String SCORE_POST_SCRIPT_PATH = "easyhighscore/easyhighscore-post.php";

	private Context ctx = GeoQuestApp.getContext();

	@Override
	protected boolean checkInitialization() {
		boolean initOK = true;
		initOK &= params.containsKey("script_url");
		return initOK;
	}

	@Override
	public void execute() {
		Variables.setValueIfUndefined(USER_NAME_VARIABLE,
				ctx.getText(R.string.varDefaultUserName));
		Variables.setValueIfUndefined(USER_ID_VARIABLE,
				ctx.getText(R.string.varDefaultUserID));
		Variables.setValueIfUndefined(Score.SCORE_VARIABLE, 0);
		postScore();
	}

	private void postScore() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(params.get("script_url")
				+ SCORE_POST_SCRIPT_PATH);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("ehs_score", Variables
					.getValue(Score.SCORE_VARIABLE).toString()));
			nameValuePairs.add(new BasicNameValuePair("ehs_name", Variables
					.getValue(USER_NAME_VARIABLE).toString()));
			nameValuePairs.add(new BasicNameValuePair("ehs_old_id", Variables
					.getValue(USER_ID_VARIABLE).toString()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse httpResponse = httpclient.execute(httppost);

			// Analyze Response:
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				StringBuilder response = new StringBuilder();
				HttpEntity messageEntity = httpResponse.getEntity();
				InputStream is = messageEntity.getContent();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line;
				while ((line = br.readLine()) != null) {
					response.append(line);
				}
				String[] data = TextUtils.split(response.toString(), ";");
				Variables.setValue(USER_ID_VARIABLE, data[0]);
				Variables.setValue(USER_SCORE_RANK_VARIABLE, data[0]);
			} else {
				Log.e(TAG, "HTTP POST status code is not 200");
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
