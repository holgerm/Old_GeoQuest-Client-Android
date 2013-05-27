package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import android.util.Log;
import edu.bonn.mobilegaming.geoquest.mission.AudioRecord;
import edu.bonn.mobilegaming.geoquest.mission.ExternalMission;
import edu.bonn.mobilegaming.geoquest.mission.ImageCapture;
import edu.bonn.mobilegaming.geoquest.mission.MultipleChoiceQuestion;
import edu.bonn.mobilegaming.geoquest.mission.NFCMission;
import edu.bonn.mobilegaming.geoquest.mission.NFCScanMission;
import edu.bonn.mobilegaming.geoquest.mission.NFCTagReadingProduct;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.QRTagReadingProduct;
import edu.bonn.mobilegaming.geoquest.mission.QRTagReadingTreasure;
import edu.bonn.mobilegaming.geoquest.mission.StartAndExitScreen;
import edu.bonn.mobilegaming.geoquest.mission.TextQuestion;
import edu.bonn.mobilegaming.geoquest.mission.VideoPlay;
import edu.bonn.mobilegaming.geoquest.mission.WebPage;
import edu.bonn.mobilegaming.geoquest.mission.WebTech;
import edu.bonn.mobilegaming.geoquest.ui.standard.DefaultUIFactory;

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
					+ "." + uistyle + UIFactory.class.getSimpleName());
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "UIFactory class for style " + uistyle
					+ " not found. Using Default instead.\n" + e.getMessage());
			e.printStackTrace();
		}
		setFactory(factoryClass);
	}

	/**
	 * This method is only used by test cases which override the uistyle with a
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

	public abstract ImageCaptureUI createUI(ImageCapture activity);

	public abstract ExternalMissionUI createUI(ExternalMission activity);

	public abstract MultipleChoiceQuestionUI createUI(
			MultipleChoiceQuestion activity);

	public abstract TextQuestionUI createUI(TextQuestion activity);

	public abstract WebTechUI createUI(WebTech activity);

	public abstract AudioRecordUI createUI(AudioRecord activity);

	public abstract VideoPlayUI createUI(VideoPlay activity);

	public abstract WebPageUI createUI(WebPage activity);

	public abstract StartAndExitScreenUI createUI(StartAndExitScreen activity);

	public abstract QRTagReadingTreasureUI createUI(
			QRTagReadingTreasure activity);

	public abstract QRTagReadingProductUI createUI(QRTagReadingProduct activity);

}
