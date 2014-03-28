package edu.bonn.mobilegaming.geoquest;

import org.dom4j.Element;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.qeevee.gq.history.HistoryActivity;

import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;

public abstract class GeoQuestMapActivity extends MapActivity implements
		MissionOrToolActivity {

	protected String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GeoQuestApp.setCurrentActivity(this);
		ibm = new InteractionBlockingManager(this);
		((GeoQuestApp) getApplication()).addActivity(this);

		// get extras
		Bundle extras = getIntent().getExtras();
		id = extras.getString("missionID");
		setBackAllowed(extras.getBoolean(Mission.BACK_ALLOWED, false));
	}

	private boolean keepsActivity = false;

	public void setKeepActivity(boolean keep) {
		keepsActivity = keep;
	}

	public boolean keepsActivity() {
		return keepsActivity;
	}

	private boolean backAllowed;

	public void setBackAllowed(boolean backAllowed) {
		this.backAllowed = backAllowed;
	}

	public boolean isBackAllowed() {
		return backAllowed;
	}

	@Override
	public void onBackPressed() {
		if (isBackAllowed())
			super.onBackPressed();
	}

	public Element getXML() {
		return Mission.get(id).xmlMissionNode;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_basic, menu);
		inflater.inflate(R.menu.menu_gqactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_imprint:
			GeoQuestApp.getInstance().showImprint();
			return true;
		case R.id.menu_preferences:
			Intent settingsActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(settingsActivity);
			return true;
		case R.id.menu_endGame:
			GeoQuestApp.getInstance().endGame();
			return true;
		case R.id.menu_history:
			Intent historyActivity = new Intent(getBaseContext(),
					HistoryActivity.class);
			startActivity(historyActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		GeoQuestApp.setCurrentActivity(this);
	}

	protected void onDestroy() {
		if (isFinishing()) {
			GeoQuestApp.getInstance().removeActivity(this);
		}
		super.onDestroy();
	}

	protected InteractionBlockingManager ibm;

	public BlockableAndReleasable blockInteraction(InteractionBlocker newBlocker) {
		return ibm.blockInteraction(newBlocker);
	}

	public void releaseInteraction(InteractionBlocker blocker) {
		ibm.releaseInteraction(blocker);
	}

}
