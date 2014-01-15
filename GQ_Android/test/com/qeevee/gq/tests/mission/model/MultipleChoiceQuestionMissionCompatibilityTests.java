package com.qeevee.gq.tests.mission.model;

import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

/**
 * In contrast to the FunctionTest this one loads a game.xml which still uses
 * the question xml tag inside the mission as parent of questiontext and
 * answers.
 * 
 * The rest is completely the same.
 * 
 * @author muegge
 * 
 */
@RunWith(GQTestRunner.class)
public class MultipleChoiceQuestionMissionCompatibilityTests extends
		MultipleChoiceQuestionMissionTests {

	@SuppressWarnings("unused")
	private static final String GAME_NAME = "MultipleChoiceQuestion/FunctionCompatibilityTest";

}
