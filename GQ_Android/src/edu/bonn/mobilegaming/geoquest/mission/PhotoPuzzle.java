package edu.bonn.mobilegaming.geoquest.mission;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public class PhotoPuzzle extends MissionActivity {

	private ImageView imageView, blackboxView1, blackboxView2;
	private boolean endByTouch = true;

	/** countdowntimer for the start countdown */
	private MyCountDownTimer myCountDownTimer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_default_photopuzzle);

		imageView = (ImageView) findViewById(R.id.photopuzzle);
		blackboxView1 = (ImageView) findViewById(R.id.blackbox1);
		blackboxView2 = (ImageView) findViewById(R.id.blackbox2);
		
		long durationLong = 5000;
		myCountDownTimer = new MyCountDownTimer(durationLong, durationLong);
		setImage();
		if (!endByTouch)
			myCountDownTimer.start();
	}

	private void setImage() {
		String imgsrc = (String) XMLUtilities.getAttribute("puzzleImage",
				XMLUtilities.NECESSARY_ATTRIBUTE, mission.xmlMissionNode);
		if (imgsrc != null)
			imageView
					.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
							.getRoundedCornerBitmap(
									BitmapUtil.loadBitmap(imgsrc), 15)));
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
		long counter = 0;

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			hidePicture();
		}

		private void hidePicture() {
			

			int height = getWindowManager().getDefaultDisplay().getHeight() / 5;
			int width = getWindowManager().getDefaultDisplay().getWidth() / 3;

			Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
					Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();

			paint.setColor(Color.BLACK);

			blackboxView1.setImageBitmap(bitmap);
			blackboxView2.setImageBitmap(bitmap);

			canvas.drawRect(0, 0, (float) width, (float) height, paint);

		}

		@Override
		public void onFinish() {
			finish(Globals.STATUS_SUCCEEDED);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			counter += 1;
			if (counter % 300 == 0) {
				revealPicture();
			}
		}

		private void revealPicture() {

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
