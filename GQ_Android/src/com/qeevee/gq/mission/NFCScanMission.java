package com.qeevee.gq.mission;

import java.io.IOException;
import java.util.List;

import org.dom4j.Element;

import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.qeevee.gq.base.Globals;
import com.qeevee.gq.base.Variables;
import com.qeevee.gq.capability.NeedsNFCCapability;
import com.qeevee.gq.ui.UIFactory;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.ui.abstrakt.NFCScanMissionUI;
import com.uni.bonn.nfc4mg.NFCEventManager;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.nfctag.ParseTagListener;
import com.uni.bonn.nfc4mg.nfctag.TagHandler;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;

public class NFCScanMission extends InteractiveMission implements
		ParseTagListener, NeedsNFCCapability {

	private static final String TAG = "NFCScanMission";
	private Context ctx;
	private NFCEventManager mNFCEventManager = null;

	// Global Tag reference
	// private Tag mTag = null;
	// private int tagType = 0;
	private TagHandler mHandler = null;

	// Scan Tag local variables
	private String type;
	private String successMsg;
	private String id;
	private String latitude;
	private String longitude;
	private String needFruit;
	// private String passKey;
	private String description;
	private String earnFruit;
	private String verifyUserInput;
	private String initImg;
	private String succImg;

	private static int fruit_count = 0;

	private NFCScanMissionUI ui;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Inside NFCScanMission");

		this.ctx = this;
		try {
			mNFCEventManager = NFCEventManager.getInstance(this.ctx);
			mNFCEventManager.initialize(this.ctx, NFCScanMission.this);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("unchecked")
		List<Element> nfctouch = mission.xmlMissionNode
				.selectNodes("./nfctouch");

		Element element = nfctouch.iterator().next();

		type = element.attributeValue("type");
		successMsg = element.attributeValue("successMsg");
		initImg = element.attributeValue("initImg");
		id = element.attributeValue("id");
		latitude = element.attributeValue("latitude");
		longitude = element.attributeValue("longitude");
		needFruit = element.attributeValue("needFruit");
		// passKey = element.attributeValue("passKey");
		description = element.attributeValue("description");
		earnFruit = element.attributeValue("earnFruit");
		succImg = element.attributeValue("succImg");
		verifyUserInput = element.attributeValue("verifyUserInput");

		ui = UIFactory.getInstance().createUI(this);
		ui.init();

		try {
			mHandler = new TagHandler(this, this);
		} catch (NfcTagException e) {
			e.printStackTrace();
			Toast.makeText(this.ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Getter Functions used in View class
	 * 
	 * @return
	 */
	public String getVerifyUserInput() {
		return verifyUserInput;
	}

	public String getSuccessMsg() {
		return successMsg;
	}

	public String getDescription() {
		return description;
	}

	public String getInitImg() {
		return initImg;
	}

	public String getSuccImg() {
		return succImg;
	}

	/**
	 * Finishing the Current running mission
	 */
	public void finishMission() {
		finish(Globals.STATUS_SUCCEEDED);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (null != mNFCEventManager) {
			mNFCEventManager.removeNFCListener(NFCScanMission.this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != mNFCEventManager) {
			mNFCEventManager.attachNFCListener(NFCScanMission.this);
		}
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

			if ("info".equals(this.type)) {

				InfoTagModel iModel = mHandler.getmInfoTagModel();

				// Check for id
				String id = "info_" + this.id;
				if (id.equals(iModel.getId())) {

					// TODO In second implementation cycle inventory concept
					// will be introduced. Currently we are simulating this with
					// class variable.
					// This means just add item to inventory
					if ("true".equals(this.earnFruit)) {

						Log.d(TAG, "This is simple earn fruit mission.");

						fruit_count++;
						ui.success(iModel.getData());
						ui.enableButton(true);
						// check for fruit in repository, else error
						// set scanned result in mission specific variable:
						Variables.registerMissionResult(mission.id,
								iModel.getData());
						invokeOnSuccessEvents();
						break;
					}

					// Check in inventory in case any resource required before
					// execute any mission
					if ("true".equals(this.needFruit)) {

						// In case no fruit in inventory end mission
						if (fruit_count <= 0) {
							// check for fruit in repository, else error
							// set scanned result in mission specific variable:
							Variables.registerMissionResult(mission.id,
									"needFruit");
							invokeOnSuccessEvents();
							finishMission();
							break;
						}

						// In other case decrement the inventory count
						fruit_count--;
						ui.success(iModel.getData());
						ui.enableButton(true);

						// check for fruit in repository, else error
						// set scanned result in mission specific variable:
						Variables.registerMissionResult(mission.id,
								iModel.getData());
						invokeOnSuccessEvents();
						break;
					}

					if ("true".equals(this.verifyUserInput)) {

						if (ui.verifyUserInput(iModel.getData())) {

							ui.success(iModel.getData());
							ui.enableButton(true);
							// check for fruit in repository, else error
							// set scanned result in mission specific variable:
							Variables.registerMissionResult(mission.id,
									iModel.getData());
							invokeOnSuccessEvents();
							break;
						} else {

							finishMission();
							break;
						}
					}

					ui.success(iModel.getData());
					ui.enableButton(true);
					// check for fruit in repository, else error
					// set scanned result in mission specific variable:
					Variables.registerMissionResult(mission.id,
							iModel.getData());
					invokeOnSuccessEvents();
					break;

				}
			}
			// In case Current mission is not matched by scanned Tag; End the
			// mission automatically.
			// check for fruit in repository, else error
			// set scanned result in mission specific variable:
			Variables.registerMissionResult(mission.id, "invalidTag");
			invokeOnSuccessEvents();
			finishMission();
			break;

		case TagConstants.TAG_TYPE_GPS:

			if ("gps".equals(this.type)) {

				GPSTagModel gModel = mHandler.getmGPSTagModel();

				// Check for id
				String id = "gps_" + this.id;
				if (id.equals(gModel.getId())) {

					this.id = "gps_" + this.id;
					if (this.id.equals(gModel.getId())
							&& this.latitude.equals(gModel.getLatitude())
							&& this.longitude.equals(gModel.getLongitude())) {

						Log.d(TAG, "Correct GPS Tag found");
						ui.success(gModel.getData());
						ui.enableButton(true);
						// set scanned result in mission specific variable:
						Variables.registerMissionResult(mission.id,
								gModel.getLatitude());
						invokeOnSuccessEvents();
						break;

					}
				}
			}

			// In case Current mission is not matched by scanned Tag; End the
			// mission automatically.
			// check for fruit in repository, else error
			// set scanned result in mission specific variable:
			Variables.registerMissionResult(mission.id, "invalidTag");
			invokeOnSuccessEvents();
			finishMission();
			break;
		default:
			break;
		}
	}
}
