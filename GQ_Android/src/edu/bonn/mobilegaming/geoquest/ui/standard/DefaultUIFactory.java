package edu.bonn.mobilegaming.geoquest.ui.standard;

import edu.bonn.mobilegaming.geoquest.mission.AudioRecord;
import edu.bonn.mobilegaming.geoquest.mission.ExternalMission;
import edu.bonn.mobilegaming.geoquest.mission.ImageCapture;
import edu.bonn.mobilegaming.geoquest.mission.MultipleChoiceQuestion;
import edu.bonn.mobilegaming.geoquest.mission.NFCMission;
import edu.bonn.mobilegaming.geoquest.mission.NFCScanMission;
import edu.bonn.mobilegaming.geoquest.mission.NFCTagReadingProduct;
import edu.bonn.mobilegaming.geoquest.mission.NPCTalk;
import edu.bonn.mobilegaming.geoquest.mission.MapOSM;
import edu.bonn.mobilegaming.geoquest.mission.QRTagReadingProduct;
import edu.bonn.mobilegaming.geoquest.mission.StartAndExitScreen;
import edu.bonn.mobilegaming.geoquest.mission.TextQuestion;
import edu.bonn.mobilegaming.geoquest.mission.VideoPlay;
import edu.bonn.mobilegaming.geoquest.mission.WebPage;
import edu.bonn.mobilegaming.geoquest.mission.WebTech;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.AudioRecordUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.ExternalMissionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.ImageCaptureUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MultipleChoiceQuestionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCMissionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCScanMissionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCTagReadingProductUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NPCTalkUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MapOSM_UI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.QRTagReadingProductUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.StartAndExitScreenUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.TextQuestionUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.VideoPlayUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.WebPageUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.WebTechUI;

public class DefaultUIFactory extends UIFactory {

	public DefaultUIFactory() {
		super();
	}

	public NFCTagReadingProductUI createUI(NFCTagReadingProduct activity) {
		return new NFCTagReadingProductUIDefault(activity);
	}

	public NFCScanMissionUI createUI(NFCScanMission activity) {
		return new NFCScanMissionUIDefault(activity);
	}

	public NFCMissionUI createUI(NFCMission activity) {
		return new NFCMissionUIDefault(activity);
	}

	public NPCTalkUI createUI(NPCTalk activity) {
		return new NPCTalkUIDefault(activity);
	}

	public ImageCaptureUI createUI(ImageCapture activity) {
		return new ImageCaptureUIDefault(activity);
	}

	public ExternalMissionUI createUI(ExternalMission activity) {
		return new ExternalMissionUIDefault(activity);
	}

	public TextQuestionUI createUI(TextQuestion activity) {
		return new TextQuestionDefault(activity);
	}

	public WebTechUI createUI(WebTech activity) {
		return new WebTechUIDefault(activity);
	}

	public AudioRecordUI createUI(AudioRecord activity) {
		return new AudioRecordUIDefault(activity);
	}

	public VideoPlayUI createUI(VideoPlay activity) {
		return new VideoPlayUIDefault(activity);
	}

	public WebPageUI createUI(WebPage activity) {
		return new WebPageUIDefault(activity);
	}

	public StartAndExitScreenUI createUI(StartAndExitScreen activity) {
		return new StartAndExitScreenUIDefault(activity);
	}

	public QRTagReadingProductUI createUI(QRTagReadingProduct activity) {
		return new QRTagReadingProductUIDefault(activity);
	}

	public MultipleChoiceQuestionUI createUI(MultipleChoiceQuestion activity) {
		return new MultipleChoiceQuestionUI(activity);
	}

	@Override
	public MapOSM_UI createUI(MapOSM activity) {
		return new MapOSM_UIDefault(activity);
	}

}
