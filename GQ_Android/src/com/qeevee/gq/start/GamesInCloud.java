package com.qeevee.gq.start;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// TODO: make cancellable
			progressDialog = ProgressDialog.show(GamesInCloud.this,
					"Downloading ...", "Please wait.", true, true,
					new OnCancelListener() {

						public void onCancel(DialogInterface dialog) {
							// cancel:
							GetGameList.this.cancel(true);
							GamesInCloud.this.finish();
						}

					});
		}

		@Override
		protected List<GameDescription> doInBackground(HostConnector... params) {
			List<GameDescription> collectedGames = new ArrayList<GameDescription>();
			List<GameDescription> curHostGames;
			progressDialog.setTitle(GeoQuestApp.getContext().getText(
					R.string.dialogGetGameListTitle));
			progressDialog.setMessage(GeoQuestApp.getContext().getText(
					R.string.wait));
			progressDialog.setIcon(R.drawable.gqlogo_solo_trans);

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
					listView.setEnabled(false);
					DownloadGame downloader = new DownloadGame(
							GamesInCloud.this);
					downloader.execute((GameDescription) parent
							.getItemAtPosition(position));
				}

			});
			progressDialog.dismiss();

			listView.invalidate();
		}

	}

	public void reenable() {
		listView.setEnabled(true);
	}

}
