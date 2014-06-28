package com.uni.bonn.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity implements Runnable {
	/**
	 * The minimal time that this screen to be shown.
	 */
	private static final long MIN_SHOW_TIME = 3 * 1000000000l; // 3 seconds
	/**
	 * The creation time.
	 */
	private long mCreationTime;
	/**
	 * Handler used to display MainActivity after 3 seconds.
	 */
	private Handler mHandler = new Handler();

	/**
	 * Function called when Activity is started.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash_screen);
		// Save the creation time.
		if (null == savedInstanceState) {
			mCreationTime = System.nanoTime();
		}

		long delay = (mCreationTime + MIN_SHOW_TIME - System.nanoTime()) / 1000000;
		mHandler.postDelayed(this, delay);

	}

	/**
	 * Function which launches the MainActivity.
	 */
	private void startMainActivity() {
		Intent lIntent = new Intent(this, HomeActivity.class);
		startActivity(lIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mHandler != null) {
			mHandler.removeCallbacks(this);
			finish();
		}
	}

	@Override
	public void run() {
		startMainActivity();

	}
}
