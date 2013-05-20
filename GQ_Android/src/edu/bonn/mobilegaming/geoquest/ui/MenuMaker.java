package edu.bonn.mobilegaming.geoquest.ui;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

/**
 * This class configures options menu items. It statically defines text and
 * images for each menu entry that might occur. Additionally it defines the
 * behavior for those menu items for which this is statically possible. You
 * might need to define specific behavior and register it with the method
 * {@link #addMenuItem(int, OnMenuItemClickListener)}.
 * 
 * @author muegge
 * 
 */
public class MenuMaker {

	protected static final String TAG = MenuMaker.class.getSimpleName();

	/**
	 * Declare Menu ID Constants
	 */
	public static final int QUIT_MENU_ID = 1;
	public static final int END_GAME_MENU_ID = 2;
	public static final int PREFS_MENU_ID = 3;
	public static final int HISTORY_MENU_ID = 4;
	public static final int RELOAD_GAMES_MENU_ID = 5;
	public static final int IMPRINT_MENU_ID = 6;

	private static SparseIntArray menuItemTextResources = new SparseIntArray();
	private static SparseIntArray menuItemDrawables = new SparseIntArray();
	private static SparseArray<OnMenuItemClickListener> standardMenuItemClickListeners = new SparseArray<OnMenuItemClickListener>();

	static {
		menuItemTextResources.put(QUIT_MENU_ID, R.string.quitMenu);
		menuItemDrawables.put(QUIT_MENU_ID, R.drawable.icon_quit);
		standardMenuItemClickListeners.put(QUIT_MENU_ID,
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						GeoQuestApp.getInstance().terminateApp();
						return false;
					}

				});
		menuItemTextResources.put(END_GAME_MENU_ID, R.string.endGameMenu);
		menuItemDrawables.put(END_GAME_MENU_ID, R.drawable.icon_end_game);
		standardMenuItemClickListeners.put(END_GAME_MENU_ID,
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						GeoQuestApp.getInstance().endGame();
						return false;
					}

				});
		menuItemTextResources.put(IMPRINT_MENU_ID, R.string.imprintMenu);
		menuItemDrawables.put(IMPRINT_MENU_ID, R.drawable.icon_imprint);
		standardMenuItemClickListeners.put(IMPRINT_MENU_ID,
				new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								(Context) GeoQuestApp.getCurrentActivity());
						builder.setTitle(R.string.imprintTitle);
						builder.setPositiveButton(R.string.ok,
								new OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.setIcon(R.drawable.geoquest_appicon);
						builder.setMessage("Hier folgt das Impressum");
						builder.show();
						return false;
					}

				});
		menuItemTextResources.put(PREFS_MENU_ID, R.string.prefsMenu);
		menuItemDrawables.put(PREFS_MENU_ID, R.drawable.icon_prefs);
		menuItemTextResources.put(HISTORY_MENU_ID, R.string.historyMenu);
		menuItemDrawables.put(HISTORY_MENU_ID, R.drawable.icon_history);
		menuItemTextResources.put(RELOAD_GAMES_MENU_ID,
				R.string.reloadGamesMenu);
		menuItemDrawables.put(RELOAD_GAMES_MENU_ID, R.drawable.icon_reload);
	}

	private static OnMenuItemClickListener NULL_OMICL = new OnMenuItemClickListener() {

		public boolean onMenuItemClick(MenuItem item) {
			Log.d(TAG,
					"Menu item function not set for item nr. "
							+ item.getItemId());
			return true;
		}

	};

	private Vector<Integer> menuIDs = new Vector<Integer>();
	private SparseArray<OnMenuItemClickListener> specificMenuItemClickListeners = new SparseArray<OnMenuItemClickListener>();

	/**
	 * Adds one or more menu items with statically predefined behavior.
	 * 
	 * @param menuItemID
	 * @return
	 */
	public MenuMaker addMenuItems(int... menuItemID) {
		for (int i = 0; i < menuItemID.length; i++) {
			menuIDs.add(menuItemID[i]);
		}
		return this;
	}

	/**
	 * Add one menu item with behavior given by the second parameter.
	 * 
	 * @param menuId
	 * @param menuItemClickListener
	 * @return
	 */
	public MenuMaker addMenuItem(int menuId,
			OnMenuItemClickListener menuItemClickListener) {
		menuIDs.add(menuId);
		specificMenuItemClickListeners.put(menuId, menuItemClickListener);
		return this;
	}

	public MenuMaker removeMenuItems(int... menuItemID) {
		for (int i = 0; i < menuItemID.length; i++) {
			menuIDs.removeElement(menuItemID[i]);
		}
		return this;
	}

	public void setupMenu(Menu menu) {
		menu.clear();
		for (Integer menuID : menuIDs) {
			MenuItem menuItem = menu.add(Menu.NONE, menuID, Menu.NONE,
					menuItemTextResources.get(menuID));
			menuItem.setIcon(menuItemDrawables.get(menuID));
			/*
			 * first we look for an object-specific click listener if we do not
			 * find one, we look for a static one, if we still do not find one,
			 * we use the NULL object just logging the error when clicked.
			 */
			menuItem.setOnMenuItemClickListener(specificMenuItemClickListeners
					.get(menuID, standardMenuItemClickListeners.get(menuID,
							NULL_OMICL)));
		}
	}
}
