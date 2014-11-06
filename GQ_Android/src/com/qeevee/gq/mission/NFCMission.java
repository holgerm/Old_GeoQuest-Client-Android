package com.qeevee.gq.mission;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.qeevee.gq.base.Globals;
import com.qeevee.gq.capability.NeedsNFCCapability;
import com.qeevee.gq.history.TransitionItem;
import com.qeevee.gq.ui.UIFactory;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.ui.abstrakt.NFCMissionUI;


public class NFCMission extends InteractiveMission implements
		NeedsNFCCapability {

	private static final String TAG = "NFCMission";
//	private Context ctx;
	// private NFCEventManager mNFCEventManager = null;

	// Global Tag reference
	private Tag mTag = null;

	// Initialize Tag variables
	private Iterator<Element> initTagItemIterator;
	private int nrOfTagItems;
	private NFCMissionUI ui;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Inside NFCMission");

		//		this.ctx = this;
		/*
		 * try { mNFCEventManager = new NFCEventManager(this.ctx);
		 * mNFCEventManager.initialize(this.ctx, NFCMission.this); } catch
		 * (Exception e) { e.printStackTrace(); Toast.makeText(this.ctx,
		 * e.getMessage(), Toast.LENGTH_SHORT).show(); }
		 */

		@SuppressWarnings("unchecked")
		List<Element> initialize = mission.xmlMissionNode
				.selectNodes("./initialize");

		initTagItemIterator = initialize.iterator();
		nrOfTagItems = initialize.size();
		Log.d(TAG, "nrOfDialogItems = " + nrOfTagItems);

		// show the UI here
		ui = UIFactory.getInstance().createUI(this);
	}

	public void finishMission() {
		new TransitionItem(this);
		if (hasMoreDialogItems())
			super.finish(Globals.STATUS_FAIL);
		else
			super.finish(Globals.STATUS_SUCCEEDED);
	}

	/**
	 * @return true if this mission still has at least one more dialogs item to
	 *         show.
	 */
	public boolean hasMoreDialogItems() {
		return initTagItemIterator.hasNext();
	}

	public Element getNextItem() {
		return initTagItemIterator.next();
	}

	/*
	 * @Override public void onPause() { super.onPause(); if (null !=
	 * mNFCEventManager) { mNFCEventManager.removeNFCListener(NFCMission.this);
	 * } }
	 */

	/*
	 * @Override public void onResume() { super.onResume(); if (null !=
	 * mNFCEventManager) { mNFCEventManager.attachNFCListener(NFCMission.this);
	 * } }
	 */

	@Override
	protected void onNewIntent(Intent intent) {

		Log.v(TAG, "Inside onNewIntent fn");

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Log.v(TAG, "Intent Action :: ACTION_TAG_DISCOVERED");

			mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			ui.init(mTag);
		}
	}

	public MissionOrToolUI getUI() {
		return null;
	}
}
