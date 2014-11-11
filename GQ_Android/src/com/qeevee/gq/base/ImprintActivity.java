package com.qeevee.gq.base;

import com.qeevee.gqdefault.R;
import com.qeevee.gqdefault.R.id;
import com.qeevee.gqdefault.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ImprintActivity extends Activity {

	private static final String IMPRINT_HTML_FILE_PATH = "file:///android_asset/imprint/index.html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imprint);
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl(IMPRINT_HTML_FILE_PATH);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
