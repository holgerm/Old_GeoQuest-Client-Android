package com.qeevee.gq.start;

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.qeevee.gq.Configuration;
import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.Mission;


public class InvisibleStart extends Activity {

	private AsyncTask<Void, Integer, Void> extractionTask;
	private final String TAG = InvisibleStart.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// extract included games asynchronously:
		extractionTask = new ExtractGamesFromAssets().execute();

		boolean started = checkAndPerformAutostart();

		if (!started)
			started = startQuestFromQRCodeScan();
		if (!started) {
			Intent showLandingScreen = new Intent(getBaseContext(),
					LandingScreen.class);
			startActivity(showLandingScreen);
		}
	}

	/**
	 * If an autostart quest is defined in assets, i.e. precompiled with the
	 * app, this is called and no other quest can be started with this app.
	 * 
	 * If the user has set an autostart quest in the preferences (which is only
	 * possible if no autostart quest is set in the assets), it will be started.
	 * This setting can only after the start be changed again.
	 */
	private boolean checkAndPerformAutostart() {
		boolean isUsingAutoStart = checkAndPerformAutostartByAssets();
		GeoQuestApp.getInstance().setUsingAutostart(isUsingAutoStart);
		Mission.setMainActivity(this);
		return isUsingAutoStart;
	}

	private boolean checkAndPerformAutostartByAssets() {
		boolean autostart = false;
		String autostartGameID = Configuration
				.get(Configuration.CONFIG_AUTOSTART);
		if (autostartGameID != null) {
			// in case we autostart, we might have to wait for extraction of the
			// game:
			try {
				extractionTask.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (GameDataManager.existsLocalQuest(autostartGameID)) {
				autostart = true;
				File gameDir = GameDataManager.getQuestDir(autostartGameID);
				GameDescription gameDescr = new GameDescription(gameDir);
				// Start quest:
				AsyncTask<GameDescription, Integer, Boolean> startLocalGame = new StartLocalGame();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					startLocalGame.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, gameDescr);
				else
					startLocalGame.execute(gameDescr);
			}
		}
		return autostart;
	}

	private boolean startQuestFromQRCodeScan() {
		Intent startingIntent = getIntent();
		String startString = null;
		if (startingIntent == null)
			return false;

		startString = startingIntent.getDataString();
		if (startString == null)
			return false;

		Log.d(TAG, "started with intent. Received: " + startString);
		int indexOfGameID = startString.lastIndexOf('/');
		if (indexOfGameID == -1)
			return false;

		String gameID = startString.substring(indexOfGameID);
		if (GameDataManager.existsLocalQuest(gameID)) {
			File gameDir = GameDataManager.getQuestDir(gameID);
			GameDescription gameDescr = new GameDescription(gameDir);
			new StartLocalGame().execute(gameDescr);
			return true;
		}

		return false;
	}
}
