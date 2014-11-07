package com.qeevee.gq.ui.standard;

import java.io.IOException;

import org.dom4j.Element;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.gpstag.GpsTag;
import com.uni.bonn.nfc4mg.infotag.InfoTag;
import com.uni.bonn.nfc4mg.nfctag.ParseTagListener;
import com.uni.bonn.nfc4mg.nfctag.TagIntializer;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;

import com.qeevee.gqphka.R;
import com.qeevee.gq.mission.NFCMission;
import com.qeevee.gq.ui.abstrakt.NFCMissionUI;


public class NFCMissionUIDefault extends NFCMissionUI {

	// Holds Info write into NFC Tag
	private TextView info;
	// Shows current connection information with NFC Tag
	private TextView nfcConnStatus;
	// Next Button
	private Button button;
	// Instance for TAG Initializer.
	private TagIntializer tInitializer;

	// Holds the instance and connection information state of recently scanned
	// NFC Tag.
	private Tag mTag = null;
	private GPSTagModel gpsModel = null;
	private InfoTagModel infoModel = null;

	private int mode = 0;
	private static final int MODE_NEXT_DIALOG_ITEM = 1;
	private static final int MODE_END = 2;

	/**
	 * Handler for handling next write call in case any
	 */
	private OnClickListener showNextDialogListener = new OnClickListener() {
		public void onClick(View v) {
			showNextDialogItem();
		}
	};

	/**
	 * End Callback for NFC Initializer Mission
	 */
	private OnClickListener endMissionListener = new OnClickListener() {
		public void onClick(View v) {
			getNFCMission().finishMission();
		}
	};

	private ParseTagListener parseTagListener = new ParseTagListener() {

		public void onStartParsing(String msg) {

		}

		public void onParseComplete(int tagType) {

			switch (tagType) {
			case TagConstants.TAG_TYPE_INFO:

				infoModel = tInitializer.getmInfoTagModel();

				String data = "Id : " + infoModel.getId() + "\n" + "MIME : "
						+ infoModel.getId() + "\n" + "Data : "
						+ infoModel.getData();

				preTagWriteView(
						data,
						"Ready to Write Data to NFC Tag...Press Write and do not remove Tag",
						true);
				break;
			case TagConstants.TAG_TYPE_GPS:

				gpsModel = tInitializer.getmGPSTagModel();
				String gpsData = "Id : " + gpsModel.getId() + "\n"
						+ "Latitude : " + gpsModel.getLatitude() + "\n"
						+ "Longitude : " + gpsModel.getLongitude() + "\n"
						+ "Data : " + gpsModel.getData();

				preTagWriteView(
						gpsData,
						"Ready to Write Data to NFC Tag...Press Write and do not remove Tag",
						true);
				break;
			default:
				break;
			}
		}
	};

	public NFCMissionUIDefault(NFCMission activity) {
		super(activity);
		tInitializer = new TagIntializer(parseTagListener);
	}

	public void onBlockingStateUpdated(boolean isBlocking) {

	}

	@Override
	public void init(Tag tag) {

		mTag = tag;
		refreshButton();
		tInitializer.initializeFromXML(getNFCMission().getNextItem());
	}

	public void showNextDialogItem() {

		if (null != mTag) {

			if (null != gpsModel) {
				try {
					GpsTag gtag = GpsTag.getInstance();
					gtag.write2Tag(gpsModel, mTag);
					gpsModel = null;
					preTagWriteView(
							"",
							"Tag is successfully initialized...Scan another Tag",
							false);
				} catch (IOException e) {
					error("Connection to Tag is lost.");
					e.printStackTrace();
				} catch (FormatException e) {
					error(e.getMessage());
					e.printStackTrace();
				} catch (NfcTagException e) {
					error(e.getMessage());
					e.printStackTrace();
				}
			} else if (null != infoModel) {
				try {
					InfoTag tag = InfoTag.getInstance();
					tag.write2Tag(infoModel, mTag);
					infoModel = null;
					preTagWriteView(
							"",
							"Tag is successfully initialized...Scan another Tag",
							false);
				} catch (IOException e) {
					error("Connection to Tag is lost.");
					e.printStackTrace();
				} catch (FormatException e) {
					error(e.getMessage());
					e.printStackTrace();
				} catch (NfcTagException e) {
					error(e.getMessage());
					e.printStackTrace();
				}
			}
		} else {
			preTagWriteView("", "Tag Lost! Please touch the Tag again...",
					false);
		}
		refreshButton();
	}

	private void refreshButton() {
		if (getNFCMission().hasMoreDialogItems()) {
			setButtonMode(MODE_NEXT_DIALOG_ITEM);
		} else {
			setButtonMode(MODE_END);
		}
	}

	@Override
	public View createContentView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		contentView = inflater.inflate(R.layout.nfctagmission, null);

		info = (TextView) contentView.findViewById(R.id.info);
		nfcConnStatus = (TextView) contentView.findViewById(R.id.nfcConnStatus);
		button = (Button) contentView.findViewById(R.id.write);
		preTagWriteView("", "Touch NFC Tag to begin...", false);
		return contentView;
	}

	/**
	 * Internal method to show error message while interacting with NFC tag
	 * 
	 * @param msg
	 */
	private void error(String msg) {

		Toast.makeText(getNFCMission(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Until NFC tag is not scanned make view and write button disable
	 */
	private void preTagWriteView(String type, String text, boolean mode) {

		info.setText(type);
		nfcConnStatus.setText(text);
		button.setEnabled(mode);
	}

	private void setButtonMode(int newMode) {
		if (mode == newMode)
			return;
		mode = newMode;
		switch (mode) {
		case MODE_NEXT_DIALOG_ITEM:
			button.setOnClickListener(showNextDialogListener);
			button.setText("Write");
			break;
		case MODE_END:
			button.setOnClickListener(endMissionListener);
			button.setText("End");
			button.setEnabled(true);
			break;
		}
	}

	@Override
	protected Element getMissionXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		contentView.destroyDrawingCache();
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).removeAllViews();
		}

		contentView = null;
	}
}
