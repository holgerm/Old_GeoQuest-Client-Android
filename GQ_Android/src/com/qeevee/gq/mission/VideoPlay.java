package com.qeevee.gq.mission;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.xml.XMLUtilities;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Globals;
import com.qeevee.gq.R;

public class VideoPlay extends InteractiveMission {

	private static final String TAG = "VideoPlay";

	CharSequence videoUriCharSequence = null;
	AlertDialog exitDialog;

	private VideoView videoView;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		// hide endgame menu item into submenu, since it looks misleading while
		// watching a movie.
		menu.findItem(R.id.menu_endGame).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.m_default_videoplay);

		videoView = (VideoView) this.findViewById(R.id.video);
		videoView.setVideoURI(initVideoUri());
		boolean isControllable = XMLUtilities
				.getBooleanAttribute("controllable",
						R.bool.videoplay_controllable_default, getXML());
		setControllers(isControllable);

		if (bundle != null && bundle.containsKey("position")) {
			videoView.seekTo(bundle.getInt("position"));
		}
		videoView.requestFocus();
		videoView.setVisibility(View.VISIBLE);
		videoView.start();
	}

	private void setControllers(final boolean controllable) {
		MediaController mc = new MediaController(this);
		mc.setPrevNextListeners(finishButtonClickListener, null);
		if (controllable)
			videoView.setMediaController(mc);
		videoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					public void onCompletion(MediaPlayer mp) {
						showExitAlertDialog(controllable);
					}

				});
		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e(TAG, "Problem occurred when trying to play the video: "
						+ videoUriCharSequence.toString());
				showErrorAlertDialog();
				return true;
			}
		});
		finishButtonClickListener = new View.OnClickListener() {

			public void onClick(View v) {
				showExitAlertDialog(controllable);
			}
		};
	}

	public Uri initVideoUri() {
		videoUriCharSequence = getMissionAttribute("file",
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		if (videoUriCharSequence == null) {
			// attribute FILE NOT given => try to use URL:
			try {
				videoUriCharSequence = getMissionAttribute("url",
						XMLUtilities.NECESSARY_ATTRIBUTE);
			} catch (IllegalArgumentException iae) {
				Log.e(TAG,
						"Both attributes file and url missing in VideoPlay Mission "
								+ mission.id + ". At least one must be given.",
						iae);
				finish();
			}
			return Uri.parse(videoUriCharSequence.toString());
		} else {
			// attribute FILE given:
			return Uri.fromFile(GeoQuestApp
					.getGameRessourceFile(videoUriCharSequence.toString()));
		}
	}

	private void showErrorAlertDialog() {
		AlertDialog errorDialog = new AlertDialog.Builder(VideoPlay.this)
				.setTitle(R.string.error_dialog_title)
				.setMessage(R.string.videoplay_errordialog_message)
				.setNegativeButton(R.string.button_text_proceed,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish(Globals.STATUS_SUCCEEDED);
							}
						}).show();
		errorDialog
				.getButton(AlertDialog.BUTTON_NEGATIVE)
				.setCompoundDrawablesWithIntrinsicBounds(null, null,
						getResources().getDrawable(R.drawable.icon_leave), null);
	}

	private void showExitAlertDialog(boolean controllable) {
		if (controllable) {
			exitDialog = new AlertDialog.Builder(VideoPlay.this)
					.setTitle(R.string.videoplay_finishdialog_title)
					.setMessage(R.string.videoplay_finishdialog_message)
					.setNegativeButton(
							R.string.videoplay_finishdialog_keepwatching, null)
					.setPositiveButton(R.string.videoplay_finishdialog_leave,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finishMission();
								}
							}).show();
			exitDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
					.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(R.drawable.icon_again),
							null, null, null);
			exitDialog.getButton(AlertDialog.BUTTON_POSITIVE)
					.setCompoundDrawablesWithIntrinsicBounds(null, null,
							getResources().getDrawable(R.drawable.icon_leave),
							null);
		} else {
			exitDialog = new AlertDialog.Builder(VideoPlay.this)
					.setTitle(R.string.videoplay_finishdialog_title)
					.setMessage(R.string.videoplay_finishdialog_message)
					.setPositiveButton(R.string.videoplay_finishdialog_leave,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finishMission();
								}
							}).show();
			exitDialog.getButton(AlertDialog.BUTTON_POSITIVE)
					.setCompoundDrawablesWithIntrinsicBounds(null, null,
							getResources().getDrawable(R.drawable.icon_leave),
							null);
		}
	}

	public void onBlockingStateUpdated(boolean blocking) {
		// TODO Auto-generated method stub

	}

	private View.OnClickListener finishButtonClickListener;

	@Override
	protected void onPause() {
		super.onPause();
		if (videoView != null)
			videoView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (videoView != null)
			videoView.resume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (videoView != null) {
			outState.putInt("position", videoView.getCurrentPosition());
			super.onSaveInstanceState(outState);
		}
	}

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	private void finishMission() {
		if (videoView != null) {
			videoView.stopPlayback();
			videoView.destroyDrawingCache();
			videoView = null;
		}
		finish(Globals.STATUS_SUCCEEDED);
	}
}