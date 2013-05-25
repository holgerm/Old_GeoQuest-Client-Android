package com.uni.bonn.nfc4mgtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TagSelectionActivity extends Activity implements OnClickListener {

	private AlertDialog alertList = null;
	private Button selection, mAudioRecord;

	private final static String TAG_CHOICES[] = { "Info Tag", "GPS Tag",
			"Bluetooth", "Wi-Fi Tag", "Auto Tag Detection"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.choice);

		selection = (Button) findViewById(R.id.selection);
		mAudioRecord = (Button) findViewById(R.id.audio_record);
		selection.setOnClickListener(this);
		mAudioRecord.setOnClickListener(this);

		AlertDialog.Builder listBuilder = new AlertDialog.Builder(
				TagSelectionActivity.this);
		listBuilder.setTitle("Select Tag Type");
		listBuilder.setItems(TAG_CHOICES,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						Intent intent;
						switch (item) {
						case 0:
							intent = new Intent(TagSelectionActivity.this,
									InfoTagActivity.class);
							startActivity(intent);
							break;
						case 1:
							intent = new Intent(TagSelectionActivity.this,
									GPSTagActivity.class);
							startActivity(intent);
							break;
						case 2:
							intent = new Intent(TagSelectionActivity.this,
									BluetoothTagActivity.class);
							startActivity(intent);
							break;
						case 3:
							break;
						case 4:
							intent = new Intent(TagSelectionActivity.this,
									AutoTagDetectionActivity.class);
							startActivity(intent);
							break;
						default:
							break;
						}
					}
				});

		alertList = listBuilder.create();
		alertList.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selection:

			if (null != alertList) {
				alertList.show();
			}

			break;
		case R.id.audio_record:
			Intent intent = new Intent(TagSelectionActivity.this,
					AudioRecordActivity.class);
			startActivity(intent);
			
			break;
		default:
			break;
		}
	}

}
