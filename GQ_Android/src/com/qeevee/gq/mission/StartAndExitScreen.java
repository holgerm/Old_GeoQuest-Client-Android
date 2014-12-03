package com.qeevee.gq.mission;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.base.Globals;
import com.qeevee.gq.base.Variables;
import com.qeevee.gq.rules.Rule;
import com.qeevee.gq.start.GameDataManager;
import com.qeevee.gq.ui.ScreenArea;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.gqdefault.R;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Device;
import com.qeevee.util.FileOperations;

public class StartAndExitScreen extends MissionActivity {

	private static final String TAG = StartAndExitScreen.class
			.getCanonicalName();
	private ImageView imageView;
	private boolean endByTimer = true;

	private List<Rule> onTapRules = new ArrayList<Rule>();
	private boolean onTapRulesLeaveMission = false;

	public boolean doOnTapRulesLeaveMission() {
		return onTapRulesLeaveMission;
	}

	public void setOnTapRulesLeaveMission(boolean onTapRulesLeaveMission) {
		this.onTapRulesLeaveMission = onTapRulesLeaveMission;
	}

	/** runs the GQEvents when the user taps on the screen */
	public void applyOnTapRules() {
		Rule.resetRuleFiredTracker();
		ScreenArea.updateScreenAreaTappedVars();
		for (Rule rule : onTapRules) {
			rule.apply();
		}
	}

