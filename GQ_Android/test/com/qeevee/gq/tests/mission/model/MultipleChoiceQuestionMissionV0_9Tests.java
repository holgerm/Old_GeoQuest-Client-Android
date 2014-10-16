package com.qeevee.gq.tests.mission.model;

import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

/**
 * In contrast to the FunctionTest this one loads a game.xml which uses the new
 * V0.9 version of MCQ.
 * 
 * <ul>
 * <li>No surrounding question tag
 * <li>Text question text is in mission attribute
 * </ul>
 * 
 * The rest is completely the same.
 * 
 * @author muegge
 * 
 */
@RunWith(GQTestRunner.class)
public class MultipleChoiceQuestionMissionV0_9Tests extends
		MultipleChoiceQuestionMissionTests {

	protected void setGameName() {
		gameName = "MultipleChoiceQuestion/V0_9_Test";
	}

}
