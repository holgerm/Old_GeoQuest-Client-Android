package com.qeevee.gq.commands;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.qeevee.gq.R;
import com.qeevee.gq.base.GeoQuestApp;

public abstract class GQCommand {

	private CharSequence title;
	private CharSequence message;
	private int okButtonTextID;

	public GQCommand(CharSequence title, CharSequence message,
			int okButtonTextID, int cancelButtonTextID) {
		this.title = title;
		this.message = message;
		this.okButtonTextID = okButtonTextID;
	}

	/**
	 * @return success
	 */
	public abstract boolean doIt();

	/**
	 * Just wait for the user to allow it. The user has no other option than to
	 * confirm that the command should be executed.
	 * 
	 * @return true iff it has been performed, i.e. the result of the internally
	 *         called {@link #doIt()}.
	 */
	public final boolean makeUserConfirmAndDoIt() {
		AlertDialog alertDialog = new AlertDialog.Builder(
				GeoQuestApp.getCurrentActivity()).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(GeoQuestApp.getContext()
				.getString(okButtonTextID),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doIt();
					}
				});
		// Set the Icon for the Dialog
		alertDialog.setIcon(R.drawable.app_item_icon);
		alertDialog.show();
		return true;
	}

}
