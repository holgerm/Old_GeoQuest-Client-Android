package edu.bonn.mobilegaming.geoquest;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SortingOptionsActivity extends ListActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sortoptionslist);
			
		((TextView) findViewById(R.id.sortOptionsHeader))
				.setText(getText(R.string.sortoptionsHeaderStart));
		
		List<String> options = new ArrayList<String>();
		options.add(getText(R.string.sortByName).toString());
		options.add(getText(R.string.sortByDate).toString());
		options.add(getText(R.string.sortByDistance).toString());
		
		//not implemented yet
//		options.add(getText(R.string.sortByLastPlayed).toString());
		
		ListAdapter sortOptionsListAdapter = new ArrayAdapter<String>(
				SortingOptionsActivity.this, R.layout.game_item,
				options);
		setListAdapter(sortOptionsListAdapter);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		GeoQuestApp.currentSortMode = position;		
		SharedPreferences prefs = GeoQuestApp.getContext().getSharedPreferences(GeoQuestApp.MAIN_PREF_FILE_NAME, Context.MODE_PRIVATE);
		prefs.edit().putInt(Preferences.PREF_KEY_SORTING_MODE, position).commit();
		finish();
	}
}
