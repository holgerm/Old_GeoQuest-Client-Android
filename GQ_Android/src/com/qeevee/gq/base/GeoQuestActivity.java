package com.qeevee.gq.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.qeevee.gqphka.R;
import com.qeevee.gqphka.R.id;
import com.qeevee.gqphka.R.menu;
import com.qeevee.gq.capability.NeedsNFCCapability;
import com.uni.bonn.nfc4mg.NFCEventManager;

import edu.bonn.mobilegaming.geoquest.contextmanager.ContextManager;

public abstract class GeoQuestActivity extends Activity {

	// @Override
	// public void finish() {
	// get
	// super.finish();
	// }
	//
	private NFCEventManager mNFCEventManager = null;

	@Override
	protected void onResume() {
		super.onResume();
		attachNFCEventListener();
		GeoQuestApp.setCurrentActivity(this);
	}

	private void attachNFCEventListener() {
		if (this instanceof NeedsNFCCapability && null != mNFCEventManager) {
			mNFCEventManager.attachNFCListener(GeoQuestActivity.this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		detachNFCEventListener();
	}

	private void detachNFCEventListener() {
		if (this instanceof NeedsNFCCapability && null != mNFCEventManager) {
			mNFCEventManager.removeNFCListener(GeoQuestActivity.this);
		}
	}

	public static ContextManager contextManager = null;

	static final String TAG = "GeoQuestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GeoQuestApp.setCurrentActivity(this);

		checkAndInitializeNFCEventManager();

		((GeoQuestApp) getApplication()).addActivity(this);
	}

	private void checkAndInitializeNFCEventManager() {
		if (this instanceof NeedsNFCCapability) {
			try {
				mNFCEventManager = NFCEventManager.getInstance(this);
				mNFCEventManager.initialize(this, GeoQuestActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_basic, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_debugMode).setChecked(
				GeoQuestApp.getInstance().isInDebugmode());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_info) {
			GeoQuestApp.getInstance().showInfo();
			return true;
		} else if (itemId == R.id.menu_imprint) {
			startActivity(new Intent(this, ImprintActivity.class));
			return true;
		} else if (itemId == R.id.menu_debugMode) {
			item.setChecked(!item.isChecked());
			GeoQuestApp.getInstance().setDebugMode(item.isChecked());
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onDestroy() {
		if (isFinishing()) {
			GeoQuestApp.getInstance().removeActivity(this);
		}
		super.onDestroy();
	}

}
