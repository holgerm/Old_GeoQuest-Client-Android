package com.uni.bonn.nfc4mgtest;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.uni.bonn.nfc4mg.NFCEventManager;

public class BluetoothTagActivity extends Activity implements OnClickListener {

	private static final String TAG = "GPSTagActivity";

	private EditText id, macId, passKey;
	private Button read, write;
	private TextView nfcStatus;

	private Context ctx;

	private NFCEventManager mNFCEventManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bt_tag);
		this.ctx = this;

		id = (EditText) findViewById(R.id.id);
		macId = (EditText) findViewById(R.id.macId);
		passKey = (EditText) findViewById(R.id.passKey);
		read = (Button) findViewById(R.id.read);
		write = (Button) findViewById(R.id.write);
		nfcStatus = (TextView) findViewById(R.id.nfcStatus);

		read.setOnClickListener(this);
		write.setOnClickListener(this);

		BluetoothProfile.ServiceListener b = new BlueToothListener();
		boolean profileProxy = BluetoothAdapter.getDefaultAdapter()
				.getProfileProxy(this, b, BluetoothProfile.A2DP);

		Log.d(TAG, "Inside profileProxy = " + profileProxy);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.read:

			break;
		case R.id.write:
			break;
		default:
			break;
		}
	}

	public class BlueToothListener implements ServiceListener {
		public BluetoothA2dp headset;
		public BluetoothDevice bluetoothDevice;

		@Override
		public void onServiceDisconnected(int profile) {

			Log.d(TAG, "Inside onServiceDisconnected = " + profile);
			headset = null;
		}

		@Override
		public void onServiceConnected(int profile, BluetoothProfile proxy) {

			Log.d(TAG, "Inside onServiceConnected = " + profile);
			try {
				if (proxy instanceof BluetoothA2dp) {
					Log.d(TAG, "Instance of BluetoothA2dp class");
					headset = ((BluetoothA2dp) proxy);
					
					List<BluetoothDevice> connectedDevices = proxy
							.getConnectedDevices();
					
					for (BluetoothDevice device : connectedDevices) {
						bluetoothDevice = device;
						Log.d(TAG, "Address = "  +device.getAddress());
						Log.d(TAG, "Name = "  +device.getName());
					}
				} else {
					Log.d(TAG, "Not an Instance of BluetoothA2dp class");
				}
			} catch (Exception e) {
			}
		}
	}
}
