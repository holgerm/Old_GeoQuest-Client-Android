package edu.bonn.mobilegaming.geoquest.ui.web;

import com.qeevee.util.JSUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.WebView;

/**
 * Class to handle all calls from JS & from Java too
 **/
public class JsHandler {

	Activity activity;
	String TAG = "JsHandler";
	WebView webView;

	public JsHandler(Activity _contxt, WebView _webView) {
		activity = _contxt;
		webView = _webView;
	}

	/**
	 * This function handles call from JS
	 */
	public void jsFnCall(String jsString) {
		showDialog(jsString);
	}

	/**
	 * This function handles call from Android-Java
	 */
	public void javaFnCall(String jsString) {
		
		JSUtil.callJSFuntion("diplayJavaMsg", "'" + jsString + "'", activity, webView);
	}

	/**
	 * function shows Android-Native Alert Dialog
	 */
	public void showDialog(String msg) {

		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setTitle("Test Titel");
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

}