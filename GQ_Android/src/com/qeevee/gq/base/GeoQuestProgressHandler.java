package com.qeevee.gq.base;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GeoQuestProgressHandler extends Handler {

	public final static int MSG_TELL_MAX_AND_TITLE = 0;
	public final static int MSG_PROGRESS = 1;
	public static final int MSG_FINISHED = 2;
	public static final int MSG_ABORT_BY_ERROR = 3;
	public static final int MSG_TELL_MAX = 4;
	public static final int MSG_RESET_PROGRESS = 5;

	public static boolean LAST_IN_CHAIN = true;
	public static boolean FOLLOWED_BY_OTHER_PROGRESS_DIALOG = false;

	private ProgressDialog progressDialog;
	private ProgressBar progressBar;
	private boolean isLastInChain;
	
	private TextView update;

	public GeoQuestProgressHandler(ProgressDialog progressDialog,
			boolean isLastInChain) {
		super();
		this.progressDialog = progressDialog;
		this.isLastInChain = isLastInChain;
	}

	public GeoQuestProgressHandler(ProgressDialog downloadRepoDataDialog,
			boolean isLastInChain, Object objectToNotifyAfterFinish) {
		this(downloadRepoDataDialog, isLastInChain);
	}
	
	public GeoQuestProgressHandler(ProgressBar progressBar, TextView update,
			boolean isLastInChain) {
		super();
		this.progressBar = progressBar;
		this.isLastInChain = isLastInChain;
		this.update = update;
	}
	
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case GeoQuestProgressHandler.MSG_PROGRESS:
			if (progressDialog != null) {
				progressDialog.incrementProgressBy(1);
			} else if (progressBar != null) {
				progressBar.incrementProgressBy(1);
			}
			break;
		case GeoQuestProgressHandler.MSG_TELL_MAX_AND_TITLE:
			if (progressDialog != null) {
				progressDialog.setProgress(0);
				progressDialog.setMax(msg.arg1);
				progressDialog.setMessage(GeoQuestApp.getContext()
						.getText(msg.arg2));
			} else if (progressBar != null) {
				progressBar.setProgress(0);
				progressBar.setMax(msg.arg1);
				if (update != null) {
					update.setText(GeoQuestApp.getContext().getText(msg.arg2));
				}
			}
			break;
		case GeoQuestProgressHandler.MSG_FINISHED:
			if (isLastInChain) {
				if (progressDialog != null) {
					progressDialog.setProgress(progressDialog.getMax());
					progressDialog.setMessage("");
					progressDialog.dismiss();
				} else if (progressBar != null) {
					progressBar.setProgress(progressBar.getMax());
					if (update != null) {
						update.setText("");
					}
				}
			}
			break;
		case GeoQuestProgressHandler.MSG_ABORT_BY_ERROR:
			if (isLastInChain) {
				if (progressDialog != null) {
					progressDialog.setProgress(0);
					progressDialog.setMessage("");
					progressDialog.dismiss();
				} else if (progressBar != null) {
					progressBar.setProgress(0);
					if (update != null) {
						update.setText("");
					}
				}
			}
			GeoQuestApp.showMessage(GeoQuestApp.getContext().getText(msg.arg1));
			break;
		case GeoQuestProgressHandler.MSG_RESET_PROGRESS:
			if (progressDialog != null) {
				progressDialog.setProgress(0);
			} else if (progressBar != null) {
				progressBar.setProgress(0);
			}
		}
	}
	
	public void handleError(int msgID) {
		Message msg = new Message();
		msg.arg1 = msgID;
		msg.what = GeoQuestProgressHandler.MSG_ABORT_BY_ERROR;
		this.sendMessage(msg);
	}

}
