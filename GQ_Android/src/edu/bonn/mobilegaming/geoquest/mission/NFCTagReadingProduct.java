package edu.bonn.mobilegaming.geoquest.mission;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.nfctag.ParseTagListener;
import com.uni.bonn.nfc4mg.nfctag.TagHandler;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;

import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.capability.NeedsNFCCapability;
import edu.bonn.mobilegaming.geoquest.ui.UIFactory;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCTagReadingProductUI;

public class NFCTagReadingProduct extends InteractiveMission implements
		ParseTagListener, NeedsNFCCapability {

	private static final String TAG = "NFCTagReadingProduct";
	private Context ctx;

	// Global Tag reference
	private TagHandler mHandler = null;

	private NFCTagReadingProductUI ui;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Inside onCreate");

		ui = UIFactory.getInstance().createUI(this);

		String taskDescription = (String) getMissionAttribute(
				"taskdescription",
				R.string.qrtagreading_taskdescription_default);

		ui.init(taskDescription);
		try {
			mHandler = new TagHandler(this, this);
		} catch (NfcTagException e) {
			e.printStackTrace();
			Toast.makeText(this.ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Finishing the Current running mission
	 */
	public void finishMission() {
		finish(Globals.STATUS_SUCCEEDED);
	}

	@Override
	protected void onNewIntent(Intent intent) {

		Log.v(TAG, "Inside onNewIntent fn");

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Log.v(TAG, "Intent Action :: ACTION_TAG_DISCOVERED");

			if (null != mHandler) {
				try {
					Log.d(TAG, "processing Intent");
					mHandler.processIntent(intent);

				} catch (IOException e) {

				} catch (FormatException e) {

				} catch (Exception e) {

				}
			}
		}
	}

	public MissionOrToolUI getUI() {
		return null;
	}

	public void onStartParsing(String msg) {

	}

	public void onParseComplete(int tagType) {

		Log.d(TAG, "Inside onParseComplete = " + tagType);

		switch (tagType) {

		case TagConstants.TAG_TYPE_INFO:

			InfoTagModel iModel = mHandler.getmInfoTagModel();
			// set scanned result in mission specific variable:
			Log.d(TAG, "Data = " + iModel.getData());
			Variables.registerMissionResult(mission.id, iModel.getData());
			invokeOnSuccessEvents();
			finishMission();
			break;

		case TagConstants.TAG_TYPE_GPS:

			GPSTagModel gModel = mHandler.getmGPSTagModel();

			Log.d(TAG, "Data = " + gModel.getData());
			// set scanned result in mission specific variable:
			Variables.registerMissionResult(mission.id, gModel.getData());
			invokeOnSuccessEvents();
			finishMission();
			break;
		}
	}
}