	/** countdowntimer for the start countdown */
	private MyCountDownTimer myCountDownTimer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_default_startscreen);

		imageView = (ImageView) findViewById(R.id.canvas);
		outerView = (View) findViewById(R.id.outerview);

		int animationDuration = handleImageAttributes();
		handleDurationAttribute(animationDuration);

		initOnTap();

		if (endByTimer && myCountDownTimer != null)
			myCountDownTimer.start();
	}

	private void handleDurationAttribute(int animationDuration) {
		long durationLong;
		String duration = (String) XMLUtilities.getStringAttribute("duration",
				R.string.startAndExitScreen_duration_default,
				mission.xmlMissionNode);

		// Is it a number?
		try {
			durationLong = Long.parseLong(duration);
			// CASE 1: number was explicitly given or by default.
			myCountDownTimer = new MyCountDownTimer(durationLong, durationLong);
			return;
		} catch (NumberFormatException nfe) {
			if (!duration.equals("interactive")
					&& !duration.equals("animation")) {
				// if attribute does NOT contain a valid value we rescue to
				// default:
				duration = getString(R.string.startAndExitScreen_duration_default);
				Log.w(TAG, "Specified value for duration is not set correct: "
						+ duration + ". We rescue to default value.");
				try {
					durationLong = Long.parseLong(duration);
					// CASE 2: we rescued to default value and got a number:
					myCountDownTimer = new MyCountDownTimer(durationLong,
							durationLong);
					return;
				} catch (NumberFormatException nfe2) {
					// we rescued to default value and got a string - we assume
					// it is correct (checked by tests).
				}
			}
		}

		if (duration.equals("interactive")) {
			// CASE 3: interactive set either in attribute or default:
			setEndInteractive();
			return;
		}

		if (duration.equals("animation")) {
			boolean loop = (Boolean) XMLUtilities.getBooleanAttribute("loop",
					R.string.animation_loop_default, mission.xmlMissionNode);
			if (!loop) {
				// CASE 4: animation set with single shot:
				myCountDownTimer = new MyCountDownTimer(animationDuration,
						animationDuration);
				return;
			} else {
				// CASE 4: animation set with loop, so we act as interactive had
				// been set:
				setEndInteractive();
				return;
			}
		}
	}

	private void setEndInteractive() {
		endByTimer = false;
		imageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish(Globals.STATUS_SUCCEEDED);
			}
		});
	}

	private void initOnTap() {
		// init interaction rules from game spec:
		addRulesToList(onTapRules, "onTap/rule");
		if (onTapRules.size() == 0)
			return;
		else {
			imageView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {

					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);

					int x = (int) Math.abs((double) event.getX() * 1000
							/ (v.getMeasuredWidth()));
					int y = (int) Math.abs((double) event.getY() * 1000
							/ (v.getMeasuredHeight()));

					Variables.setValue(Variables.LAST_TAP_X, (double) x);
					Variables.setValue(Variables.LAST_TAP_Y, (double) y);

					applyOnTapRules();
					return false;
				}
			});
			for (Rule rule : onTapRules) {
				if (rule.leavesMission()) {
					setOnTapRulesLeaveMission(true);
					break;
				}
			}
		}
	}

	private int handleImageAttributes() {
		String pathToImage = ((String) XMLUtilities.getStringAttribute("image",
				XMLUtilities.OPTIONAL_ATTRIBUTE, mission.xmlMissionNode))
				.trim();
		if (pathToImage.endsWith(".zip")) {
			boolean loop = (Boolean) XMLUtilities.getBooleanAttribute("loop",
					R.string.animation_loop_default, mission.xmlMissionNode);
			int framerate = (Integer) XMLUtilities.getIntegerAttribute("fps",
					R.string.animation_fps_default, mission.xmlMissionNode);
			return setupAnimation(pathToImage, framerate, loop);
		} else {
			setupImage(pathToImage);
			return 0;
		}
	}

	/**
	 * Erases all files in the cache dir.
	 * 
	 * @param pathToAnimationArchive
	 * @param framerate
	 * @param loop
	 * @return number of milliseconds this animation will take to run through
	 *         one time.
	 */
	private int setupAnimation(String pathToAnimationArchive, int framerate,
			boolean loop) {
		File animationArchiveFile = new File(GeoQuestApp.getRunningGameDir(),
				pathToAnimationArchive);
		File animationCacheDir = new File(getCacheDir(), "anim");
		if (animationCacheDir.exists())
			FileOperations.cleanDirectory(animationCacheDir);
		String cacheDirPath = animationCacheDir.getAbsolutePath();
		GameDataManager.unzipFile(animationArchiveFile, cacheDirPath);

		// prepare image files for iteration:
		String[] frameFileNames = animationCacheDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return (filename.endsWith(".jpg") || filename.endsWith(".png") || filename
						.endsWith(".gif"));
			}
		});

		// TODO setup animation drawable and start the animation
		AnimationDrawable animD = new AnimationDrawable();
		String frameFile;
		for (int i = 0; i < frameFileNames.length; i++) {
			frameFile = animationCacheDir.getAbsolutePath() + "/"
					+ frameFileNames[i];
			animD.addFrame(new BitmapDrawable(getResources(), frameFile),
					1000 / framerate);
		}

		imageView.setBackgroundDrawable(animD);

		animD.setOneShot(!loop);
		animD.stop();
		animD.start();

		return framerate * animD.getNumberOfFrames();
	}

	private void setupImage(String pathToImage) {
		try {
			int margin = GeoQuestApp.getContext().getResources()
					.getDimensionPixelSize(R.dimen.margin);
			Bitmap bitmap = BitmapUtil.loadBitmap(pathToImage,
					Device.getDisplayWidth() - (2 * margin),
					Device.getDisplayHeight() - (2 * margin), true);
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
		handleImageAttributes();
		if (endByTimer && myCountDownTimer != null)
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
			endByTimer = true;
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

	@Override
	public void finish() {
		// GeoQuestApp.recycleImagesFromView(imageView);
		Drawable drawable = imageView.getBackground();
		if (!(drawable instanceof AnimationDrawable)) {
			// TODO release Bitmap
		}

		AnimationDrawable anim = (AnimationDrawable) drawable;

		anim.stop();
		for (int i = 0; i < anim.getNumberOfFrames(); ++i) {
			Drawable frame = anim.getFrame(i);
			if (frame instanceof BitmapDrawable) {
				Bitmap bmp = ((BitmapDrawable) frame).getBitmap();
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
					bmp = null;
				}
			}
			frame.setCallback(null);
			frame = null;
		}
		anim.setCallback(null);
		//		System.gc();

		super.finish();
	}

}
