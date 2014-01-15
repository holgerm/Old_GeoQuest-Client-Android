package edu.bonn.mobilegaming.geoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;

import edu.bonn.mobilegaming.geoquest.ui.InteractionBlocker;
import edu.bonn.mobilegaming.geoquest.ui.InteractionBlockingManager;
import edu.bonn.mobilegaming.geoquest.ui.MenuMaker;

public abstract class GeoQuestMapActivity extends MapActivity implements
		MissionOrToolActivity {
	/**
	 * Declare Menu ID Constants
	 */
	static final private int QUIT_MENU_ID = Menu.FIRST;
	static final private int END_GAME_MENU_ID = Menu.FIRST + 1;
	static final private int PREFS_MENU_ID = Menu.FIRST + 2;
	protected static final int MENU_ID_OFFSET = Menu.FIRST + 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ibm = new InteractionBlockingManager(this);
		((GeoQuestApp) getApplication()).addActivity(this);
	}

	protected MenuMaker menuMaker = new MenuMaker();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menuMaker.addMenuItems(MenuMaker.END_GAME_MENU_ID,
				MenuMaker.QUIT_MENU_ID, MenuMaker.IMPRINT_MENU_ID);
		menuMaker.setupMenu(menu);
		menu.add(0, PREFS_MENU_ID, 0, R.string.prefsMenu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case QUIT_MENU_ID:
			GeoQuestApp.getInstance().terminateApp();
			break;
		case END_GAME_MENU_ID:
			GeoQuestApp.getInstance().endGame();
			break;
		case PREFS_MENU_ID:
			Intent settingsActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(settingsActivity);
			break;
		}
		return super.onOptionsItemSelected(item);
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
