package edu.bonn.mobilegaming.geoquest.mission;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

/**
 * Start and exit screen mission. The first mission to be started (gets the id
 * zero). Shows the start screen countdown and after the countdown starts the
 * next mission specified in the xml file. When all missions are over the start
 * and exit screen mission is active again and nice looking firework is shown.
 * 
 * @author Krischan Udelhoven
 * @author Folker Hoffmann
 */
public class StartAndExitScreen extends MissionActivity {

    private ImageView imageView;
    private boolean endByTouch = false;

    /** countdowntimer for the start countdown */
    private MyCountDownTimer myCountDownTimer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.start);

	imageView = (ImageView) findViewById(R.id.startimage);

	String duration = mission.xmlMissionNode.attributeValue("duration");
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
	String imgsrc = mission.xmlMissionNode.attributeValue("image");
	imageView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
		.loadBitmap(imgsrc,
			    true)));
	if (!endByTouch)
	    myCountDownTimer.start();
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

	String imgsrc = mission.xmlMissionNode.attributeValue("image");
	imageView.setBackgroundDrawable(new BitmapDrawable(BitmapUtil
		.loadBitmap(imgsrc,
			    true)));
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
	public MyCountDownTimer(long millisInFuture,
				long countDownInterval) {
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
