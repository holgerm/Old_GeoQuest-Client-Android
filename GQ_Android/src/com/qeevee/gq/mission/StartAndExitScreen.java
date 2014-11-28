package com.qeevee.gq.mission;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
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
	private boolean endByTouch = false;

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

		imageView = (ImageView) findViewById(R.id.startimage);
		outerView = (View) findViewById(R.id.outerview);

		readDurationAttribute();
		handleImageAttributes();

		initOnTap();

		if (!endByTouch && myCountDownTimer != null)
			myCountDownTimer.start();
	}

	private void readDurationAttribute() {
		String duration = (String) XMLUtilities.getStringAttribute("duration",
				R.string.startAndExitScreen_duration_default,
				mission.xmlMissionNode);
		if (duration == null)
			duration = "5000";
		if (duration.equals("interactive")) {
			endByTouch = true;
			imageView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					finish(Globals.STATUS_SUCCEEDED);
				}
			});
			return;
		}
		if (duration.equals("infinite"))
			return; // TODO: weg oder feature?
		if (duration.equals("animation"))
			return; // TODO: implement

		// else:
		long durationLong = Long.parseLong(duration);
		myCountDownTimer = new MyCountDownTimer(durationLong, durationLong);
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

	private void handleImageAttributes() {
		String pathToImage = ((String) XMLUtilities.getStringAttribute("image",
				XMLUtilities.OPTIONAL_ATTRIBUTE, mission.xmlMissionNode))
				.trim();
		if (pathToImage.endsWith(".zip")) {
			boolean loop = (Boolean) XMLUtilities.getBooleanAttribute("loop",
					R.string.animation_loop_default, mission.xmlMissionNode);
			int framerate = (Integer) XMLUtilities.getIntegerAttribute("fps",
					R.string.animation_fps_default, mission.xmlMissionNode);
			// TODO framerate
			setupAnimation(pathToImage, framerate, loop);
		} else {
			setupImage(pathToImage);
		}
	}

	/**
	 * Erases all files in the cache dir.
	 * 
	 * @param pathToAnimationArchive
	 */
	private void setupAnimation(String pathToAnimationArchive, int framerate,
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
				// TODO extract as boolean function to some util class
			}
		});

		// TODO setup animation drawable and start the animation
		AnimationDrawable animD = new AnimationDrawable();
		String frameFile;
		for (int i = 0; i < frameFileNames.length; i++) {
			frameFile = animationCacheDir.getAbsolutePath() + "/"
					+ frameFileNames[i];
			animD.addFrame(new BitmapDrawable(frameFile), 1000 / framerate);
		}

		imageView.setBackgroundDrawable(animD);

		animD.setOneShot(!loop);
		animD.stop();
		animD.start();
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
		if (!endByTouch && myCountDownTimer != null)
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

	@Override
	public void finish() {
		// GeoQuestApp.recycleImagesFromView(imageView);
		super.finish();
	}

}
