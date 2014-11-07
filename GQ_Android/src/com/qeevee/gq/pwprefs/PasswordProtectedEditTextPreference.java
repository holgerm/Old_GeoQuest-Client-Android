package com.qeevee.gq.pwprefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.qeevee.gqtours.R;

public class PasswordProtectedEditTextPreference extends EditTextPreference {
	
	private boolean passwordEnabled = true;
	
	public PasswordProtectedEditTextPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PasswordProtectedEditTextPreference(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PasswordProtectedEditTextPreference(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	private void callSuperOnClick(){
		super.onClick();
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
	 * Overwrite this method to do some preprocessing before asking for the password and displaying
	 * the Preferences' dialog. Calling <code>super.doPreProcessing()</code> is not necessary as the
	 * default implementation does simply nothing.
	 */
	public void doPreProcessing(){
		/* TODO: maybe replace this by a setPreProcessing(Runnable run) method,
		 * whose getter is called by onClick(), so we don't have this empty method stub
		 */
	}
		
	@Override
	protected void onClick() {
		doPreProcessing();
		if (passwordEnabled){
			LinearLayout layout = new LinearLayout(getContext());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER_HORIZONTAL);
			final EditText input = new EditText(getContext());
			input.requestFocus();
			input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
			layout.setPadding(10, 0, 10, 0);
			layout.addView(input);
			
			new AlertDialog.Builder(getContext())
			.setTitle(R.string.pw_dialog_title)
			.setMessage(R.string.pw_dialog_descr)
			.setView(layout)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String pw = input.getText().toString();
					if (pw != null && pw.equals(getSharedPreferences().getString("pref_password", null))){
						callSuperOnClick();
					} else {
						showWrongPasswordDialog();
					}
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			}).show();
		} else {
			super.onClick();
			setPasswordEnabled(true);
		}
	}

}
