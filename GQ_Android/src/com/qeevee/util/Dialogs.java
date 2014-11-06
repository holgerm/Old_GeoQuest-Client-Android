package com.qeevee.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.qeevee.gq.R;
import com.qeevee.gq.base.GeoQuestApp;

public class Dialogs {

	public static final int DIALOG_DELETE_ALL = 1;
	public static final int DIALOG_END_GAME = 2;

	public final static OnClickListener endGameOnClickListener = new OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			GeoQuestApp.getInstance().endGame();
		}

	};

	public final static OnClickListener terminateAppOnClickListener = new OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			GeoQuestApp.getInstance().terminateApp();
		}

	};

	public final static OnClickListener cancelOnClickListener = new OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
		}
	};

	public static Dialog createYesNoDialog(Context context,
			int titleResourceID, OnClickListener yesClickListener,
			OnClickListener noClickListener) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(titleResourceID);
		builder.setCancelable(true);
		builder.setPositiveButton(GeoQuestApp.getContext()
				.getText(R.string.yes), yesClickListener);
		builder.setNegativeButton(
				GeoQuestApp.getContext().getText(R.string.no), noClickListener);
		AlertDialog dialog = builder.create();
		return dialog;
	}

	public static final int DIALOG_TERMINATE_APP = 10;

}
