package edu.bonn.mobilegaming.geoquest.ui.web;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import edu.bonn.mobilegaming.geoquest.mission.AudioRecord;
import edu.bonn.mobilegaming.geoquest.mission.ExternalMission;
import edu.bonn.mobilegaming.geoquest.mission.ImageCapture;
import edu.bonn.mobilegaming.geoquest.mission.MultipleChoiceQuestion;
import edu.bonn.mobilegaming.geoquest.mission.NFCMission;
import edu.bonn.mobilegaming.geoquest.mission.NFCScanMission;
import edu.bonn.mobilegaming.geoquest.mission.NFCTagReadingProduct;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.MapOSM;
import edu.bonn.mobilegaming.geoquest.mission.QRTagReading;
import edu.bonn.mobilegaming.geoquest.mission.StartAndExitScreen;
import edu.bonn.mobilegaming.geoquest.mission.TextQuestion;
import edu.bonn.mobilegaming.geoquest.mission.VideoPlay;
import edu.bonn.mobilegaming.geoquest.mission.WebPage;
import edu.bonn.mobilegaming.geoquest.mission.WebTech;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.AudioRecordUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.ExternalMissionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.ImageCaptureUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MultipleChoiceQuestionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCMissionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCScanMissionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCTagReadingProductUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MapOSM_UI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.QRTagReadingUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.StartAndExitScreenUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.TextQuestionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.VideoPlayUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.WebPageUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.WebTechUI;
import edu.bonn.mobilegaming.geoquest.ui.standard.AudioRecordUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.ExternalMissionUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.ImageCaptureUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.NFCMissionUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.NFCScanMissionUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.NFCTagReadingProductUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.MapOSM_UIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.QRTagReadingUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.StartAndExitScreenUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.TextQuestionDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.VideoPlayUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.WebPageUIDefault;
import edu.bonn.mobilegaming.geoquest.ui.standard.WebTechUIDefault;

public class WebUIFactory extends UIFactory {

	public WebUIFactory() {
		super();
	}

	public NFCTagReadingProductUI createUI(NFCTagReadingProduct activity) {
		return new NFCTagReadingProductUIDefault(activity);
	}

	public NFCScanMissionUI createUI(NFCScanMission activity) {
		return new NFCScanMissionUIDefault(activity);
	}

	public NFCMissionUI createUI(NFCMission activity) {
		return new NFCMissionUIDefault(activity);
	}

	public NPCTalkUI createUI(NPCTalk activity) {
		return new NPCTalkUIWeb(activity);
	}

	public ImageCaptureUI createUI(ImageCapture activity) {
		return new ImageCaptureUIDefault(activity);
	}

	public ExternalMissionUI createUI(ExternalMission activity) {
		return new ExternalMissionUIDefault(activity);
	}

	public TextQuestionUI createUI(TextQuestion activity) {
		return new TextQuestionDefault(activity);
	}

	public WebTechUI createUI(WebTech activity) {
		return new WebTechUIDefault(activity);
	}

	public AudioRecordUI createUI(AudioRecord activity) {
		return new AudioRecordUIDefault(activity);
	}

	public VideoPlayUI createUI(VideoPlay activity) {
		return new VideoPlayUIDefault(activity);
	}

	public WebPageUI createUI(WebPage activity) {
		return new WebPageUIDefault(activity);
	}

	public StartAndExitScreenUI createUI(StartAndExitScreen activity) {
		return new StartAndExitScreenUIDefault(activity);
	}

	public QRTagReadingUI createUI(QRTagReading activity) {
		return new QRTagReadingUIDefault(activity);
	}

	@Override
	public MultipleChoiceQuestionUI createUI(MultipleChoiceQuestion activity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapOSM_UI createUI(MapOSM activity) {
		return new MapOSM_UIDefault(activity);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public static WebView optimizeWebView(WebView webView) {
		// Tell the WebView to enable javascript execution.
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setBackgroundColor(Color.parseColor("#808080"));

		// Set whether the DOM storage API is enabled.
		webView.getSettings().setDomStorageEnabled(true);

		// setBuiltInZoomControls = false, removes +/- controls on screen
		webView.getSettings().setBuiltInZoomControls(false);

		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setAllowFileAccess(true);

		webView.getSettings().setAppCacheMaxSize(1024 * 8);
		webView.getSettings().setAppCacheEnabled(true);

		webView.getSettings().setUseWideViewPort(false);
		webView.setWebChromeClient(new WebChromeClient());

		// these settings speed up page load into the webview
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.requestFocus(View.FOCUS_DOWN);

		return webView;
	}

}
