package edu.bonn.mobilegaming.geoquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
}
