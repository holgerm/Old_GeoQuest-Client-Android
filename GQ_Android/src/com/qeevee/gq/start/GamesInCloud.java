package com.qeevee.gq.start;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
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
		AsyncTask<HostConnector, Integer, List<GameDescription>> loadGameList = new GetGameList();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			loadGameList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					publicHostConnector, gq_internalHostConnector);
		else
			loadGameList.execute(publicHostConnector, gq_internalHostConnector);
	}

	private class GetGameList extends
			AsyncTask<HostConnector, Integer, List<GameDescription>> {

		@Override
		protected List<GameDescription> doInBackground(HostConnector... params) {
			List<GameDescription> collectedGames = new ArrayList<GameDescription>();
			List<GameDescription> curHostGames;
			for (HostConnector connector : params) {
				curHostGames = connector.getGameList();
				for (GameDescription curOtherGameDescription : curHostGames) {
					boolean alreadyInList = false;
					for (GameDescription existingGameDescription : collectedGames) {
						if (existingGameDescription.getID().equals(
								curOtherGameDescription.getID())) {
							alreadyInList = true;
							break;
						}
					}
					if (!alreadyInList)
						collectedGames.add(curOtherGameDescription);
				}
			}
			return collectedGames;
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
