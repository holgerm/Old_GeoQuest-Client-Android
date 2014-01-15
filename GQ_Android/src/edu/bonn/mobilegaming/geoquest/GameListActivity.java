package edu.bonn.mobilegaming.geoquest;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.bonn.mobilegaming.geoquest.gameaccess.GameItem;

public class GameListActivity extends GeoQuestListActivity {

	private ListAdapter gameListAdapter;
	private ListAdapter gameListAdapterNoLoc;
	private ListAdapter gameListAdapterLoc;
	private ListView lv2;
	public CharSequence repoName;

	private final static boolean enable_long_click_auto_game = false;

	private GameListActivity getInstance() {
		return this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.repoName = getIntent().getCharSequenceExtra(
				"edu.bonn.mobilegaming.geoquest.REPO");

		// // Init data adapter - moved this code to onResume():
		// gameListAdapter = new ArrayAdapter<String>(this, R.layout.game_item,
		// GeoQuestApp.getGameNamesForRepository(repoName.toString()));
		// setListAdapter(gameListAdapter);

		if (enable_long_click_auto_game) { // remove this if-clause if you want
			// a context menu to be shown under
			// any circumstances
			registerForContextMenu(getListView());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (enable_long_click_auto_game) {
			menu.add(R.string.context_auto_start_game);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		if (item.getTitle().equals(getString(R.string.context_auto_start_game))) {
			// create and show password dialog
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER_HORIZONTAL);
			final EditText input = new EditText(this);
			input.setTransformationMethod(android.text.method.PasswordTransformationMethod
					.getInstance());
			layout.setPadding(10, 0, 10, 0);
			layout.addView(input);

			new AlertDialog.Builder(this)
					.setTitle(R.string.pw_dialog_title)
					.setMessage(R.string.pw_dialog_descr)
					.setView(layout)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String pw = input.getText().toString();
									if (pw != null
											&& pw.equals(PreferenceManager
													.getDefaultSharedPreferences(
															getInstance())
													.getString("pref_password",
															null))) {
										AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
												.getMenuInfo();
										String quest = (String) getListAdapter()
												.getItem(menuInfo.position);
										SharedPreferences mainPrefs = PreferenceManager
												.getDefaultSharedPreferences(getInstance());
										mainPrefs
												.edit()
												.putBoolean(
														Preferences.PREF_KEY_AUTO_START_GAME_CHECK,
														true).commit();
										mainPrefs
												.edit()
												.putString(
														Preferences.PREF_KEY_AUTO_START_REPO,
														repoName.toString())
												.commit();
										mainPrefs
												.edit()
												.putString(
														Preferences.PREF_KEY_AUTO_START_GAME,
														quest).commit();
										Toast.makeText(
												getInstance(),
												R.string.context_auto_start_game_toast,
												Toast.LENGTH_SHORT).show();
									} else {
										new AlertDialog.Builder(getInstance())
												.setMessage(
														R.string.pw_dialog_wrong_pw)
												.show();
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final String gameName = (String) l.getAdapter().getItem(position);
		final GameItem gameItem = GeoQuestApp.getGameItem(repoName, gameName);
		final String selectedRepo = repoName.toString();

		startGame(gameItem, selectedRepo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem sortItem = menu.add(R.string.game_list_sort);
		sortItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				Intent i = new Intent(
						GeoQuestApp.getContext(),
						edu.bonn.mobilegaming.geoquest.SortingOptionsActivity.class);
				startActivity(i);
				return false;
			}
		});
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		lv2 = null;

		if (GeoQuestApp.currentSortMode == GameItem.SORT_GAMELIST_BY_DISTANCE) {
			setContentView(R.layout.gamelistbydistance);
			String headerTextNoLoc = getText(R.string.start_gameList_headerPart)
					.toString()
					+ " \""
					+ this.repoName
					+ "\": "
					+ getText(R.string.start_gameList_headerPartNoLoc)
							.toString();
			((TextView) findViewById(R.id.gamelistHeaderNoLoc))
					.setText(headerTextNoLoc);

			String headerTextLoc = getText(R.string.start_gameList_headerPart)
					.toString()
					+ " \""
					+ this.repoName
					+ "\": "
					+ getText(R.string.start_gameList_headerPartLoc).toString();
			((TextView) findViewById(R.id.gamelistHeaderLoc))
					.setText(headerTextLoc);

			List<String> repoNamesWithoutLoc = GeoQuestApp
					.getGameNamesForRepositoryWithoutLocation(repoName
							.toString());
			List<String> repoNamesWithLoc = GeoQuestApp
					.getGameNamesForRepositoryWithLocation(repoName.toString());

			gameListAdapterNoLoc = new ArrayAdapter<String>(this,
					R.layout.game_item, repoNamesWithoutLoc);
			gameListAdapterLoc = new ArrayAdapter<String>(this,
					R.layout.game_item, repoNamesWithLoc);
			setListAdapter(gameListAdapterNoLoc);
			lv2 = (ListView) findViewById(android.R.id.list);
			lv2.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					onListItemClick((ListView) (arg0), arg1, arg2, arg2);
				}
			});
			lv2.setAdapter(gameListAdapterLoc);
		} else {
			setContentView(R.layout.gamelist);
			String headerText = getText(R.string.start_gameList_headerPart)
					.toString() + " \"" + this.repoName + "\":";
			((TextView) findViewById(R.id.gamelistHeader)).setText(headerText);

			// Init data adapter
			gameListAdapter = new ArrayAdapter<String>(this,
					R.layout.game_item,
					GeoQuestApp.getGameNamesForRepository(repoName.toString()));
			setListAdapter(gameListAdapter);
		}
	}
}