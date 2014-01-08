package edu.bonn.mobilegaming.geoquest.ui.web;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qeevee.gq.res.ResourceManager;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk.DialogItem;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.GeoQuestUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;

public class NPCTalkUIWeb extends NPCTalkUI {

	private Button button;
	private TextView dialogText;
	private ScrollView scrollView;
	private DialogItem currentDialogItem = null;
	private WordTicker ticker = null;
	private static final long milliseconds_per_part = 100;

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
	private CharSequence mode;
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
		_jsHandler = new JsHandler(getNPCTalk(), mainView);
		mainView.addJavascriptInterface(_jsHandler, "JsHandler");

		String htmlFilePath = "file:///"
				+ ResourceManager.getResourcePath("ui/npctalk.html");
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
		// if (getNPCTalk().hasMoreDialogItems()) {
		// currentDialogItem = getNPCTalk().getNextDialogItem();
		// displaySpeaker();
		// if (currentDialogItem.getAudioFilePath() != null)
		// GeoQuestApp.playAudio(currentDialogItem.getAudioFilePath(),
		// currentDialogItem.blocking);
		// }
		// refreshButton();
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

	/**
	 * Display the text of the given DialogItem word by word.
	 */
	public class WordTicker extends CountDownTimer implements
			InteractionBlocker {

		private WordTicker() {
			super(milliseconds_per_part
					* (currentDialogItem.getNumberOfTextTokens() + 1),
					milliseconds_per_part);
			// block interaction on the NPCTalk using this Timer as Blocker
			// monitor:
			blockInteraction(this);
			refreshButton();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			CharSequence next = currentDialogItem.getNextTextToken();
			if (next != null)
				dialogText.append(next);
			if (currentDialogItem.hasNextPart())
				dialogText.append(" ");
			else
				dialogText.append("\n");
			scrollView.fullScroll(View.FOCUS_DOWN);
		}

		@Override
		public void onFinish() {
			// Zur Sicherheit, da manchmal Wörter verschluckt werden (nicht
			// ausreichend genauer timer!)
			CharSequence next = currentDialogItem.getNextTextToken();
			while (next != null) {
				dialogText.append(next);
				next = currentDialogItem.getNextTextToken();
				if (next != null)
					dialogText.append(" ");
				else
					dialogText.append("\n");
				scrollView.fullScroll(View.FOCUS_DOWN);
			}
			scrollView.fullScroll(View.FOCUS_DOWN);
			getNPCTalk().hasShownDialogItem(currentDialogItem);
			releaseInteraction(NPCTalkUIWeb.this.ticker);
			NPCTalkUIWeb.this.ticker = null;
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
}
