package edu.bonn.mobilegaming.geoquest.mission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.jsinterface.NPCTalkJSInterface;
import edu.bonn.mobilegaming.geoquest.jsinterface.QuestionAndAnswerJSInterface;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public class WebTech extends MissionActivity {

	private String adrGenericMissionsJS;
	private String adrMissionJS;

	private String missionType;

	private WebView mWebView;

	private File imgDir = GeoQuestApp.getGameRessourceFile("/drawable");
	private String layoutDir = GeoQuestApp.getGameRessourceFile("/layout")
			.getAbsolutePath();
	private final String LOG_TAG = "WebTech";

	// TODO -- Sabine -- WebTech WebViewDemo Ausgabe aus LogCat entfernen.
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.webtech);
		mWebView = (WebView) findViewById(R.id.webview);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);

		mWebView.setWebChromeClient(new MyWebChromeClient());

		missionType = mission.xmlMissionNode.attributeValue("type");
		setJSFInterface();
		setJSFiles();

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadInternalJSFiles();
				loadCustomerJSFile();
			}
		});

		loadHTMLPage();
	}

	/**
	 * Load internal JS-Files. - generic JS-File (all_missions.js) - JS-File for
	 * active mission (npc_talk.js, ...)
	 */
	private void loadInternalJSFiles() {
		mWebView.loadUrl("javascript: (function() { "
				+ "var script=document.createElement('script');"
				+ "script.type='text/javascript';script.src='"
				+ adrGenericMissionsJS
				+ "';"
				+ "document.getElementsByTagName('head').item(0).appendChild(script);"
				+ "})()");
		Log.d("WebTech", "loaded javascript file: " + adrGenericMissionsJS);

		mWebView.loadUrl("javascript: (function() { "
				+ "var script=document.createElement('script');"
				+ "script.type='text/javascript';script.src='"
				+ adrMissionJS
				+ "';"
				+ "document.getElementsByTagName('head').item(0).appendChild(script);"
				+ "})()");
		Log.d("WebTech", "loaded javascript file: " + adrMissionJS);
	}

	/**
	 * Load JS-File, that contains functions defined by the customer
	 * (customer.js). If the file doesn't exist, a remark is shown by LogCat.
	 */
	private void loadCustomerJSFile() {
		File customerJS = new File(layoutDir, "/customer.js");
		if (customerJS.exists()) {
			mWebView.loadUrl("javascript: (function() { "
					+ "var script=document.createElement('script');"
					+ "script.type='text/javascript';script.src='"
					+ "file://"
					+ customerJS.getAbsolutePath()
					+ "';"
					+ "document.getElementsByTagName('head').item(0).appendChild(script);"
					+ "})()");
			Log.d("WebTech", "loaded javascript file: " + "file://"
					+ customerJS.getAbsolutePath());
		} else {
			Log.d("WebTech", "javascript file doesn't exist: " + "customer.js");
		}
	}

	/**
	 * Loads the html page according to the id or type of the mission.
	 * 
	 * All html-Files must be in the "layout" folder.
	 * 
	 * For a global layout the name of the html-file must have the form
	 * "<mission-type>.html". For a layout which is only valid for a specific
	 * mission, the filename must have the form "<mission-id>.html".
	 * 
	 * Example: - <mission-type>.html -> NPCTalk.html - <mission-id>.html ->
	 * myMissionId.html
	 */
	private void loadHTMLPage() {

		String missionId = mission.xmlMissionNode.attributeValue("id");
		File htmlFile = new File(layoutDir, "/" + missionId + ".html");

		if (htmlFile.exists()) {
			mWebView.loadUrl("file://" + htmlFile.getAbsolutePath());
			Log.d("WebTech",
					"Loaded html page: " + "file://"
							+ htmlFile.getAbsolutePath());
		} else {
			htmlFile = new File(layoutDir, "/" + missionType + ".html");
			if (htmlFile.exists()) {
				mWebView.loadUrl("file://" + htmlFile.getAbsolutePath());
				Log.d("WebTech",
						"Loaded html page: " + "file://"
								+ htmlFile.getAbsolutePath());
			} else {
				Log.e(LOG_TAG, "There is no hmtl file for mission " + missionId);
			}
		}

	}

	/**
	 * Set needed paths to JS Files according to the type of the mission. All
	 * files are saved in the directory .../repositories/global/.
	 * 
	 * If this folder doesn't exist or doesn't contain the JS Files, it will be
	 * created and the files will be downloaded from the sever.
	 */
	private void setJSFiles() {

		// TODO -- Sabine -- Download implementieren

		File repDir = GeoQuestApp.getRunningGameDir().getParentFile()
				.getParentFile();
		File jsDir = new File(repDir.getAbsolutePath() + "/global/jsFiles");
		File allMissionsJs = new File(jsDir.getAbsolutePath(),"/all_missions.js");
		File missionJs = null;

		if (!jsDir.exists()) {
			jsDir.mkdirs();
			if (!allMissionsJs.exists()) {
				copyJsFile(allMissionsJs, R.raw.all_missions);
			}

		}

		adrGenericMissionsJS = "file://" + allMissionsJs.getAbsolutePath();

		// NPCTalk
		if (missionType.equals("NPCTalk")) {
			missionJs = new File(jsDir.getAbsolutePath(), "/npc_talk.js");
			if (!missionJs.exists()) {
				copyJsFile(missionJs, R.raw.npc_talk);
			}
		} else {
			// QuestionAndAnswer
			if (missionType.equals("QuestionAndAnswer")) {
				missionJs = new File(jsDir.getAbsolutePath(), "/question_and_answer.js");
				if (!missionJs.exists()) {
					copyJsFile(missionJs, R.raw.question_and_answer);
				}
			}
		}
		if (missionJs != null) {
			adrMissionJS = "file://" + missionJs.getAbsolutePath();
		}
		
		// TODO -- Sabine -- WebTech andere Missionen einf�gen
	}

	/**
	 * Set the JSInterface according to the type of the mission.
	 */
	private boolean setJSFInterface() {

		if (missionType.equals("NPCTalk")) {
			mWebView.addJavascriptInterface(new NPCTalkJSInterface(this,
					mission, imgDir), "npcTalk_JSI");
			return true;
		}
		if (missionType.equals("QuestionAndAnswer")) {
			mWebView.addJavascriptInterface(new QuestionAndAnswerJSInterface(this,
					mission), "questionAndAnswer_JSI");
			return true;
		}	
		// TODO -- Sabine -- WebTech restlichen Missionstypen eintragen
		return false;
	}

	private void copyJsFile(File jsFile, int rId) {
		InputStream ins = getResources().openRawResource(rId);

		try {
			int size = ins.available();
			byte[] buffer = new byte[size];
			ins.read(buffer);
			ins.close();

			if (jsFile.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(jsFile);
				fos.write(buffer);
				fos.close();
			}
			// TODO -- Sabine -- WebTech Log Ausgabe, wenn Datei nicht erstellt
			// wird

		} catch (IOException e) {
			// TODO -- Sabine -- WebTech Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Closes mission and sets the status. It's used by JSInterfaces, when a
	 * mission is finished.
	 * 
	 * @param status
	 *            Status at the end of a mission.
	 */
	public void endWebMission(Double status) {
		Log.d(LOG_TAG, "End WebTechMission with status "
				+ status);
		finish(status);
	}

	/**
	 * Is used by JSInterfaces to read Strings out of
	 * edu.bonn.mobilegaming.geoquest.R.
	 * 
	 * @param resId
	 *            Id of a string resource.
	 * @return Value of a string resource.
	 */
	public String getResourceString(int resId) {
		return getText(resId).toString();
	}

	/**
	 * WebChromeClient is used to handle events that might be triggered by HTML
	 * or JavaScript and would affect a browser but (typically) not in the HTML
	 * rendering area.
	 * 
	 */
	final class MyWebChromeClient extends WebChromeClient {

		private static final String LOG_TAG = "WebChromeClient";

		/**
		 * Provides a hook for calling "alert" from JavaScript. Useful for
		 * debugging your JavaScript.
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			Log.d(LOG_TAG, message);
			result.confirm();
			return true;
		}
	}

	public void onBlockingStateUpdated(boolean blocking) {
		// TODO Auto-generated method stub
		
	}

	public MissionOrToolUI getUI() {
	    // TODO Auto-generated method stub
	    return null;
	}
}
