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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qeevee.gq.rules.act.Score;
import com.qeevee.gq.rules.expr.Expressions;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

/**
 * This class implements a photo behind 15 black boxes one of which will be
 * uncovered every 3 seconds until the user touches the screen or the time is
 * out. If the user touches the screen, he gets the possibility to give an
 * answer. He can try three times. If he guesses correctly the next mission will
 * start, if the guess was wrong after three times the mission fails. 
 * The level of visible boxes can be checked with the photopuzzlecounter variable,
 * possible levels are 0,1 and 2.
 * 
 * @author Valeriya Ilyina
 * @author Marieke Kunze
 */

public class PhotoPuzzle extends InteractiveMission {

	private ImageView photopuzzleImage;
	private TextView[] textViewArray = new TextView[15];

	private TextView textView, textView_message;
	private EditText answerText;
	private Button button;
	private CharSequence replyTextOnWrong;
	boolean showReplyOnWrong = true;
	private CharSequence questionText;
	public List<String> answers = new ArrayList<String>();
	private OnClickListener buttonOnClickListener;
	private String storeVariable;
	private int numberOfAnswers = 0;
	static final String PHOTOPUZZLECOUNTER = "photopuzzlecounter";
	
	private ArrayList<Integer> viewableSquares = new ArrayList<Integer>();

	private MyCountDownTimer myCountDownTimer;
	private long duration, countdownInterval;

	/**
	 * This method initializes the layout of the photo puzzle.
	 * 
	 * @param savedInstanceState
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_default_photopuzzle);

		photopuzzleImage = (ImageView) findViewById(R.id.photopuzzleimage);

		textView = (TextView) findViewById(R.id.textView);
		textView_message = (TextView) findViewById(R.id.textView_message);
		initInput();
		setImage();

		for (int i = 0; i < 15; i++) {
			textViewArray[i] = getTextView(i);
			textViewArray[i].setText("");
			
		}

		photopuzzleImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showQuestion();
			}

		});

		duration = 51000;
		countdownInterval = 3000;
		myCountDownTimer = new MyCountDownTimer(duration, countdownInterval);
		myCountDownTimer.start();
		
		Variables.setValue(PHOTOPUZZLECOUNTER, 0);

	}

	/**
	 * Shows the question if the user has touched the screen
	 * 
	 */

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

	/**
	 * Initializes the input button and input box
	 * 
	 */

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
		
		storeVariable = (String) getMissionAttribute(
				"storeAcceptedAnswerInVariable",
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		
		if((String) getMissionAttribute(
				"replyOnWrong",
				XMLUtilities.OPTIONAL_ATTRIBUTE) != null){
				replyTextOnWrong = (String) getMissionAttribute(
				"replyOnWrong",
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		} else {
			showReplyOnWrong = false;
		}
		
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

	/**
	 * Evaluates the answer given by the user and finishes the mission if he has
	 * guessed correct
	 * 
	 * @param evaluateAnswer
	 */
	private void handleAnswer(boolean evaluateAnswer) {
		numberOfAnswers++;
		if (evaluateAnswer) {
			setPhotopuzzleCounter();
			finish(Globals.STATUS_SUCCEEDED);
			invokeOnSuccessEvents();

		} else {
			if (numberOfAnswers == 3) {
				finish(Globals.STATUS_FAIL);
				invokeOnFailEvents();
			}

			else {
				if(showReplyOnWrong)
					GeoQuestApp.showMessage(replyTextOnWrong);
				hideQuestion();

			}
		}

	}

	private void setPhotopuzzleCounter() {
		int counter;
		if(viewableSquares.size()<=5)
			counter = 0;
		else if(viewableSquares.size()<=10)
			counter = 1;
		else 
			counter = 2;
		Variables.setValue(PHOTOPUZZLECOUNTER,
				counter);		
	}

	/**
	 * Hides the question button and text
	 * 
	 */
	private void hideQuestion() {

		textView.setVisibility(View.INVISIBLE);
		answerText.setVisibility(View.INVISIBLE);
		button.setVisibility(View.INVISIBLE);
		photopuzzleImage.setVisibility(View.VISIBLE);

		duration = 51000 - (viewableSquares.size() * countdownInterval);
		myCountDownTimer.start();

	}

	/**
	 * Evaluates the answer given by user and returns true if correct
	 */

	private boolean evaluateAnswer() {
		String givenAnswer = answerText.getText().toString();
		boolean found = false;
		for (Iterator<String> iterator = answers.iterator(); iterator.hasNext();) {
			String answer = (String) iterator.next();
			found |= givenAnswer.matches(answer);
		}
		return found;

	}

	/**
	 * Sets the puzzle Image
	 */
	private void setImage() {
		String imgsrc = (String) XMLUtilities.getStringAttribute("puzzleImage",
				XMLUtilities.NECESSARY_ATTRIBUTE, mission.xmlMissionNode);
		if (imgsrc != null)
			photopuzzleImage.setBackgroundDrawable(new BitmapDrawable(
					BitmapUtil.getRoundedCornerBitmap(
							BitmapUtil.loadBitmap(imgsrc), 15)));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			return;
		}

	}

	/**
	 * Initializes a countdowntimer that reveals a box every 3 seconds If the
	 * time runs out, the mission fails
	 */
	public class MyCountDownTimer extends CountDownTimer {
		int counter = 0;
		boolean firstCall = true;

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		@Override
		public void onFinish() {
			finish(Globals.STATUS_FAIL);
			invokeOnFailEvents();
		}

		@Override
		public void onTick(long millisUntilFinished) {

			Random rand = new Random();
			int randomInt = 0;
			TextView view;

			if (viewableSquares.size() == textViewArray.length) {
				onFinish();
				return;
			}

			if (!firstCall) {
				do {
					randomInt = rand.nextInt(textViewArray.length);
					view = getTextView(randomInt);
				} while (viewableSquares.contains(randomInt));

				view.setBackgroundColor(Color.TRANSPARENT);
				viewableSquares.add(randomInt);
			}
			firstCall = false;
		}

	}

	public void onBlockingStateUpdated(boolean blocking) {

	}

	public MissionOrToolUI getUI() {
		return null;
	}

	private TextView getTextView(int i) {
		switch (i) {
		case 0:
			return (TextView) findViewById(R.id.blackbox1);
		case 1:
			return (TextView) findViewById(R.id.blackbox2);
		case 2:
			return (TextView) findViewById(R.id.blackbox3);
		case 3:
			return (TextView) findViewById(R.id.blackbox4);
		case 4:
			return (TextView) findViewById(R.id.blackbox5);
		case 5:
			return (TextView) findViewById(R.id.blackbox6);
		case 6:
			return (TextView) findViewById(R.id.blackbox7);
		case 7:
			return (TextView) findViewById(R.id.blackbox8);
		case 8:
			return (TextView) findViewById(R.id.blackbox9);
		case 9:
			return (TextView) findViewById(R.id.blackbox10);
		case 10:
			return (TextView) findViewById(R.id.blackbox11);
		case 11:
			return (TextView) findViewById(R.id.blackbox12);
		case 12:
			return (TextView) findViewById(R.id.blackbox13);
		case 13:
			return (TextView) findViewById(R.id.blackbox14);
		case 14:
			return (TextView) findViewById(R.id.blackbox15);
		default:
			return null;
		}

	}

}
