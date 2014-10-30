package com.qeevee.gq.ui;

import java.util.Locale;

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
import com.qeevee.gq.ui.standard.DefaultUIFactory;

import android.util.Log;

public abstract class UIFactory {

	private static final String TAG = UIFactory.class.getName();
	private static UIFactory instance;

	protected UIFactory() {
	}

	/**
	 * Sets the UIFactory to the given style or uses {@link DefaultUIFactory} as
	 * default.
	 * 
	 * @param uistyle
	 *            either a valid name of a UIFactory or null to reset this
	 *            singleton.
	 */
	public static void selectUIStyle(String uistyle) {
		if (uistyle == null) {
			instance = null;
			return;
		}
		Class<?> factoryClass = null;
		try {
			factoryClass = Class.forName(UIFactory.class.getPackage().getName()
					+ "." + uistyle.toLowerCase(Locale.US) + "." + uistyle
					+ UIFactory.class.getSimpleName());
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "UIFactory class for style " + uistyle
					+ " not found. Using Default instead.\n" + e.getMessage());
			e.printStackTrace();
		}
		setFactory(factoryClass);
	}

	/**
	 * This method is only used by test cases which override the ui style with a
	 * mocking test style.
	 * 
	 * @param factoryClass
	 */
	public static void setFactory(Class<?> factoryClass) {
		try {
			instance = (UIFactory) factoryClass.newInstance();
		} catch (IllegalAccessException e1) {
			Log.e(TAG, e1.getMessage());
			e1.printStackTrace();
			instance = null;
		} catch (InstantiationException e1) {
			Log.e(TAG, e1.getMessage());
			e1.printStackTrace();
			instance = null;
		}
	}

	public static UIFactory getInstance() {
		if (instance == null) {
			instance = new DefaultUIFactory();
		}
		return instance;
	}

	public abstract NFCTagReadingProductUI createUI(
			NFCTagReadingProduct activity);

	public abstract NFCScanMissionUI createUI(NFCScanMission activity);

	public abstract NFCMissionUI createUI(NFCMission activity);

	public abstract NPCTalkUI createUI(NPCTalk activity);

	public abstract MapOSM_UI createUI(MapOSM activity);

	public abstract ImageCaptureUI createUI(ImageCapture activity);

	public abstract MultipleChoiceQuestionUI createUI(
			MultipleChoiceQuestion activity);

	public abstract TextQuestionUI createUI(TextQuestion activity);

	public abstract AudioRecordUI createUI(AudioRecord activity);

	public abstract VideoPlayUI createUI(VideoPlay activity);

	public abstract WebPageUI createUI(WebPage activity);

	public abstract StartAndExitScreenUI createUI(StartAndExitScreen activity);

	public abstract QRTagReadingUI createUI(QRTagReading activity);

}
