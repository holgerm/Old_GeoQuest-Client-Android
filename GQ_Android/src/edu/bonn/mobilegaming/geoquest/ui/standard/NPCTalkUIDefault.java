package edu.bonn.mobilegaming.geoquest.ui.standard;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Util;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk.DialogItem;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.GeoQuestUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;

public class NPCTalkUIDefault extends NPCTalkUI {

	private ImageView charImage;
	private Button button;
	private TextView dialogText;
	private ScrollView scrollView;
	private DialogItem currentDialogItem = null;
	private WordTicker ticker = null;
	private static final long milliseconds_per_part = 100;

	private int state = 0;
	private static final int STATE_NEXT_DIALOG_ITEM = 1;
	private static final int STATE_END = 2;
	private static final String TAG = NPCTalkUIDefault.class.getCanonicalName();

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

	private CharSequence mode;

	/**
	 * @see GeoQuestUI#GeoQuestUI(android.app.Activity)
	 * @param activity
	 */
	public NPCTalkUIDefault(NPCTalk activity) {
		super(activity);
		setImage(getNPCTalk().getMissionAttribute("image"));
		this.mode = XMLUtilities.getStringAttribute("mode",
				R.string.npctalk_mode_default, getMissionXML());
		init();
	}

	@Override
	public View createContentView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.m_default_npctalk, null);
		outerView = (View) contentView.findViewById(R.id.outerview);
		charImage = (ImageView) contentView.findViewById(R.id.npcimage);
		button = (Button) contentView.findViewById(R.id.proceedButton);
		dialogText = (TextView) contentView.findViewById(R.id.npctext);
		dialogText.setTextSize(getTextsize());
		dialogText.setTextColor(getTextColor());
		scrollView = (ScrollView) contentView
				.findViewById(R.id.npc_scroll_view);
		return contentView;
	}

	private boolean setImage(CharSequence pathToImageFile) {
		if (pathToImageFile == null) {
			charImage.setVisibility(View.GONE);
			return false;
		}
		try {
			int margin = GeoQuestApp.getContext().getResources()
					.getDimensionPixelSize(R.dimen.margin);
			Bitmap bitmap = BitmapUtil.loadBitmap(pathToImageFile.toString(),
					Util.getDisplayWidth() - (2 * margin), 0, true);
			if (bitmap != null) {
				charImage.setImageBitmap(bitmap);
			} else {
				Log.e(TAG, "Bitmap file invalid: " + pathToImageFile.toString());
			}
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
