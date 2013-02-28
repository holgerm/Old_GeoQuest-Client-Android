package edu.bonn.mobilegaming.geoquest;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.qeevee.util.FileOperations;
import com.qeevee.util.locationmocker.LocationSource;

import edu.bonn.mobilegaming.geoquest.gameaccess.GameDataManager;
import edu.bonn.mobilegaming.geoquest.pwprefs.PasswordProtectedDialogPreference;
import edu.bonn.mobilegaming.geoquest.pwprefs.PasswordProtectedListPreference;

public class Preferences extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	// TODO: add additional preference Strings as constants
	public static final String PREF_KEY_LAST_USED_REPOSITORY = "repositoryName";
	public static final String PREF_KEY_LAST_PLAYED_GAME_NAME = "gameName";
	public static final String PREF_KEY_LAST_PLAYED_GAME_FILE_NAME = "gameFileName";
	public static final String PREF_KEY_AUTO_START_GAME = "pref_auto_start_game";
	public static final String PREF_KEY_AUTO_START_REPO = "auto_start_repo";
	public static final String PREF_KEY_AUTO_START_GAME_CHECK = "pref_auto_start_game_check";
	public static final String PREF_KEY_PASSWORD = "pref_password";
	public static final String PREF_KEY_SERVER_URL = "pref_server_url";
	public static final String PREF_KEY_CLEAN_SDCARD = "pref_clean_sdcard";
	public static final String PREF_KEY_SORTING_MODE = "pref_sorting_mode";

	private SharedPreferences mainPrefs;
	private boolean clickAutoStartQuestChooser = false;
	// private boolean checkAutoStartGameSet = false;

	private final String TAG = Preferences.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		mainPrefs = this.getPreferenceScreen().getSharedPreferences();
		mainPrefs.registerOnSharedPreferenceChangeListener(this);

		// Additionally we use specific prefs for storing usage information:
		// SharedPreferences gamePrefs =
		GeoQuestApp.getContext().getSharedPreferences(
				GeoQuestApp.MAIN_PREF_FILE_NAME, Context.MODE_PRIVATE);

		// prepare auto-start-quest preference
		PasswordProtectedListPreference questPref = (PasswordProtectedListPreference) findPreference(PREF_KEY_AUTO_START_GAME);
		prepareRepoListForAutoStartQuest(questPref);
		if (questPref.getValue() != null) {
			questPref.setTitle(questPref.getValue());
		}

		PasswordProtectedDialogPreference deleteGameDataPref = (PasswordProtectedDialogPreference) findPreference(PREF_KEY_CLEAN_SDCARD);
		if (GeoQuestApp.getInstance().isInGame()) {
			deleteGameDataPref.setEnabled(false);
		}
		deleteGameDataPref
				.setOnClickListener(new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							if (FileOperations.deleteDirectory(GameDataManager
									.getLocalRepoDir(null))) {
								Toast.makeText(
										Preferences.this,
										R.string.pref_clean_sdcard_toast_success,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(Preferences.this,
										R.string.pref_clean_sdcard_toast_error,
										Toast.LENGTH_LONG).show();
								// TODO: handle this case (i.e. tell the user
								// how to manually delete the folder or
								// automatically restore the files)
							}
						}
					}
				});
	}

	@Override
	protected void onResume() {
		if (!mainPrefs.contains(Preferences.PREF_KEY_PASSWORD)) {
			promptForPassword();
		}
		super.onResume();
	}

	private void promptForPassword() {
		// TODO: refactor Password-Dialog code in new class
		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.pref_password_dialog_title)
				.setMessage(R.string.pref_password_dialog_message)
				.setCancelable(true)
				.setOnCancelListener(new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						finish();
					}
				}).create();

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);

		final EditText password1 = new EditText(this);
		password1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		password1
				.setTransformationMethod(android.text.method.PasswordTransformationMethod
						.getInstance());
		final EditText password2 = new EditText(this);
		password2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		password2
				.setTransformationMethod(android.text.method.PasswordTransformationMethod
						.getInstance());

		Button btnOk = new Button(this);
		btnOk.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String pw1 = password1.getText().toString();
				String pw2 = password2.getText().toString();
				if (pw1.equals(pw2)) {
					if (pw1.equals("")) {
						Toast.makeText(Preferences.this,
								R.string.pref_password_dialog_error_empty_pw,
								Toast.LENGTH_LONG).show();
					} else {
						mainPrefs.edit().putString("pref_password", pw1)
								.commit();
						alertDialog.dismiss();
					}
				} else {
					password2.requestFocus();
					Toast.makeText(Preferences.this,
							R.string.pref_password_dialog_error_pws_not_equal,
							Toast.LENGTH_LONG).show();
				}
			}
		});
		btnOk.setText(R.string.ok);
		btnOk.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		Button btnCancel = new Button(this);
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				alertDialog.dismiss();
				finish();
			}
		});
		btnCancel.setText(R.string.cancel);
		btnCancel.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));

		LinearLayout btnLayout = new LinearLayout(this);
		btnLayout.setOrientation(LinearLayout.HORIZONTAL);
		btnLayout.setGravity(Gravity.CENTER_VERTICAL);
		btnLayout.addView(btnOk);
		btnLayout.addView(btnCancel);

		layout.setPadding(10, 0, 10, 0);
		layout.addView(password1);
		layout.addView(password2);
		layout.addView(btnLayout);
		alertDialog.setView(layout);

		alertDialog.show();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && clickAutoStartQuestChooser) {
			clickAutoStartQuestChooser = false;
			PasswordProtectedListPreference questPref = (PasswordProtectedListPreference) findPreference(PREF_KEY_AUTO_START_GAME);
			prepareRepoListForAutoStartQuest(questPref);
			questPref.setPasswordEnabled(false);
			// checkAutoStartGameSet = true;
			int order = getCurrentPreferenceCount(
					PasswordProtectedListPreference.class,
					PREF_KEY_AUTO_START_GAME);
			if (order != -1) {
				getPreferenceScreen().onItemClick(null, null, order, 0);
			} else {
				Log.i("Geoquest#" + TAG,
						"key pref_key_auto_start_game not found in preferences");
			}
			// TODO: check if preferences quest preference is set - hasFocus
			// code below runs to early therefore commented out
			// clickAutoStartQuestChooser = false;
		}
		// if (hasFocus && checkAutoStartGameSet) {
		// if (!mainPrefs.contains(PREF_KEY_AUTO_START_GAME) ||
		// !mainPrefs.contains(PREF_KEY_AUTO_START_REPO)) {
		// PasswordProtectedCheckBoxPreference questCheckPref =
		// (PasswordProtectedCheckBoxPreference)
		// findPreference(PREF_KEY_AUTO_START_GAME_CHECK);
		// questCheckPref.setPasswordEnabled(false);
		// int order =
		// getCurrentPreferenceCount(PasswordProtectedCheckBoxPreference.class,
		// PREF_KEY_AUTO_START_GAME_CHECK);
		// if (order != -1){
		// getPreferenceScreen().onItemClick(null, null, order, 0);
		// } else {
		// Log.i("Geoquest#" + TAG,
		// "key pref_key_auto_start_game_check not found in preferences");
		// }
		// checkAutoStartGameSet = false;
		// }
		// }
		super.onWindowFocusChanged(hasFocus);
	}

	@SuppressWarnings("rawtypes")
	private int getCurrentPreferenceCount(Class prefClass, String prefKey) {
		ListAdapter prefAdapter = getPreferenceScreen().getRootAdapter();
		int order = -1;
		int prefCount = prefAdapter.getCount();
		for (int i = 0; i < prefCount; i++) {
			Object item = prefAdapter.getItem(i);
			if (item.getClass().equals(prefClass)) {
				Preference pref = (Preference) item;
				if (pref.getKey().equals(prefKey)) {
					order = i;
				}
			}
		}
		return order;
	}

	private void prepareRepoListForAutoStartQuest(
			final PasswordProtectedListPreference questPref) {
		final AlertDialog immediateAlert = createRepoListDialog(questPref);
		Runnable run = new Runnable() {

			public void run() {
				immediateAlert.show();
			}
		};
		questPref.setImmediateProcessing(run, immediateAlert);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			final String key) {
		if (key.equals("isGPSMocking")) {
			if (sharedPreferences.getBoolean(key, false) == true) {
				String deviceName = sharedPreferences.getString(
						"deviceNameForGPSMocking", "");
				LocationSource.setDeviceName(getApplicationContext(),
						deviceName);
			}
		}
		if (key.equals("deviceNameForGPSMocking")) {
			if (sharedPreferences.getBoolean("isGPSMocking", false) == true) {
				String deviceName = sharedPreferences.getString(
						"deviceNameForGPSMocking", "DefaultName");
				LocationSource.setDeviceName(getApplicationContext(),
						deviceName);
			}
		}
		if (key.equals(PREF_KEY_AUTO_START_GAME_CHECK)
				&& sharedPreferences.getBoolean(PREF_KEY_AUTO_START_GAME_CHECK,
						false)
				&& !sharedPreferences.contains(PREF_KEY_AUTO_START_GAME)) {
			// start the repo/quest list selection if no repo has been selected
			// yet
			// the quest chooser preference will be clicked as soon as this
			// Activity is visible again
			clickAutoStartQuestChooser = true;
		}
		if (key.equals(PREF_KEY_AUTO_START_GAME)) {
			// recreate repo list alert dialog so it won't have it's old
			// selected state causing it not to be displayed
			PasswordProtectedListPreference questPref = (PasswordProtectedListPreference) findPreference(PREF_KEY_AUTO_START_GAME);
			prepareRepoListForAutoStartQuest(questPref);
			String autoStartQuestName = questPref.getValue();
			if (autoStartQuestName != null) {
				questPref.setTitle(autoStartQuestName);
			}
			Log.i("Geoquest", "pref_auto_start_game: " + autoStartQuestName);
		}
		if (key.equals(PREF_KEY_SERVER_URL)) {
//			PasswordProtectedEditTextPreference serverPref = (PasswordProtectedEditTextPreference) findPreference(PREF_KEY_SERVER_URL);
			String serverURL = sharedPreferences.getString(PREF_KEY_SERVER_URL,
					getString(R.string.geoquest_server_url));
			if (serverURL.equals("")) {
				serverURL = getString(R.string.geoquest_server_url);
			}
			mainPrefs.edit().putString(PREF_KEY_SERVER_URL, serverURL).commit();
		}
	}

	private AlertDialog createRepoListDialog(
			final PasswordProtectedListPreference questPref) {
		List<String> repoList = GeoQuestApp.getNotEmptyRepositoryNamesAsList();
		// TODO: check via GeoQuestApp.getInstance().isRepoDataAvailable()
		if (repoList == null || repoList.size() == 0) {
			// repo list not locally stored - download
			ProgressDialog downloadRepoDataDialog = new ProgressDialog(this);
			downloadRepoDataDialog
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			downloadRepoDataDialog.setCancelable(false);

			final GeoQuestProgressHandler downloadRepoDataHandler;

			downloadRepoDataDialog.setProgress(0);
			downloadRepoDataDialog.setMessage(GeoQuestApp.getContext().getText(
					R.string.start_fetchingDataFromServer));
			downloadRepoDataHandler = new GeoQuestProgressHandler(
					downloadRepoDataDialog,
					GeoQuestProgressHandler.LAST_IN_CHAIN, this);

			GeoQuestApp.loadRepoData(downloadRepoDataHandler);
			repoList = GeoQuestApp.getNotEmptyRepositoryNamesAsList();
		}
		final String[] repos = new String[repoList.size()];
		repoList.toArray(repos);
		AlertDialog immediateAlert = new AlertDialog.Builder(this)
				.setTitle(R.string.start_repoList_header)
				.setItems(repos, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String repo = repos[which];
						questPref.setDialogTitle(repo);
						List<String> questList = GeoQuestApp
								.getGameNamesForRepository(repo);
						String[] quests = new String[questList.size()];
						questList.toArray(quests);
						questPref.setEntries(quests);
						mainPrefs.edit()
								.putString(PREF_KEY_AUTO_START_REPO, repo)
								.commit();
						questPref.setEntryValues(quests);
					}
				}).create();
		return immediateAlert;
	}
}
