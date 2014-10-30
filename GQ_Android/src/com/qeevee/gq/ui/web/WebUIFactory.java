package com.qeevee.gq.ui.web;

import com.qeevee.gq.mission.AudioRecord;
import com.qeevee.gq.mission.ImageCapture;
import com.qeevee.gq.mission.MapOSM;
import com.qeevee.gq.mission.MultipleChoiceQuestion;
import com.qeevee.gq.mission.NFCMission;
import com.qeevee.gq.mission.NFCScanMission;
import com.qeevee.gq.mission.NFCTagReadingProduct;
import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.mission.QRTagReading;
import com.qeevee.gq.mission.StartAndExitScreen;
import com.qeevee.gq.mission.TextQuestion;
import com.qeevee.gq.mission.VideoPlay;
import com.qeevee.gq.mission.WebPage;
import com.qeevee.gq.ui.UIFactory;
import com.qeevee.gq.ui.abstrakt.AudioRecordUI;
import com.qeevee.gq.ui.abstrakt.ImageCaptureUI;
import com.qeevee.gq.ui.abstrakt.MapOSM_UI;
import com.qeevee.gq.ui.abstrakt.MultipleChoiceQuestionUI;
import com.qeevee.gq.ui.abstrakt.NFCMissionUI;
import com.qeevee.gq.ui.abstrakt.NFCScanMissionUI;
import com.qeevee.gq.ui.abstrakt.NFCTagReadingProductUI;
import com.qeevee.gq.ui.abstrakt.NPCTalkUI;
import com.qeevee.gq.ui.abstrakt.QRTagReadingUI;
import com.qeevee.gq.ui.abstrakt.StartAndExitScreenUI;
import com.qeevee.gq.ui.abstrakt.TextQuestionUI;
import com.qeevee.gq.ui.abstrakt.VideoPlayUI;
import com.qeevee.gq.ui.abstrakt.WebPageUI;
import com.qeevee.gq.ui.standard.AudioRecordUIDefault;
import com.qeevee.gq.ui.standard.ImageCaptureUIDefault;
import com.qeevee.gq.ui.standard.MapOSM_UIDefault;
import com.qeevee.gq.ui.standard.NFCMissionUIDefault;
import com.qeevee.gq.ui.standard.NFCScanMissionUIDefault;
import com.qeevee.gq.ui.standard.NFCTagReadingProductUIDefault;
import com.qeevee.gq.ui.standard.QRTagReadingUIDefault;
import com.qeevee.gq.ui.standard.StartAndExitScreenUIDefault;
import com.qeevee.gq.ui.standard.TextQuestionDefault;
import com.qeevee.gq.ui.standard.VideoPlayUIDefault;
import com.qeevee.gq.ui.standard.WebPageUIDefault;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;

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

	public TextQuestionUI createUI(TextQuestion activity) {
		return new TextQuestionDefault(activity);
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
