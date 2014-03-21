package edu.bonn.mobilegaming.geoquest.mission;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Util;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public class StartAndExitScreen extends MissionActivity {

	private static final String TAG = StartAndExitScreen.class
			.getCanonicalName();
	private ImageView imageView;
	private boolean endByTouch = false;

	/** countdowntimer for the start countdown */
	private MyCountDownTimer myCountDownTimer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_default_startscreen);

		imageView = (ImageView) findViewById(R.id.startimage);

		String duration = (String) XMLUtilities.getStringAttribute("duration",
				R.string.startAndExitScreen_duration_default,
				mission.xmlMissionNode);
		if (duration != null && duration.equals("interactive")) {
			endByTouch = true;
			imageView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					finish(Globals.STATUS_SUCCEEDED);
				}
			});
		} else {
			long durationLong;
			if (duration == null)
				durationLong = 5000;
			else
				durationLong = Long.parseLong(duration);
			myCountDownTimer = new MyCountDownTimer(durationLong, durationLong);
		}
		setImage();
		if (!endByTouch)
			myCountDownTimer.start();
	}

	private void setImage() {
		String pathToImage = (String) XMLUtilities.getStringAttribute("image",
				XMLUtilities.OPTIONAL_ATTRIBUTE, mission.xmlMissionNode);
		try {
			int margin = GeoQuestApp.getContext().getResources()
					.getDimensionPixelSize(R.dimen.margin);
			Bitmap bitmap = BitmapUtil.loadBitmap(pathToImage,
					Util.getDisplayWidth() - (2 * margin), 0, true);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				Log.e(TAG, "Bitmap file invalid: " + pathToImage);
			}
		} catch (IllegalArgumentException iae) {
			imageView.setVisibility(View.GONE);
		}
	}

	/**
	 * Called by the android framework when the focus is changed. When the
	 * mission has the focus and startScreen is true the start screen is shown.
	 * When the mission has the focus and startScreen is false the exit screen
	 * is shown.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			return;
		}
		setImage();
		if (!endByTouch)
			myCountDownTimer.start();
	}

	/**
	 * count down timer for the start screen
	 * 
	 * @author Krischan Udelhoven
	 * @author Folker Hoffmann
	 */

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			finish(Globals.STATUS_SUCCEEDED);
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

	public void onBlockingStateUpdated(boolean blocking) {
		// TODO Auto-generated method stub

	}

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}
}
