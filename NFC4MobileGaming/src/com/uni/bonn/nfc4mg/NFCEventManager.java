package com.uni.bonn.nfc4mg;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;

/**
 * Class implements basic operations to listen for NFC callback. Any Application
 * want to listen for NFC callback must use this class. Factory class to handle
 * NFC related callback specific to application
 * 
 * @author shubham
 * 
 */
public class NFCEventManager {

	private static NFCEventManager INSTANCE = null;

	// NFC adapter instance
	public NfcAdapter mNfcAdapter = null;

	// Application specific NFC intents
	private PendingIntent pendingIntent = null;

	// Dynamic intent filter specific to current running application
	private IntentFilter writeTagFilters[] = null;

	// Status of callback for NFC application specific read/write
	private boolean writeMode;

	/**
	 * NFC Event Manager Singelton class
	 */
	private NFCEventManager() {
	}

	/**
	 * Constructor
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	private NFCEventManager(Context ctx) throws Exception {

		// reference to system NFC manager object.
		mNfcAdapter = NfcAdapter.getDefaultAdapter(ctx.getApplicationContext());

		// Check explicitly, support for NFC feature in client.
		if (null == mNfcAdapter) {
			throw new Exception("Client doesn't support NFC feature!");
		}
	}

	/**
	 * Caller of this API must pass the Current Activity instance in order to
	 * listen to NFC events.
	 * 
	 * @param ctx
	 * @return
	 * @throws Exception
	 *             : In case mobile does not support NFC feature or NFC
	 *             permission is not defined in application manifest file
	 */
	public static NFCEventManager getInstance(Context ctx) throws Exception {

		if (null == INSTANCE) {

			INSTANCE = new NFCEventManager(ctx);
		}
		return INSTANCE;
	}

	/**
	 * Must be called from NFCEventManager getInstance function.
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	public void initialize(Context ctx, Activity instance) throws Exception {

		pendingIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx,
				instance.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
				0);

		// Define intents for NFC
		IntentFilter ACTION_NDEF_DISCOVERED = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);

		IntentFilter ACTION_TAG_DISCOVERED = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);

		IntentFilter ACTION_TECH_DISCOVERED = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);

		ACTION_NDEF_DISCOVERED.addCategory(Intent.CATEGORY_DEFAULT);
		ACTION_TAG_DISCOVERED.addCategory(Intent.CATEGORY_DEFAULT);
		ACTION_TECH_DISCOVERED.addCategory(Intent.CATEGORY_DEFAULT);

		writeTagFilters = new IntentFilter[] { ACTION_NDEF_DISCOVERED,
				ACTION_TAG_DISCOVERED, ACTION_TECH_DISCOVERED };

	}

	/**
	 * Enable NFC read/write when application receives focus.
	 */
	public void attachNFCListener(Activity instance) {
		writeMode = true;
		mNfcAdapter.enableForegroundDispatch(instance, pendingIntent,
				writeTagFilters, null);
	}

	/**
	 * Disable NFC read/write mode when application is paused.
	 */
	public void removeNFCListener(Activity instance) {
		writeMode = false;
		mNfcAdapter.disableForegroundDispatch(instance);
	}

	/**
	 * Application specific NFC callback listener status
	 * 
	 * @return
	 */
	public boolean isWriteMode() {
		return writeMode;
	}

	/**
	 * Api to check Nfc feature is enable in device or not.
	 * 
	 * @return true in case its enable else false.
	 */
	public boolean isNfcEnable() {

		if (null != mNfcAdapter) {

			return mNfcAdapter.isEnabled();
		}
		return false;
	}

	/**
	 * Launch NFC settings to manually enable/disable NFC settings
	 * 
	 * @param instance
	 */
	public void startNfcSettingsActivity(Activity instance) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			instance.startActivity(new Intent(
					android.provider.Settings.ACTION_NFCSHARING_SETTINGS));
		} else {
			instance.startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}
}
