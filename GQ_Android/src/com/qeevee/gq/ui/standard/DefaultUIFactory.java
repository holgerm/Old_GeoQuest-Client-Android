package com.qeevee.gq.ui.standard;

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
import com.qeevee.gq.ui.UIFactory;
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

	public TextQuestionUI createUI(TextQuestion activity) {
		return new TextQuestionDefault(activity);
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

	public QRTagReadingUI createUI(QRTagReading activity) {
		return new QRTagReadingUIDefault(activity);
	}

	public MultipleChoiceQuestionUI createUI(MultipleChoiceQuestion activity) {
		return new MultipleChoiceQuestionUI(activity);
	}

	@Override
	public MapOSM_UI createUI(MapOSM activity) {
		return new MapOSM_UIDefault(activity);
	}

}
