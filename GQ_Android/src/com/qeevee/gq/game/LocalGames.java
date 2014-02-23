package com.qeevee.gq.game;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.bonn.mobilegaming.geoquest.R;

public class LocalGames extends Activity {

	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_list);

		listView = (ListView) findViewById(R.id.listview);

		List<GameDescription> games = GameDataManager.getGameDescriptions();
		GameListAdapter listAdapter = new GameListAdapter(this,
				android.R.layout.simple_list_item_1, games);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				new StartLocalGame().execute((GameDescription) parent
						.getItemAtPosition(position));
			}

		});
	}

}
