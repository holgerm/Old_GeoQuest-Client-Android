package com.qeevee.gq.game;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qeevee.gq.host.HostConnector;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class GamesInCloud extends Activity {

	private ListView listView;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_list);

		listView = (ListView) findViewById(R.id.listview);
		titleView = (TextView) findViewById(R.id.titleGamesList);
		titleView.setText(R.string.titleGamesInCloud);

		final HostConnector connector = GeoQuestApp.getHostConnector();
		List<GameDescription> games = connector.getGameList();
		GameListAdapter listAdapter = new GameListAdapter(this,
				R.layout.list_item, games);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				new DownloadGame().execute((GameDescription) parent
						.getItemAtPosition(position));
			}

		});
	}

}
