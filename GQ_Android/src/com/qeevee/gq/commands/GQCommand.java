package com.qeevee.gq.commands;

import android.app.AlertDialog;
import android.content.DialogInterface;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public abstract class GQCommand {

	private CharSequence title;
	private CharSequence message;
	private int okButtonTextID;
	private int cancelButtonTextID;

	public GQCommand(CharSequence title, CharSequence message,
			int okButtonTextID, int cancelButtonTextID) {
		this.title = title;
		this.message = message;
		this.okButtonTextID = okButtonTextID;
		this.cancelButtonTextID = cancelButtonTextID;
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
						doIt();
					}
				});
		// Set the Icon for the Dialog
		alertDialog.setIcon(R.drawable.gqlogo_solo_trans);
		alertDialog.show();
		return true;
	}

}
