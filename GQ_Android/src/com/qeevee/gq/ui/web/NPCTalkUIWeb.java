package com.qeevee.gq.ui.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.qeevee.gq.mission.NPCTalk;
import com.qeevee.gq.mission.NPCTalk.DialogItem;
import com.qeevee.gq.res.ResourceManager;
import com.qeevee.gq.res.ResourceManager.ResourceType;
import com.qeevee.gq.ui.abstrakt.GeoQuestUI;
import com.qeevee.gq.ui.abstrakt.NPCTalkUI;
import com.qeevee.util.JSUtil;

import com.qeevee.gq.R;

public class NPCTalkUIWeb extends NPCTalkUI {

	private Button button;
	private TextView dialogText;
	private ScrollView scrollView;
	private DialogItem currentDialogItem = null;
	private int state = 0;
	private static final int STATE_NEXT_DIALOG_ITEM = 1;
	private static final int STATE_END = 2;
	private static final String TAG = NPCTalkUIWeb.class.getCanonicalName();

	private OnClickListener showNextDialogListener = new OnClickListener() {
		public void onClick(View v) {
			showNextDialogItem();
		}
	};

	private OnClickListener endMissionListener = new OnClickListener() {
		public void onClick(View v) {
			getNPCTalk().finishMission();
		}
	};

	private CharSequence nextDialogButtonTextDefault;
	private WebView mainView;
	private JsHandler _jsHandler;

	/**
	 * @see GeoQuestUI#GeoQuestUI(android.app.Activity)
	 * @param activity
	 */
	public NPCTalkUIWeb(NPCTalk activity) {
		super(activity);
	}

	@Override
	public View createContentView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.m_web_npctalk, null);
		outerView = (View) contentView.findViewById(R.id.outerview);
		mainView = (WebView) contentView.findViewById(R.id.webview);
		WebUIFactory.optimizeWebView(mainView);
		mainView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Toast.makeText(getNPCTalk(), "Page started", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Toast.makeText(getNPCTalk(), "Page started", Toast.LENGTH_SHORT)
						.show();
				init();
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				super.onReceivedSslError(view, handler, error);
				// Toast.makeText(TableContentsWithDisplay.this, "error "+error,
				// Toast.LENGTH_SHORT).show();

			}
		});
		_jsHandler = new JsHandler(getNPCTalk(), mainView);
		mainView.addJavascriptInterface(_jsHandler, "JsHandler");

		String htmlFilePath = "file:///"
				+ ResourceManager.getResourcePath("ui/npctalk.html",
						ResourceType.HTML);
		try {
			mainView.loadUrl(htmlFilePath);
		} catch (IllegalArgumentException iae) {
			Log.e(TAG, "UI for NPCTalk activity "
					+ this.getNPCTalk().getMission().id
					+ " does not find the basic html (" + htmlFilePath + ")");
		}

		return contentView;
	}

	@Override
	public void showNextDialogItem() {
		if (getNPCTalk().hasMoreDialogItems()) {
			currentDialogItem = getNPCTalk().getNextDialogItem();
			displayButtonText();
			// displaySpeaker();
			// if (currentDialogItem.getAudioFilePath() != null)
			// GeoQuestApp.playAudio(currentDialogItem.getAudioFilePath(),
			// currentDialogItem.blocking);
		}
		// refreshButton();
	}

	private void displayButtonText() {
		String buttonText = (String) currentDialogItem
				.getNextDialogButtonText();
		if (buttonText == null) {
			buttonText = (String) getNPCTalk().getNextDialogButtonTextDefault();
		}
		JSUtil.callJSFuntion("setProceedButtonText", "'" + buttonText + "'",
				getNPCTalk(), mainView);
	}

	private void displaySpeaker() {
		if (currentDialogItem.getSpeaker() == null)
			return;
		dialogText.append(Html.fromHtml("<b>" + currentDialogItem.getSpeaker()
				+ ": </b>"));
	}

	public CharSequence getNextDialogButtonTextDefault() {
		return nextDialogButtonTextDefault;
	}

	private void refreshButton() {
		if (getNPCTalk().hasMoreDialogItems()) {
			setButtonMode(STATE_NEXT_DIALOG_ITEM);
		} else {
			setButtonMode(STATE_END);
		}
	}

	private void setButtonMode(int newMode) {
		state = newMode;
		switch (state) {
		case STATE_NEXT_DIALOG_ITEM:
			button.setOnClickListener(showNextDialogListener);
			if (currentDialogItem.getNextDialogButtonText() != null)
				button.setText(currentDialogItem.getNextDialogButtonText());
			else
				button.setText(R.string.button_text_next);
			break;
		case STATE_END:
			button.setOnClickListener(endMissionListener);
			if (getNPCTalk().getMissionAttribute("endbuttontext") != null)
				button.setText(getNPCTalk()
						.getMissionAttribute("endbuttontext"));
			else if (currentDialogItem.getNextDialogButtonText() != null)
				button.setText(currentDialogItem.getNextDialogButtonText());
			else
				button.setText(R.string.button_text_proceed);
			break;
		}

	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		button.setEnabled(!isBlocking);
		scrollView.fullScroll(View.FOCUS_DOWN);
	}

	@Override
	public void finishMission() {
		if (state == STATE_END)
			button.performClick();
	}

	public void release() {
		contentView.destroyDrawingCache();
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).removeAllViews();
		}

		currentDialogItem = null;
		contentView = null;
	}
}
