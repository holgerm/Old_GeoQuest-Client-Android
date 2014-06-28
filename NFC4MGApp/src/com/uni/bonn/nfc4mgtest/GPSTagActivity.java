package com.uni.bonn.nfc4mgtest;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.bonn.nfc4mg.NFCEventManager;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.gpstag.GpsTag;
import com.uni.bonn.nfc4mg.infotag.InfoTag;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;

public class GPSTagActivity extends Activity implements OnClickListener {

	private static final String TAG = "GPSTagActivity";

	private EditText id, latitude, longitude;
	private Button read, write;
	private TextView nfcStatus;

	private Context ctx;
	// Global Tag reference
	private Tag mTag = null;

	private NFCEventManager mNFCEventManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gps_tag);
		this.ctx = this;

		id = (EditText) findViewById(R.id.id);
		latitude = (EditText) findViewById(R.id.latitude);
		longitude = (EditText) findViewById(R.id.longitude);
		read = (Button) findViewById(R.id.read);
		write = (Button) findViewById(R.id.write);
		nfcStatus = (TextView) findViewById(R.id.nfcStatus);

		read.setOnClickListener(this);
		write.setOnClickListener(this);

		try {
			mNFCEventManager = NFCEventManager.getInstance(this.ctx);
			mNFCEventManager.initialize(this.ctx, GPSTagActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mNFCEventManager) {
			mNFCEventManager.attachNFCListener(GPSTagActivity.this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mNFCEventManager) {
			mNFCEventManager.removeNFCListener(GPSTagActivity.this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.v(TAG, "Inside onNewIntent fn");

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Log.v(TAG, "Intent Action :: ACTION_TAG_DISCOVERED");

			mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			nfcStatus.setText("Ready to interact with NFC Tag");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.read:

			if (null != mTag) {
				try {
					InfoTag tag = InfoTag.getInstance();
					InfoTagModel model = tag.readTagData(mTag);

					if (null != model) {
						id.setText(model.getId());
						longitude.setText(model.getId());
						longitude.setText(model.getData());
					} else {
						id.setText("");
						longitude.setText("");
						longitude.setText("");
						Toast.makeText(GPSTagActivity.this,
								"GPS Tag Formatted Exception.",
								Toast.LENGTH_SHORT).show();
					}

				} catch (IOException e) {

					e.printStackTrace();
					Toast.makeText(GPSTagActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();

				} catch (FormatException e) {

					e.printStackTrace();
					Toast.makeText(GPSTagActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();

				} catch (NfcTagException e) {
					e.printStackTrace();
					Toast.makeText(GPSTagActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(GPSTagActivity.this, "Touch NFC Tag First.",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.write:

			String ii = id.getEditableText().toString();
			String mm = latitude.getEditableText().toString();
			String dd = longitude.getEditableText().toString();

			GPSTagModel model = new GPSTagModel(ii, mm, dd, "");

			if (null != mTag) {
				try {
					GpsTag tag = GpsTag.getInstance();
					boolean status = tag.write2Tag(model, mTag);

					if (status) {
						Toast.makeText(GPSTagActivity.this, "Success.",
								Toast.LENGTH_SHORT).show();
					}

				} catch (IOException e) {

					e.printStackTrace();
					Toast.makeText(GPSTagActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();

				} catch (FormatException e) {

					e.printStackTrace();
					Toast.makeText(GPSTagActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();

				} catch (NfcTagException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(GPSTagActivity.this, "Touch NFC Tag First.",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
}
