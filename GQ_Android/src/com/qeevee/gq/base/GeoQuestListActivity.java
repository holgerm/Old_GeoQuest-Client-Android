package com.qeevee.gq.base;

import com.qeevee.gqtours.R;
import com.qeevee.gqtours.R.id;
import com.qeevee.gqtours.R.menu;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class GeoQuestListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GeoQuestApp.setCurrentActivity(this);
		((GeoQuestApp) getApplication()).addActivity(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_basic, menu);
		if (GeoQuestApp.getInstance().isUsingAutostart()) {
			MenuItem endGameItem = menu.findItem(R.id.menu_endGame);
			if (endGameItem != null)
				endGameItem.setTitle(R.id.menu_quit);
		}
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

	@Override
	protected void onResume() {
		super.onResume();
		GeoQuestApp.setCurrentActivity(this);
	}

}
