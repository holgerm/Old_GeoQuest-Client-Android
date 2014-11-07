package com.qeevee.gq.base;

import org.dom4j.Element;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.qeevee.gqtours.R;
import com.qeevee.gqtours.R.id;
import com.qeevee.gqtours.R.menu;
import com.qeevee.gqtours.R.string;
import com.qeevee.gq.ui.InteractionBlocker;
import com.qeevee.gq.ui.InteractionBlockingManager;
import com.qeevee.util.Dialogs;

public abstract class GeoQuestMapActivity extends MapActivity implements
		MissionOrToolActivity {

	protected String id;

	public String getMissionID() {
		return id;
	}

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
		} else if (itemId == R.id.menu_endGame) {
			showDialog(Dialogs.DIALOG_END_GAME);
			return true;
		} else if (itemId == R.id.menu_debugMode) {
			item.setChecked(!item.isChecked());
			GeoQuestApp.getInstance().setDebugMode(item.isChecked());
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Dialogs.DIALOG_END_GAME:
			Dialog dialog = Dialogs.createYesNoDialog(this,
					R.string.dialogEndGameTitle,
					Dialogs.endGameOnClickListener,
					Dialogs.cancelOnClickListener);
			dialog.show();
			return dialog;
		default:
			return super.onCreateDialog(id);
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
