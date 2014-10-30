package com.uni.bonn.nfc4mgtest;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.uni.bonn.nfc4mg.NFCEventManager;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.nfctag.ParseTagListener;
import com.uni.bonn.nfc4mg.nfctag.TagHandler;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;

public class AutoTagDetectionActivity extends Activity implements
		OnClickListener, ParseTagListener {

	private static final String TAG = "AutoTagDetectionActivity";
	private TagHandler mHandler = null;
	private Button detect;

	private Context ctx;

	private NFCEventManager mNFCEventManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.auto_detection);
		detect = (Button) findViewById(R.id.detect);
		detect.setOnClickListener(this);

		this.ctx = this;

		try {

			mNFCEventManager = NFCEventManager.getInstance(this.ctx);
			mNFCEventManager
					.initialize(this.ctx, AutoTagDetectionActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		try {
			mHandler = new TagHandler(this, this);
		} catch (NfcTagException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mNFCEventManager) {
			mNFCEventManager.attachNFCListener(AutoTagDetectionActivity.this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mNFCEventManager) {
			mNFCEventManager.removeNFCListener(AutoTagDetectionActivity.this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.v(TAG, "Inside onNewIntent fn");

		if (null != mHandler) {

			try {

				mHandler.processIntent(intent);

			} catch (IOException e) {

				Toast.makeText(AutoTagDetectionActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();

			} catch (FormatException e) {

				Toast.makeText(AutoTagDetectionActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();

			} catch (Exception e) {

				Toast.makeText(AutoTagDetectionActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();

			}
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.detect:

			break;

		default:
			break;
		}
	}

	@Override
	public void onStartParsing(String msg) {

		Log.v(TAG, msg);

	}

	@Override
	public void onParseComplete(int tagType) {

		switch (tagType) {
		case TagConstants.TAG_TYPE_INFO:

			InfoTagModel iModel = mHandler.getmInfoTagModel();

			String data = iModel.getId() + "\n" + iModel.getId() + "\n"
					+ iModel.getData();

			Toast.makeText(AutoTagDetectionActivity.this, data,
					Toast.LENGTH_SHORT).show();

			break;

		case TagConstants.TAG_TYPE_GPS:

			GPSTagModel gModel = mHandler.getmGPSTagModel();

			String coords = gModel.getId() + "\n" + gModel.getLatitude() + "\n"
					+ gModel.getLongitude();

			Toast.makeText(AutoTagDetectionActivity.this, coords,
					Toast.LENGTH_SHORT).show();

			break;
		default:

			Toast.makeText(AutoTagDetectionActivity.this,
					"Not able to detect Tag type.", Toast.LENGTH_SHORT).show();

			break;
		}

	}
}
