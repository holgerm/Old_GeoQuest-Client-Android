package com.qeevee.gq.start;

import java.util.ArrayList;
import java.util.List;

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

import com.qeevee.gq.GeoQuestActivity;
import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;
import com.qeevee.gq.host.HostConnector;

public class GamesInCloud extends GeoQuestActivity {

	public static final String TAG = GamesInCloud.class.getCanonicalName();
	private ListView listView;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamesincloud);

		listView = (ListView) findViewById(R.id.listview);
		titleView = (TextView) findViewById(R.id.titleGamesList);
		titleView.setText(R.string.titleGamesInCloud);

		AsyncTask<Void, Integer, List<GameDescription>> loadGameList = new GetGameList();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			loadGameList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			loadGameList.execute();

	}

	private class GetGameList extends
			AsyncTask<Void, Integer, List<GameDescription>> {

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
			progressDialog.setTitle(GeoQuestApp.getContext().getText(
					R.string.dialogGetGameListTitle));
			progressDialog.setMessage(GeoQuestApp.getContext().getText(
					R.string.wait));
			progressDialog.setIcon(R.drawable.app_item_icon);
		}

		@Override
		protected List<GameDescription> doInBackground(Void... params) {
			List<GameDescription> gamesList = new ArrayList<GameDescription>();
			gamesList.addAll(HostConnector.getGamesList());
			return gamesList;
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
