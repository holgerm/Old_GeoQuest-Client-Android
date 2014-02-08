package edu.bonn.mobilegaming.geoquest.mission;

import static com.qeevee.util.StringTools.trim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.dom4j.Element;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public class PhotoPuzzle extends MissionActivity {

	/** Hintergrundbild und Views der Zellen**/
	private ImageView photopuzzleImage;
	private ImageView[] imageViewArray = new ImageView[15];

	private ImageView imageView, blackboxView1, blackboxView2;
	private boolean endByTouch = true;
	private Button button;
	private CharSequence replyTextOnCorrect;
	private CharSequence replyTextOnWrong;
	private CharSequence questionText;
	public List<String> answers = new ArrayList<String>();
	private OnClickListener buttonOnClickListener;
	private String storeVariable;
	private int numberOfAnswers = 0;

	/** unsichtbare Zellen **/
	private ArrayList<Integer> viewableSquares = new ArrayList<Integer>();

	/** countdowntimer for the start countdown */
	private MyCountDownTimer myCountDownTimer;
	private long duration, countdownInterval;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_default_photopuzzle);

		imageView = (ImageView) findViewById(R.id.photopuzzle);
		blackboxView1 = (ImageView) findViewById(R.id.blackbox1);
		blackboxView2 = (ImageView) findViewById(R.id.blackbox2);
		
		long durationLong = 5000;
		myCountDownTimer = new MyCountDownTimer(durationLong, durationLong);
		setImage();

		/** Views der Zellen speichern **/
		for (int i = 0; i < 15; i++) {
			imageViewArray[i] = getImageView(i);
		}

		photopuzzleImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showQuestion();
			}

		if (!endByTouch)

		
		/** Countdowntimer starten **/
		duration = 51000;
		countdownInterval = 3000;
		myCountDownTimer = new MyCountDownTimer(duration, countdownInterval);
		myCountDownTimer.start();

	}

	private void showQuestion() {
		myCountDownTimer.cancel();

		photopuzzleImage.setVisibility(View.INVISIBLE);
		
		textView.setVisibility(View.VISIBLE);
		textView.setText(questionText);
		answerText.setText("");
		answerText.setVisibility(View.VISIBLE);
		button.setText(R.string.button_text_accept);
		button.setOnClickListener(buttonOnClickListener);
		button.setVisibility(View.VISIBLE);
	}

	private void initInput() {
		button = (Button) findViewById(R.id.acceptBT);
		button.setEnabled(false);
		buttonOnClickListener = new OnClickListener() {
			public void onClick(View v) {
				handleAnswer(evaluateAnswer());

			}

		};
		button.setVisibility(View.INVISIBLE);

		answerText = (EditText) findViewById(R.id.editText);
		answerText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				button.setEnabled(count > 0);
			}

		});
		answerText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							evaluateAnswer();
						}
						return false;
					}
				});
		answerText.setText("");
		answerText.setVisibility(View.INVISIBLE);

		questionText = getMissionAttribute("textQuestion",
				XMLUtilities.NECESSARY_ATTRIBUTE);
		replyTextOnCorrect = getMissionAttribute("replyOnCorrect",
				R.string.question_reply_correct_default);
		replyTextOnWrong = getMissionAttribute("replyOnWrong",
				R.string.question_reply_wrong_default);
		storeVariable = (String) getMissionAttribute(
				"storeAcceptedAnswerInVariable",
				XMLUtilities.OPTIONAL_ATTRIBUTE);

		@SuppressWarnings("unchecked")
		List<Element> xmlAnswers = ((Element) mission.xmlMissionNode)
				.selectNodes("answers/answer");
		for (Iterator<Element> j = xmlAnswers.iterator(); j.hasNext();) {
			Element xmlAnswer = j.next();
			answers.add(trim(xmlAnswer.getText()));
		}
		textView.setVisibility(View.INVISIBLE);

		textView_message.setText(replyTextOnWrong);
		textView_message.setVisibility(View.INVISIBLE);

	}

	private void handleAnswer(boolean evaluateAnswer) {
		numberOfAnswers++;
		if (evaluateAnswer) {
			GeoQuestApp.showMessage(replyTextOnCorrect);
			finish(Globals.STATUS_SUCCEEDED);
			invokeOnSuccessEvents();

		} else {
			if (numberOfAnswers == 3) {
				finish(Globals.STATUS_FAIL);
				invokeOnFailEvents();
			}

			else {
				GeoQuestApp.showMessage(replyTextOnWrong);
				hideQuestion();
				
			}
		}

	}

	private void hideQuestion() {

		textView.setVisibility(View.INVISIBLE);
		answerText.setVisibility(View.INVISIBLE);
		button.setVisibility(View.INVISIBLE);
		photopuzzleImage.setVisibility(View.VISIBLE);
		
		duration = 51000 - (viewableSquares.size() * countdownInterval);
			myCountDownTimer.start();

	}

	private boolean evaluateAnswer() {
		String givenAnswer = answerText.getText().toString();
		boolean found = false;
		for (Iterator<String> iterator = answers.iterator(); iterator.hasNext();) {
			String answer = (String) iterator.next();
			found |= givenAnswer.matches(answer);
		}
		return found;

	}

	private void setImage() {
		String imgsrc = (String) XMLUtilities.getAttribute("puzzleImage",
				XMLUtilities.NECESSARY_ATTRIBUTE, mission.xmlMissionNode);
		if (imgsrc != null)
					.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
							.getRoundedCornerBitmap(
									BitmapUtil.loadBitmap(imgsrc), 15)));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			return;
		}
		setImage();
		if (!endByTouch)
			myCountDownTimer.start();
	}

	/**
	 * count down timer for the start screen
	 * 
	 * @author Krischan Udelhoven
	 * @author Folker Hoffmann
	 */

	public class MyCountDownTimer extends CountDownTimer {
		long counter = 0;
		boolean firstCall = true;

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			hidePicture();

		}

		private void hidePicture() {
			

			int height = getWindowManager().getDefaultDisplay().getHeight() / 5;
			int width = getWindowManager().getDefaultDisplay().getWidth() / 3;

			Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
					Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();

			paint.setColor(Color.BLACK);

			blackboxView1.setImageBitmap(bitmap);
			blackboxView2.setImageBitmap(bitmap);

			canvas.drawRect(0, 0, (float) width, (float) height, paint);

		}

		@Override
		public void onFinish() {
			finish(Globals.STATUS_SUCCEEDED);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			counter += 1;
			if (counter % 300 == 0) {
				revealPicture();
			}
		}

		private void revealPicture() {

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
