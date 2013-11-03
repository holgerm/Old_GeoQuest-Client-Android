package edu.bonn.mobilegaming.geoquest.ui.standard;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.ZoomImageView;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk.DialogItem;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;

public class NPCTalkUIDefault extends NPCTalkUI {

	private ZoomImageView charImage;
	private Button button;
	private TextView dialogText;
	private ScrollView scrollView;
	private DialogItem currentDialogItem = null;
	private WordTicker ticker = null;
	private static final long milliseconds_per_part = 100;

	private int state = 0;
	private static final int STATE_NEXT_DIALOG_ITEM = 1;
	private static final int STATE_END = 2;

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

	public NPCTalkUIDefault(NPCTalk activity) {
		super(activity);
		setBackground();
		setImage(getNPCTalk().getMissionAttribute("image"));
		setNextDialogButtonText(getNPCTalk().getMissionAttribute(
				"nextdialogbuttontext", R.string.button_text_next));
		this.mode = XMLUtilities.getAttribute("mode",
				R.string.npctalk_mode_default, getMissionXML());
	}

	private void setNextDialogButtonText(
			CharSequence nextDialogButtonTextDefault) {
		this.nextDialogButtonTextDefault = nextDialogButtonTextDefault;
	}

	@Override
	public View createView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.m_default_npctalk, null);
		outerView = (View) view.findViewById(R.id.outerview);
		charImage = (ZoomImageView) view.findViewById(R.id.npcimage);
		button = (Button) view.findViewById(R.id.proceedButton);
		dialogText = (TextView) view.findViewById(R.id.npctext);
		dialogText.setTextColor(Color.BLACK);
		scrollView = (ScrollView) view.findViewById(R.id.npc_scroll_view);
		return view;
	}

	private boolean setImage(CharSequence pathToImageFile) {
		if (pathToImageFile == null) {
			charImage.setVisibility(View.GONE);
			return false;
		}
		try {
			charImage.setRelativePathToImageBitmap(pathToImageFile.toString());
			return true;
		} catch (IllegalArgumentException iae) {
			charImage.setVisibility(View.GONE);
			return false;
		}
	}

	@Override
	public void showNextDialogItem() {
		if (getNPCTalk().hasMoreDialogItems()) {
			currentDialogItem = getNPCTalk().getNextDialogItem();
			displaySpeaker();
			if (currentDialogItem.getAudioFilePath() != null)
				GeoQuestApp.playAudio(currentDialogItem.getAudioFilePath(),
						currentDialogItem.blocking);
			initDialogItemPresenter();
		}
		refreshButton();
	}

	private void initDialogItemPresenter() {
		if (this.mode.equals("chunk")) {
			// display formatted text as a complete chunk:
			dialogText.append(Html.fromHtml(currentDialogItem.getText()));
			dialogText.append("\n");
			scrollView.fullScroll(View.FOCUS_DOWN);
		}
		if (this.mode.equals("wordticker")) {
			// show dialog item text word by word via ticker
			ticker = new WordTicker();
			ticker.start();
		}
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
			releaseInteraction(NPCTalkUIDefault.this.ticker);
			NPCTalkUIDefault.this.ticker = null;
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
