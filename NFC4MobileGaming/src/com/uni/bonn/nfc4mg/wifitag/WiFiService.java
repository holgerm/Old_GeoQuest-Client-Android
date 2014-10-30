package com.uni.bonn.nfc4mg.wifitag;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.uni.bonn.nfc4mg.tagmodels.WiFiTagModel;

/**
 * This class manages system wifi related features.
 * 
 * @author shubham
 * 
 */
public final class WiFiService {

	/**
	 * Caller of the API must pass the activity context and action mode read
	 * from the nfc tag
	 * 
	 * @param ctx
	 * @param actionMode
	 * @return false : in case action mode is supported by framework, else true
	 */
	public static void handleWifiActionMode(Context ctx, WiFiTagModel model) {

		int actionMode = model.getActionMode();
		switch (actionMode) {
		case WiFiActionModes.WIFI_ON_OFF_AUTOMATICALLY:
			showAlertDialog(ctx,
					"Would you like to perform WiFi toogle operation?",
					model);
			break;
		case WiFiActionModes.WIFI_PAIRING_SCREEN:
			showAlertDialog(ctx, "Do you want to launch WiFi Settings?",
					model);
		case WiFiActionModes.WIFI_AUTO_CONNECT:
			showAlertDialog(ctx, "Do you want to Perform WiFi Auto Connect?",
					model);
			break;
		}
	}

	/**
	 * API to toggle system Wifi service.
	 * 
	 * @return
	 */
	private static boolean toogleSystemWiFi(Context ctx) {

		WifiManager wifiManager = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);

		return wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
	}

	/**
	 * API to launch Wifi Setting activity
	 * 
	 * @param ctx
	 */
	private static void launchWiFiSetting(Context ctx) {

		Intent intentWifi = new Intent();
		intentWifi.setAction(android.provider.Settings.ACTION_WIFI_SETTINGS);
		ctx.startActivity(intentWifi);
	}
	
	/**
	 * This is to perform auto connect to given ssid and password
	 * @param model
	 */
	private static void performWiFiAutoConnect(Context ctx, WiFiTagModel model){
		
		String networkId = model.getSsid();
		String pwd = model.getPassword();

		SimpleWifiInfo wifiInfo = new SimpleWifiInfo();
		wifiInfo.setSsid(networkId);
		wifiInfo.setPwd(pwd);
		setNewWifi(ctx, wifiInfo);
		
		
	}

	/**
	 * private function for auto connect to wifi network
	 * @param ctx
	 * @param wifiInfo
	 */
	private static void setNewWifi(Context ctx, SimpleWifiInfo wifiInfo) {

		WifiManager wifiManager = (WifiManager) ctx.getSystemService(
				Context.WIFI_SERVICE);

		wifiManager.setWifiEnabled(true);

		boolean foundAKnownNetwork = false;

		//In case of no configured network, this will be null
		List<WifiConfiguration> configuredNetworks = wifiManager
				.getConfiguredNetworks();

		if (null != configuredNetworks) {
			for (WifiConfiguration wifiConfiguration : configuredNetworks) {

				if (wifiConfiguration.SSID.equals("\"" + wifiInfo.getSsid()
						+ "\"")) {

					foundAKnownNetwork = true;

					boolean result = wifiManager.enableNetwork(
							wifiConfiguration.networkId, true);

					if (result) {
						showLongToast(ctx, "Now connected to known network \""
								+ wifiInfo.getSsid()
								+ "\". If you want to set a new WPA key, please delete the network first.");
					} else {
						showLongToast(ctx, "Connection to a known network failed.");
					}
				}
			}
		}

		if (!foundAKnownNetwork) {
			setupNewNetwork(ctx, wifiInfo, wifiManager);
		}
	}

	/**
	 * Internal function for setting up new wifi network and initiating connection
	 * @param ctx
	 * @param wifiInfo
	 * @param wifiManager
	 */
	private static void setupNewNetwork(Context ctx, SimpleWifiInfo wifiInfo,
			WifiManager wifiManager) {

		WifiConfiguration wc = new WifiConfiguration();

		wc.SSID = "\"" + wifiInfo.getSsid() + "\"";
		wc.preSharedKey = "\"" + wifiInfo.getPwd() + "\"";

		int networkId = wifiManager.addNetwork(wc);
		boolean result = wifiManager.enableNetwork(networkId, true);

		if (result) {
			showLongToast(ctx, "Now connected to \"" + wifiInfo.getSsid() + "\"");
			wifiManager.saveConfiguration();
		} else {
			showLongToast(ctx, "Creating connection failed. " + wc);
		}
	}
	
	private static void showLongToast(Context ctx, String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
	}
	
	private static class SimpleWifiInfo{
		
		String ssid;
		String pwd;

		public String getSsid() {
			return ssid;
		}

		public void setSsid(String ssid) {
			this.ssid = ssid;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
		
	}
	
	/**
	 * Asking user permission
	 * @param ctx
	 * @param msg
	 * @param model
	 */
	private static void showAlertDialog(final Context ctx, String msg,
			final WiFiTagModel model) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

		// set title
		alertDialogBuilder.setTitle("WiFi Service");

		// set dialog message
		alertDialogBuilder.setMessage(msg).setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						switch (model.getActionMode()) {
						case WiFiActionModes.WIFI_ON_OFF_AUTOMATICALLY:

							toogleSystemWiFi(ctx);
							break;
						case WiFiActionModes.WIFI_PAIRING_SCREEN:
							launchWiFiSetting(ctx);
							break;
						case WiFiActionModes.WIFI_AUTO_CONNECT:
							performWiFiAutoConnect(ctx, model);
						}
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
