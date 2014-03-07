package com.qeevee.gq.game;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
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
		setContentView(R.layout.gamesincloud);

		listView = (ListView) findViewById(R.id.listview);
		titleView = (TextView) findViewById(R.id.titleGamesList);
		titleView.setText(R.string.titleGamesInCloud);

		final HostConnector publicHostConnector = GeoQuestApp
				.getHostConnector();
		final HostConnector gq_internalHostConnector = new HostConnector(1);
		new GetGameList()
				.execute(publicHostConnector, gq_internalHostConnector);
	}

	private class GetGameList extends
			AsyncTask<HostConnector, Integer, List<GameDescription>> {

		@Override
		protected List<GameDescription> doInBackground(HostConnector... params) {
			List<GameDescription> games = new ArrayList<GameDescription>();
			for (HostConnector connector : params) {
				games.addAll(connector.getGameList());
			}
			return games;
		}

		@Override
		protected void onPostExecute(List<GameDescription> games) {
			GameListAdapter listAdapter = new GameListAdapter(
					GamesInCloud.this, R.layout.list_item, games);

			listView.setAdapter(listAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					new DownloadGame().execute((GameDescription) parent
							.getItemAtPosition(position));
				}

			});

			listView.invalidate();
		}

	}

}
