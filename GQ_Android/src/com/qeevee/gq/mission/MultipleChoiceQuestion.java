package com.qeevee.gq.mission;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qeevee.gq.base.Globals;
import com.qeevee.gq.base.Variables;
import com.qeevee.gq.history.TextItem;
import com.qeevee.gq.history.TextType;
import com.qeevee.gq.mission.helpers.ChoiceListAdapter;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.util.StringTools;
import com.qeevee.gqtours.R;

/**
 * Simple multiple choice question and answer mission.
 * 
 * @author Holger Muegge
 */
public class MultipleChoiceQuestion extends Question {
	/** list view that holds all choices that are offered as answers */
	private ListView choiceList;
	/** text view for displaying text */
	private TextView mcTextView;
	private Button bottomButton;

	private int mode = 0; // UNDEFINED MODE AT START IS USED TO TRIGGER INIT!
	private static final int MODE_QUESTION = 1;
	private static final int MODE_REPLY_TO_CORRECT_ANSWER = 2;
	private static final int MODE_REPLY_TO_WRONG_ANSWER = 3;
	private static final int MODE_CHOICE = 4;

	private List<Answer> textAnswers = new ArrayList<Answer>();
	private List<Answer> imageAnswers = new ArrayList<Answer>();
	private Answer selectedAnswer;
	private String questionText;
	private OnClickListener proceed, restart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initQuestionAndAnswers();
		setMode(MODE_QUESTION);
	}

	/**
	 * Clears and (re-) populates the complete view.
	 * 
	 * @param newMode
	 */
	private void setMode(int newMode) {
		if (mode == newMode)
			return;
		// real change in mode:
		mode = newMode;
		switch (mode) {
		case MODE_QUESTION:
			setBackgroundQuestion();
			setUpQuestionView();
			break;
		case MODE_REPLY_TO_CORRECT_ANSWER:
			if (isOnSuccessRulesLeaveMission()) {
				invokeOnSuccessEvents();
				proceedAfterFeedback();
			} else {
				setBackgroundCorrectReply();
				setTextViewToReply();
				setBottomButton(loopUntilSuccess);
				invokeOnSuccessEvents();
			}
			break;
		case MODE_REPLY_TO_WRONG_ANSWER:
			if (isOnFailRulesLeaveMission()) {
				invokeOnFailEvents();
				proceedAfterFeedback();
			} else {
				setBackgroundWrongReply();
				setTextViewToReply();
				setBottomButton(loopUntilSuccess);
				invokeOnFailEvents();
			}
			break;
		case MODE_CHOICE:
			finish(Globals.STATUS_SUCCEEDED);
			break;
		}
	}

	private void setBottomButton(boolean loop) {
		bottomButton.setVisibility(View.VISIBLE);

		if (!selectedAnswer.correct && loop) {
			if (selectedAnswer.nextbuttontext != null)
				// game specific if specified:
				bottomButton.setText(selectedAnswer.nextbuttontext);
			else
				bottomButton
						.setText(getString(R.string.question_repeat_button));
			bottomButton.setOnClickListener(restart);
			bottomButton.setGravity(Gravity.LEFT);
			if (bottomButton.getLayoutParams() instanceof LinearLayout.LayoutParams)
				((LinearLayout.LayoutParams) bottomButton.getLayoutParams()).gravity = Gravity.LEFT;
		} else {
			bottomButton.setText(getString(R.string.question_proceed_button));
			bottomButton.setOnClickListener(proceed);
			bottomButton.setGravity(Gravity.RIGHT);
			if (bottomButton.getLayoutParams() instanceof LinearLayout.LayoutParams)
				((LinearLayout.LayoutParams) bottomButton.getLayoutParams()).gravity = Gravity.RIGHT;
		}

		outerView.invalidate();
	}

	private void setTextViewToReply() {
		CharSequence answerToShow;
		if (selectedAnswer.onChoose != null) {
			answerToShow = selectedAnswer.onChoose;
		} else {
			answerToShow = selectedAnswer.correct ? getString(R.string.questionandanswer_rightAnswer)
					: getString(R.string.questionandanswer_wrongAnswer);
		}
		mcTextView.setText(answerToShow);
		new TextItem(answerToShow, this,
				selectedAnswer.correct ? TextType.REACTION_ON_CORRECT
						: TextType.REACTION_ON_WRONG);
		choiceList.setVisibility(View.GONE);
	}

	private void initContentView() {
		setContentView(R.layout.multiplechoice);
		outerView = findViewById(R.id.outerview);
		mcTextView = (TextView) findViewById(R.id.mcTextView);
		choiceList = (ListView) findViewById(R.id.choiceList);
		bottomButton = (Button) findViewById(R.id.bottomButton);
		// prefab neccessary buttons:
		prepareBottomButton();
	}

	private void prepareBottomButton() {
		bottomButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.button));
		proceed = new OnClickListener() {

			public void onClick(View v) {
				proceedAfterFeedback();
			}
		};

		restart = new OnClickListener() {

			public void onClick(View v) {
				setMode(MODE_QUESTION);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void initQuestionAndAnswers() {
		questionText = mission.xmlMissionNode.attributeValue("question");
		if (questionText == null) {
			questionText = mission.xmlMissionNode
					.selectSingleNode(".//questiontext").getText()
					.replaceAll("\\s+", " ").trim();
		}

		questionText = StringTools.replaceVariables(questionText);
		List<Element> xmlAnswers = mission.xmlMissionNode
				.selectNodes(".//answer");
		for (Iterator<Element> j = xmlAnswers.iterator(); j.hasNext();) {
			Element xmlAnswer = j.next();
			new Answer(xmlAnswer);
		}
		List<Element> xmlImageAnswers = mission.xmlMissionNode
				.selectNodes(".//imageanswer");
		for (Iterator<Element> j = xmlImageAnswers.iterator(); j.hasNext();) {
			Element xmlImageAnswer = j.next();
			new Answer(xmlImageAnswer); // TODO create different Answer objects
		}

		textAnswers.addAll(imageAnswers); // TODO Do something different for
		// imageAnswers

		shuffleAnswers();
		setUpAnswerList();
	}

	private void setUpAnswerList() {
		ChoiceListAdapter listAdapter = new ChoiceListAdapter(this,
				R.layout.choice_item, textAnswers);

		choiceList.setAdapter(listAdapter);

		choiceList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent,
							final View view, int position, long id) {
						selectedAnswer = (Answer) parent
								.getItemAtPosition(position);
						onAnswerSelected();
					}

				});
		choiceList.setVisibility(View.VISIBLE);
		choiceList.invalidate();
	}

	private void shuffleAnswers() {
		CharSequence shuffleString = getMissionAttribute("shuffle",
				R.string.multipleChoiceQuestion_default_shuffle_mode)
				.toString();

		if (shuffleString.equals("true")) {
			java.util.Collections.shuffle(imageAnswers);
			java.util.Collections.shuffle(textAnswers);
		}
	}

	private void setUpQuestionView() {
		// show question:
		mcTextView.setText(questionText);
		new TextItem(questionText, this, TextType.QUESTION);

		// list answers:
		setUpAnswerList();
		choiceList.setVisibility(View.VISIBLE);

		// hide buttom:
		bottomButton.setVisibility(View.GONE);
		bottomButton.setGravity(Gravity.RIGHT);
		if (bottomButton.getLayoutParams() instanceof LinearLayout.LayoutParams)
			((LinearLayout.LayoutParams) bottomButton.getLayoutParams()).gravity = Gravity.RIGHT;
		outerView.invalidate();
	}

	/**
	 * Simple class that encapsulates an answer
	 */
	public class Answer {
		public String answertext;
		public Boolean correct;
		public CharSequence onChoose;
		public CharSequence nextbuttontext;
		public CharSequence imagePath;

		public Answer(Element xmlElement) {
			this.answertext = XMLUtilities.getXMLContent(xmlElement);
			this.correct = XMLUtilities
					.getBooleanAttribute(
							"correct",
							R.bool.mission_MultipleChoiceQuestion_default_answerCorrectness,
							xmlElement);
			this.onChoose = XMLUtilities.getStringAttribute("onChoose",
					this.correct ? R.string.questionandanswer_rightAnswer
							: R.string.questionandanswer_wrongAnswer,
					xmlElement);
			this.nextbuttontext = XMLUtilities.getStringAttribute(
					"nextbuttontext",
					this.correct ? R.string.question_proceed_button
							: R.string.question_repeat_button, xmlElement);
			this.imagePath = XMLUtilities.getStringAttribute("image",
					XMLUtilities.OPTIONAL_ATTRIBUTE, xmlElement);
			if ("imageanswer".equals(xmlElement.getName()))
				imageAnswers.add(this);
			else
				textAnswers.add(this);
		}
	}

	public void onBlockingStateUpdated(boolean blocking) {
		choiceList.setEnabled(!blocking);
	}

	public boolean allAnswersWrong() {
		for (Answer curAnswer : textAnswers) {
			if (curAnswer.correct)
				return false;
		}
		return true;
	}

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	private void onAnswerSelected() {
		// set chosen answer text as result in mission specific
		// variable:
		Variables.registerMissionResult(mission.id, selectedAnswer.answertext);
		if (MultipleChoiceQuestion.this.allAnswersWrong()) {
			setMode(MODE_CHOICE);
			return;
		}
		if (selectedAnswer.correct) {
			setMode(MODE_REPLY_TO_CORRECT_ANSWER);
		} else {
			setMode(MODE_REPLY_TO_WRONG_ANSWER);
		}
	}

	private void proceedAfterFeedback() {
		if (loopUntilSuccess) {
			if (!selectedAnswer.correct) {
				setMode(MODE_QUESTION);
			} else {
				finish(Globals.STATUS_SUCCEEDED);
			}
		} else {
			finish(selectedAnswer.correct ? Globals.STATUS_SUCCEEDED
					: Globals.STATUS_FAIL);
		}
	}

}
