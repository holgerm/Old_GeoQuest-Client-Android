package com.qeevee.util;

import android.app.Activity;
import android.webkit.WebView;

public class JSUtil {

	static public void callJSFuntion(final String functionName,
			final String parameterString, Activity activity,
			final WebView webView) {

		if (!activity.isFinishing())
			activity.runOnUiThread(new Runnable() {

				public void run() {
					webView.loadUrl("javascript:" + functionName + "("
							+ parameterString + ")");
				}
			});

	}
}
