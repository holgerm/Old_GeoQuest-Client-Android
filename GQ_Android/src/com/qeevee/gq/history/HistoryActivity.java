package com.qeevee.gq.history;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class HistoryActivity extends ListActivity {
	private static final String TAG = HistoryActivity.class.getSimpleName();

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setListAdapter(new HistoryListAdapter());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStop()");
		super.onStop();
	}

}
