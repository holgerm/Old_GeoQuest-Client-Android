package com.qeevee.gq.start;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qeevee.gq.GeoQuestActivity;
import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;
import com.qeevee.gq.ui.UIFactory;
import com.qeevee.util.Dialogs;
import com.qeevee.util.FileOperations;

public class LocalGames extends GeoQuestActivity {

	private ListView listView;
	private GameListAdapter listAdapter;
	private TextView titleView;
	private List<GameDescription> games;

	@SuppressWarnings("unused")
	private Class<? extends UIFactory> predefinedUIFactory = null;

	/**
	 * Used for test in order to set a test ui factory.
	 * 
	 * @param uiFactories
	 */
	public void setPredefinedUIFactories(
			Class<? extends UIFactory>... uiFactories) {
		predefinedUIFactory = uiFactories[0];
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localgames);

		listView = (ListView) findViewById(R.id.listview);
		titleView = (TextView) findViewById(R.id.titleGamesList);
		titleView.setText(R.string.titleLocalGames);

		games = GameDataManager.getGameDescriptions();
		listAdapter = new GameListAdapter(this,
				android.R.layout.simple_list_item_1, games);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				AsyncTask<GameDescription, Integer, Boolean> startLocalGame = new StartLocalGame();
				GameDescription gameDescToStart = (GameDescription) parent
						.getItemAtPosition(position);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					startLocalGame.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, gameDescToStart);
				else
					startLocalGame.execute(gameDescToStart);
			}

		});
	}

	/**
	 * Called when the activity's options menu needs to be created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_local_games, menu);
		return true;
	}

	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_DeleteAllQUests) {
			showDialog(Dialogs.DIALOG_DELETE_ALL);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Dialogs.DIALOG_DELETE_ALL:
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.deleteAllDialogTitle);
			builder.setCancelable(true);
			builder.setPositiveButton(
					GeoQuestApp.getContext().getText(R.string.yes),
					new DeleteAllGamesOnClickListener());
			builder.setNegativeButton(
					GeoQuestApp.getContext().getText(R.string.no),
					Dialogs.cancelOnClickListener);
			AlertDialog dialog = builder.create();
			dialog.show();
			return dialog;
		default:
			return super.onCreateDialog(id);
		}
	}

	private final class DeleteAllGamesOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			deleteAllGames();
		}
	}

	private void deleteAllGames() {
		File questsDir = GameDataManager.getQuestsDir();
		FileOperations.deleteDirectory(questsDir);
		questsDir.mkdirs();
		refresh();
	}

	public void refresh() {
		File questsDir = GameDataManager.getQuestsDir();
		if (questsDir != null && questsDir.exists()) {
			games = GameDataManager.getGameDescriptions();
			listAdapter = new GameListAdapter(this,
					android.R.layout.simple_list_item_1, games);
			listView.setAdapter(listAdapter);
		}

	}
}
