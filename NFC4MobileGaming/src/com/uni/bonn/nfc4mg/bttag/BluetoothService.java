package com.uni.bonn.nfc4mg.bttag;

import com.uni.bonn.nfc4mg.wifitag.WiFiActionModes;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Class implements system bluetooth stack operation supported by framework.
 * 
 * @author shubham
 * 
 */
public final class BluetoothService {

	/**
	 * Caller of the API must pass the activity context and action mode read
	 * from the nfc tag
	 * 
	 * @param ctx
	 * @param actionMode
	 * @return false : in case action mode is supported by framework, else true
	 */
	public static void handleBluetoothActionMode(Context ctx, int actionMode) {

		switch (actionMode) {
		case BTActionModes.BT_ON_OFF_AUTOMATICALLY:
			showAlertDialog(ctx,
					"Would you like to perform Bluetooth toogle operation?",
					actionMode);
			break;
		case BTActionModes.BT_PAIRING_SCREEN:
			showAlertDialog(ctx, "Do you want to launch Bluetooth Settings?",
					actionMode);
			break;
		}
	}

	/**
	 * API to toggle sysytem bluetooth service.
	 * 
	 * @return
	 */
	private static boolean toogleSystemBluetooth() {

		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

		if (btAdapter.isEnabled()) {
			return btAdapter.disable();
		}
		return btAdapter.enable();
	}

	/**
	 * API to launch Bluetooth Setting activity
	 * 
	 * @param ctx
	 */
	private static void launchBluetoothSetting(Context ctx) {

		Intent intentBluetooth = new Intent();
		intentBluetooth
				.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
		ctx.startActivity(intentBluetooth);
	}

	private static void showAlertDialog(final Context ctx, String msg,
			final int actionMode) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

		// set title
		alertDialogBuilder.setTitle("Bluetooth Service");

		// set dialog message
		alertDialogBuilder.setMessage(msg).setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						switch (actionMode) {
						case WiFiActionModes.WIFI_ON_OFF_AUTOMATICALLY:

							toogleSystemBluetooth();
							break;
						case WiFiActionModes.WIFI_PAIRING_SCREEN:
							launchBluetoothSetting(ctx);
							break;
						}
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
