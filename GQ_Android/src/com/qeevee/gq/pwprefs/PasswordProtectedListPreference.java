package com.qeevee.gq.pwprefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.qeevee.gqdefault.R;

public class PasswordProtectedListPreference extends ListPreference {
	
	/*
	 * TODO: This class could be refactored into two classes, one (ExtendableListPreference 
	 * being a subclass of ListPreference) which allows to run code and an alert dialog 
	 * before showing it's own dialog and another one (PasswordProtectedListPreference being
	 * a subclass of the first) implementing the password alert dialog.
	 * 
	 * Apply this scheme also to PasswordProtectedCheckBoxPreference and
	 * PasswordProtectedEditTextPreference.
	 */
	
	// TODO: move password alert related code to helper class as it is also needed in other classes
	
	ViewGroup parent;
	private boolean passwordEnabled = true;
	private AlertDialog helpAlert;
	private Runnable immediateRunnable;
	private AlertDialog immediateAlert;
	private boolean immediateAlertCanceled = false;
	
	
	public PasswordProtectedListPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PasswordProtectedListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private void showWrongPasswordDialog(){
		new AlertDialog.Builder(getContext())
		.setMessage(R.string.pw_dialog_wrong_pw)
		.show();
	}
	
	/**
	 * Determines if user has to type in password.
	 * This change will be only active during the next click performed on this Preference.
	 * After that it will be set to the default value <code>true</code>.
	 * @param passwordEnabled
	 */
	public void setPasswordEnabled(boolean passwordEnabled){
		this.passwordEnabled  = passwordEnabled;
	}
	
	/**
	 * You can pass a Runnable to this method which will be executed *after* the password was typed in correctly
	 * and *before* this Preference's dialog has been shown. You can use this to run additional code, e.g. to modify
	 * the Preference dialog.
	 * 
	 * If you want to display an alert dialog and want the application to wait until it's dismissed, simply pass the
	 * alert dialog, too - otherwise pass <code>null</code>. In the former case don't forget to call the alert's
	 * <code>show()</code> method in the Runnable, otherwise the Preference's list dialog will never been displayed.
	 * Additionally the list dialog won't be shown if your alert was canceled. 
	 * @param run
	 * @param immediateAlert
	 */
	public void setImmediateProcessing(Runnable run, AlertDialog immediateAlert){
		this.immediateRunnable = run;
		this.immediateAlert = immediateAlert;
	}
	
	private void createHelpAlertDialog(){
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		final EditText input = new EditText(getContext());
		input.requestFocus();
		input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		input.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
		layout.setPadding(10, 0, 10, 0);
		layout.addView(input);
		
		helpAlert = new AlertDialog.Builder(getContext())
		.setTitle(R.string.pw_dialog_title)
		.setMessage(R.string.pw_dialog_descr)
		.setView(layout)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String pw = input.getText().toString();
				if (pw != null && pw.equals(getSharedPreferences().getString("pref_password", null))){
					showImmediateAlert();
				} else {
					showWrongPasswordDialog();
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).create();
	}
	
	@Override
	protected void onClick() {
		if (passwordEnabled){
			// create a new help alert dialog so that it's started in fresh state without password being already filled in
			createHelpAlertDialog();
			
			// this will also trigger the immediate alert and the Preference's list dialog afterwards
			helpAlert.show();
		} else {
			// this will also trigger the Preference's list dialog afterwards
			showImmediateAlert();
			setPasswordEnabled(true);
		}
	}
	
	private void createAndShowListDialog() {
		new AlertDialog.Builder(getContext())
		.setTitle(getDialogTitle())
		.setItems(getEntries(), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String newValue = getEntryValues()[which].toString();
				setValue(newValue);
				notifyChanged(); 
			}
		}).show();
	}

	private void showImmediateAlert() {
		if (immediateRunnable != null){
			if (immediateAlert != null) {
				immediateAlert.setOnDismissListener(new DialogInterface.OnDismissListener(){
					
					public void onDismiss(DialogInterface dialog) {
						if (!immediateAlertCanceled){
							createAndShowListDialog();
						} else {
							immediateAlertCanceled = false;
						}
					}
					
				});
				immediateAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {
						immediateAlertCanceled = true;
					}
				});
			}
			immediateRunnable.run();
		} else {
			createAndShowListDialog();
		}
	}
}
