package com.uni.bonn.nfc;

import java.io.IOException;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.uni.bonn.nfc4mg.NFCEventManager;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.nfctag.ParseTagListener;
import com.uni.bonn.nfc4mg.nfctag.TagHandler;

public class HomeActivity extends Activity implements ParseTagListener,
		android.view.View.OnClickListener {

	private static final String TAG = "HomeActivity";
	private static final int MSG_PAUSE = 1;
	private static final int MSG_PLAY = 2;

	private static VideoView mVideoView;
	private LinearLayout mPinLyt;

	private NFCEventManager mNFCEventManager = null;

	// Game parameters.
	private TagHandler mTagHamdler;
	private int mCurrentInteraction = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_layout);
		mPinLyt = (LinearLayout) findViewById(R.id.pin_layout);

		initWheel(R.id.passw_1);
		initWheel(R.id.passw_2);
		initWheel(R.id.passw_3);

		try {
			mNFCEventManager = NFCEventManager.getInstance(this);
			mNFCEventManager.initialize(this, HomeActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mTagHamdler = new TagHandler(this, this);
		} catch (NfcTagException e) {
			e.printStackTrace();
		}

		mVideoView = (VideoView) findViewById(R.id.video_view);

		String path = "android.resource://" + getPackageName() + "/"
				+ R.raw.intro;
//		setnPlayVideo(path); TEST

		hidePinLyt();
	}

	private void hidePinLyt() {

		mPinLyt.setVisibility(View.GONE);
		mVideoView.setVisibility(View.VISIBLE);
	}

	private void showPinLyt() {

		mVideoView.setVisibility(View.GONE);
		mPinLyt.setVisibility(View.VISIBLE);
	}

	/**
	 * Initialises wheel
	 * 
	 * @param id
	 *            the wheel widget Id
	 */
	private void initWheel(int id) {
		WheelView wheel = getWheel(id);
		wheel.setViewAdapter(new NumericWheelAdapter(this, 0, 9));
		wheel.setCurrentItem((int) (Math.random() * 10));

		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(true);
		wheel.setInterpolator(new AnticipateOvershootInterpolator());
	}

	/**
	 * Returns wheel by Id
	 * 
	 * @param id
	 *            the wheel Id
	 * @return the wheel with passed Id
	 */
	private WheelView getWheel(int id) {
		return (WheelView) findViewById(id);
	}

	// Wheel scrolled flag
	private boolean wheelScrolled = false;

	// Wheel scrolled listener
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			wheelScrolled = true;
		}

		public void onScrollingFinished(WheelView wheel) {
			wheelScrolled = false;
			// updateStatus();
		}
	};

	// Wheel changed listener
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!wheelScrolled) {
				// updateStatus();
			}
		}
	};

	/**
	 * Tests entered PIN
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param v4
	 * @return true
	 */
	private boolean testPin(int v1, int v2, int v3) {

		return testWheelValue(R.id.passw_1, v1)
				&& testWheelValue(R.id.passw_2, v2)
				&& testWheelValue(R.id.passw_3, v3);
	}

	/**
	 * Tests wheel value
	 * 
	 * @param id
	 *            the wheel Id
	 * @param value
	 *            the value to test
	 * @return true if wheel value is equal to passed value
	 */
	private boolean testWheelValue(int id, int value) {
		return getWheel(id).getCurrentItem() == value;
	}

	private void setnPlayVideo(final String path) {
		mVideoView.setVideoURI(Uri.parse(path));
		mVideoView.start();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (null != mNFCEventManager) {
			mNFCEventManager.removeNFCListener(HomeActivity.this);
		}

		/*
		 * if (null != mVideoView && mVideoView.isPlaying()) {
		 * mVideoView.pause(); }
		 */
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mNFCEventManager) {
			mNFCEventManager.attachNFCListener(HomeActivity.this);
		}

		/*
		 * if (null != mVideoView) { mVideoView.start(); }
		 */
	}

	@Override
	protected void onNewIntent(Intent intent) {

		try {

			mTagHamdler.processIntent(intent);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NfcTagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStartParsing(String msg) {

	}

	@Override
	public void onParseComplete(int tagType) {

		switch (tagType) {

		case TagConstants.TAG_TYPE_INFO:

			String data = mTagHamdler.getmInfoTagModel().getData();
			handleInteraction(data);
			break;
		case TagConstants.TAG_TYPE_RESOURCE:

			String resName = mTagHamdler.getmResourceTagModel().getName();
			int resCount = mTagHamdler.getmResourceTagModel().getCount();

			boolean isPlaying = mVideoView.isPlaying();

			if (!isPlaying) {
				handleResourceInteraction(resName, resCount);
			}
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		switch (id) {

		case 1:
			alertBuilder.setTitle(R.string.alert_title);
			alertBuilder.setMessage(R.string.err_invalid_action);
			alertBuilder.setCancelable(true);
			alertBuilder.setPositiveButton(R.string.btn_ok,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();

						}
					});
			break;
		case 2:

			alertBuilder.setMessage(R.string.ignite_candle);
			alertBuilder.setCancelable(true);
			alertBuilder.setPositiveButton(R.string.btn_yes,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
							// Set the interaction counter to next scene

						}
					}).setNegativeButton(R.string.btn_no,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});

			break;
		}

		// create alert dialog
		AlertDialog alertDialog = alertBuilder.create();

		return alertDialog;
	}

	/**
	 * Handler Attached to activity to execute video related events.
	 */
	private static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MSG_PLAY:
				mVideoView.start();
				break;
			case MSG_PAUSE:
				mVideoView.pause();
				break;
			default:
				break;
			}
		}
	};

	private void handleResourceInteraction(final String data, final int resCount) {

		if (mCurrentInteraction == 2 && "matchbox".equalsIgnoreCase(data)) {

			mCurrentInteraction = 3;

			String path = "android.resource://" + getPackageName() + "/"
					+ R.raw.tunnel_match_candle;
			setnPlayVideo(path);

			Message m1 = new Message();
			m1.what = MSG_PAUSE;
			mHandler.sendMessageDelayed(m1, 8000);

		} else {
			handleWrongInteraction(mCurrentInteraction);
		}

	}

	private void handleInteraction(final String data) {

		boolean isPlaying = mVideoView.isPlaying();

		// concert ticket case : start
		if (!isPlaying && mCurrentInteraction == 0
				&& "concert_ticket".equalsIgnoreCase(data)) {

			// Set the interaction counter to next scene
			mCurrentInteraction = 1;
			String path = "android.resource://" + getPackageName() + "/"
					+ R.raw.concert;
			setnPlayVideo(path);

		}
		// concert ticket case : end

		// Beer case : start
		else if (!isPlaying && mCurrentInteraction == 1
				&& "milchschnitte".equalsIgnoreCase(data)) {// replace
															// by
															// beer

			// Set the interaction counter to next scene
			mCurrentInteraction = 2;
			String path = "android.resource://" + getPackageName() + "/"
					+ R.raw.beer;
			setnPlayVideo(path);

		}
		// Beer case : end

		// tunnel match box and candle : start
		else if (isPlaying && mCurrentInteraction == 3
				&& "candle".equalsIgnoreCase(data)) {

			// remove old callback first
			mHandler.removeMessages(MSG_PAUSE);

			// If within 8 seconds player scan the candle resume the video
			Message m1 = new Message();
			m1.what = MSG_PLAY;
			mHandler.sendMessage(m1);
			mCurrentInteraction = 4;

		}

		// Garden key : start
		else if (!isPlaying && mCurrentInteraction == 4
				&& "key".equalsIgnoreCase(data)) {

			mCurrentInteraction = 5;
			String path = "android.resource://" + getPackageName() + "/"
					+ R.raw.garden_key;
			setnPlayVideo(path);

		}
		// Garden key : end

		// Map : start
		else if (!isPlaying && mCurrentInteraction == 5
				&& "maps".equalsIgnoreCase(data)) {

			mCurrentInteraction = 6;
			String path = "android.resource://" + getPackageName() + "/"
					+ R.raw.map;
			setnPlayVideo(path);

			mVideoView.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {

					showPinLyt();
				}
			});

		}
		// Map : end

		else {

			if (!isPlaying) {
				handleWrongInteraction(mCurrentInteraction);
			}
		}
	}

	/**
	 * Common Error handler for entire game
	 * 
	 * @param state
	 */
	private void handleWrongInteraction(int state) {

		String path = null;
		switch (state) {
		case 0:
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.concert_wrong;

			break;
		case 1:
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.beer_wrong;
			break;
		case 2:
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.tunnel_match_candle_wrong;
			break;
		case 3:
			mCurrentInteraction = 2;
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.tunnel_match_candle_wrong;
			break;
		case 4:
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.garden_key_wrong;
			break;
		case 5:
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.map_wrong;
			break;
		case 6:
			path = "android.resource://" + getPackageName() + "/"
					+ R.raw.bike_lock_wrong;

			mVideoView.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {

					if (!isMissionSuccess) {
						showPinLyt();
					}
				}
			});

			break;
		default:
			break;
		}

		if (null != path) {
			setnPlayVideo(path);
		} else {
			showDialog(1);
		}
	}

	private boolean isMissionSuccess = false;

	/**
	 * Updates entered PIN status
	 */
	private void updateStatus() {
		if (testPin(5, 5, 1)) {

			isMissionSuccess = true;
			String path = "android.resource://" + getPackageName() + "/"
					+ R.raw.bike_lock;
			hidePinLyt();
			setnPlayVideo(path);

		} else {

			hidePinLyt();
			handleWrongInteraction(mCurrentInteraction);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:

			updateStatus();

			break;

		default:
			break;
		}
	}
}
