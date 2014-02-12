package com.qeevee.gq.start;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.qeevee.gq.game.DownloadGame;
import com.qeevee.gq.game.GameDescription;
import com.qeevee.gq.host.HostConnector;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class GameList extends Activity {

	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_list);

		listView = (ListView) findViewById(R.id.listview);

		final HostConnector connector = GeoQuestApp.getHostConnector();
		List<GameDescription> games = connector.getGameList();
		GameListAdapter listAdapter = new GameListAdapter(this,
				android.R.layout.simple_list_item_1, games);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				new DownloadGame().execute((GameDescription) parent
						.getItemAtPosition(position));
				Toast.makeText(
						GameList.this,
						(CharSequence) connector
								.getDownloadURL(((GameDescription) parent
										.getItemAtPosition(position)).getID()),
						Toast.LENGTH_LONG).show();
			}

		});
	}

}
