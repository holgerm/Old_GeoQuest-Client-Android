package edu.bonn.mobilegaming.geoquest.mission;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;
import android.hardware.Camera.Size;

/**
 * This class records a video. The user can play it after the recording, discard it 
 * or record it one more time.
 * 
 * @author Valeriya Ilyina
 * @author Marieke Kunze
 *
 */
public class VideoRecord extends InteractiveMission implements SurfaceHolder.Callback{

	private static final String TAG = "VideoRecord";

	public static final int BUTTON_TAG_RECORD = 1;
	public static final int BUTTON_TAG_STOP_RECORDING = 2;
	public static final int BUTTON_TAG_PLAY = 3;
	public static final int BUTTON_TAG_STOP_PLAYING = 4;

	public static final int MODE_INITIAL = 1;
	public static final int MODE_RECORDING = 2;
	public static final int MODE_READY = 3;
	public static final int MODE_PLAYING = 4;
	private int mode;

	private TextView taskView;
	private TextView activityIndicator;
	
	private SurfaceView cameraPreview;
	private Camera mCamera;
	private SurfaceHolder holder;

	private Button recBT = null;
	private Button useBT = null;
	private Button playBT = null;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	private String mFileName = null;


	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.videorecord);

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ getMissionAttribute("file", R.string.videorecord_file_default);

		initTaskViewAndActivityIndicator();
		initButtons();

		setMode(MODE_INITIAL);
	}

	/**
	 * Plays the recorded video
	 */
	private void startPlaying() {
		mPlayer = new MediaPlayer();
		mPlayer.setDisplay(holder);
		
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					setMode(MODE_READY);
				}
			});
			mPlayer.prepare();
			mPlayer.start();

		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
	}

	
	/**
	 * Stops the playing of the recorded video
	 */
	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	/**
	 * Starts recording the video
	 */
	@SuppressLint("NewApi")
	private void startRecording() {
		mCamera = Camera.open();

		mCamera.stopPreview();
		mCamera.setDisplayOrientation(90);
		mCamera.unlock();
		
		mRecorder = new MediaRecorder();
		
	    mRecorder.setCamera(mCamera);

		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	
		mRecorder.setPreviewDisplay(holder.getSurface());
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			
        mRecorder.setMaxDuration(120000); // 120 seconds
        mRecorder.setMaxFileSize(10000000); // Approximately 10 megabytes
        
        mRecorder.setOrientationHint(90);
        
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	/**
	 * Stops recording the video.
	 */
	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mCamera.lock();
		mCamera.release();
		mCamera = null;
		mRecorder = null;
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	
		if (cameraPreview != null){
			cameraPreview.destroyDrawingCache();
			cameraPreview = null;
		}
	}

	/**
	 * Enables or disables the buttons if the user has made a recording 
	 * or not.
	 * 
	 * @param newMode
	 */
	private void setMode(int newMode) {
		switch (newMode) {
		case MODE_INITIAL:
			activityIndicator
					.setText(R.string.videorecord_activityIndicator_initial);
			recBT.setEnabled(true);
			recBT.setText(R.string.button_text_record);
			recBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_record), null, null);
			recBT.setTag(BUTTON_TAG_RECORD);
			useBT.setEnabled(false);
			useBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_use_disabled), null, null);
			playBT.setEnabled(false);
			playBT.setText(R.string.button_text_play);
			playBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_play_disabled), null, null);
			playBT.setTag(BUTTON_TAG_PLAY);
			mode = newMode;
			break;
		case MODE_RECORDING:
			activityIndicator
					.setText(R.string.videorecord_activityIndicator_recording);
			recBT.setEnabled(true);
			recBT.setText(R.string.button_text_stop);
			recBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_record_stop), null, null);
			recBT.setTag(BUTTON_TAG_STOP_RECORDING);
			useBT.setEnabled(false);
			useBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_use_disabled), null, null);
			playBT.setEnabled(false);
			playBT.setText(R.string.button_text_play);
			playBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_play_disabled), null, null);
			playBT.setTag(BUTTON_TAG_PLAY);
			startRecording();
			mode = newMode;
			break;
		case MODE_READY:
			activityIndicator
					.setText(R.string.videorecord_activityIndicator_ready);
			recBT.setEnabled(true);
			recBT.setText(R.string.button_text_record);
			recBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_record), null, null);
			recBT.setTag(BUTTON_TAG_RECORD);
			useBT.setEnabled(true);
			useBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_use), null, null);
			playBT.setEnabled(true);
			playBT.setText(R.string.button_text_play);
			playBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_play), null, null);
			playBT.setTag(BUTTON_TAG_PLAY);
			if (mode == MODE_RECORDING)
				stopRecording();
			if (mode == MODE_PLAYING)
				stopPlaying();
			mode = newMode;
			break;
		case MODE_PLAYING:
			activityIndicator
					.setText(R.string.videorecord_activityIndicator_playing);
			recBT.setEnabled(false);
			recBT.setText(R.string.button_text_record);
			recBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_record_disabled), null, null);
			recBT.setTag(BUTTON_TAG_RECORD);
			useBT.setEnabled(false);
			useBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_use_disabled), null, null);
			playBT.setEnabled(true);
			playBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.icon_play_stop), null, null);
			playBT.setText(R.string.button_text_stop);
			playBT.setTag(BUTTON_TAG_STOP_PLAYING);			
			startPlaying();
			mode = newMode;
			break;
		default:
			Log.e(TAG, "Undefined mode " + newMode);
		}
	}

	/**
	 * Initializes the buttons
	 */
	private void initButtons() {
		recBT = (Button) findViewById(R.id.videoRecordRecordButton);
		recBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (mode) {
				case MODE_INITIAL:
				case MODE_READY:
					setMode(MODE_RECORDING);
					break;
				case MODE_RECORDING:
					setMode(MODE_READY);
					break;
				default:
					Log.e(TAG, "Record Button should not be enabled in mode "
							+ mode);
				}
			}
		});

		playBT = (Button) findViewById(R.id.videoRecordPlayButton);
		playBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (mode) {
				case MODE_PLAYING:
					setMode(MODE_READY);
					break;
				case MODE_READY:
					setMode(MODE_PLAYING);
					break;
				default:
					Log.e(TAG, "Play Button should not be enabled in mode "
							+ mode);
				}

			}
		});

		useBT = (Button) findViewById(R.id.videoRecordUseButton);
		useBT.setText(R.string.button_text_use);
		useBT.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
				.getDrawable(R.drawable.icon_use_disabled), null, null);
		useBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (mode) {
				case MODE_READY:
					performFinish();
					break;
				default:
					Log.e(TAG, "Use Button should not be enabled in mode "
							+ mode);
				}
			}

		});
	}

	/**
	 * Initializes the camera preview on the surface view and the text view
	 */
	private void initTaskViewAndActivityIndicator() {
		taskView = (TextView) findViewById(R.id.videoRecordTextView);
		taskView.setText(getMissionAttribute("task",
				R.string.videorecord_task_default));

		activityIndicator = (TextView) findViewById(R.id.videoRecordActivityIndicator);
		cameraPreview = (SurfaceView) findViewById(R.id.videoCameraPreview);
		cameraPreview.setClickable(false);
		holder = cameraPreview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}



	private void performFinish() {
		Variables.registerMissionResult(mission.id, mFileName.toString());
		invokeOnSuccessEvents();
		finish(Globals.STATUS_SUCCEEDED);
	}

	public void onBlockingStateUpdated(boolean blocking) {

	}

	public MissionOrToolUI getUI() {
		return null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	   
	 



}