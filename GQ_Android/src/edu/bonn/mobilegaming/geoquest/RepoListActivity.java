package edu.bonn.mobilegaming.geoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.bonn.mobilegaming.geoquest.ui.MenuMaker;

public class RepoListActivity extends GeoQuestListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "GeoQuestListActivity";

	@Override
	// TODO perhaps we should move most of this function to onResume() (hm)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repolist);

		((TextView) findViewById(R.id.repolistHeader))
				.setText(getText(R.string.start_repoList_header));

		ListAdapter repoListAdapter = new ArrayAdapter<String>(
				RepoListActivity.this, R.layout.game_item,
				GeoQuestApp.getNotEmptyRepositoryNamesAsList());
		setListAdapter(repoListAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String nameOfSelectedRepo = (String) getListView().getItemAtPosition(
				position);
		Intent intent = new Intent(GeoQuestApp.getContext(),
				edu.bonn.mobilegaming.geoquest.GameListActivity.class);
		intent.putExtra("edu.bonn.mobilegaming.geoquest.REPO",
				nameOfSelectedRepo);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menuMaker.addMenuItem(MenuMaker.RELOAD_GAMES_MENU_ID,
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						Intent i = new Intent(GeoQuestApp.getContext(),
								edu.bonn.mobilegaming.geoquest.Start.class);
						i.putExtra(Start.RELOAD_REPO_DATA, true);
						i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						setResult(RESULT_OK, i);
						finish();
						return false;
					}
				});
		menuMaker.setupMenu(menu);
		return true;
	}

}
