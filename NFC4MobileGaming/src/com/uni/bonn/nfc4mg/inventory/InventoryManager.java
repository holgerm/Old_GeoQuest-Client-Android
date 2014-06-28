package com.uni.bonn.nfc4mg.inventory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.ResourceTagModel;

/**
 * Inventory Manager class to manage the data about all resources earned during
 * the game. The entire inventory is managed in terms of key and value pairs.
 * 
 * @author shubham
 * 
 */
public class InventoryManager {

	private static final String TAG = "InventoryManager";

	private static InventoryManager INSTANCE = null;
	private OnResourceCallback mOnResourceCallback = null;
	private static HashMap<String, InventoryModel> INVENTORY_REPO = new HashMap<String, InventoryModel>();

	/**
	 * Singleton Class
	 */
	private InventoryManager() {
	}

	/**
	 * Get the instance of inventory manager.
	 * 
	 * @return
	 */
	public static InventoryManager getInventoryManager() {

		if (null == INSTANCE) {
			INSTANCE = new InventoryManager();
		}
		return INSTANCE;
	}

	/**
	 * Api to handle resource transfer via Android Beaming
	 * @param intent
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean handleTransferResource(Intent intent) throws IOException,
			FormatException, NfcTagException {

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// only one message sent during the beam
			NdefMessage msg = (NdefMessage) rawMsgs[0];

			ResourceTagModel model = ResourceTag.parseResNdefMsg(msg);
			if (null == model) {
				throw new NfcTagException("Invalid Resource Model.");
			} else {

				addItem(model);
				return true;
			}
		}
		return false;

	}

	/**
	 * API to interact with resource model. OnResourceCallback has to override
	 * in order to get immediate callback.
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 * @throws TagModelException
	 * @throws NfcTagException
	 */
	public void handleResourceTag(Tag tag) throws IOException, FormatException,
			NfcTagException {

		ResourceTagModel model = ResourceTag.readTagData(tag);

		int count = model.getCount();

		Log.d(TAG, "count = " + count);

		// check resource is already taken or not.
		if (0 != count) {

			// update the resource inventory and call success callback
			model.setCount(model.getCount() - 1);
			if (ResourceTag.write2Tag(model, tag)) {

				// add item to inventory
				InventoryModel addedModel = addItem(model.getId(),
						model.getName(), model.getCount());
				if (null != mOnResourceCallback) {
					mOnResourceCallback.onEarnedResource(addedModel);
				}
			}
		} else {
			// call callback in case resource is empty
			if (null != mOnResourceCallback) {

				mOnResourceCallback
						.onResourceEmpty(
								InventoryErrors.ErrorCodes.ERROR_RESOURCE_ALREADY_TAKEN,
								InventoryErrors.ErrorMsg.ERROR_RESOURCE_ALREADY_TAKEN);
			}
		}
	}

	/**
	 * Add item to inventory
	 * 
	 * @param key
	 *            : Each NFC Tag is initialized with unique id, that id can be
	 *            treated as the key in inventory.
	 * @param model
	 */
	private InventoryModel addItem(String key, String name, int count) {

		InventoryModel item = new InventoryModel(key, name, count);
		INVENTORY_REPO.put(key, item);
		return item;
	}

	/**
	 * Add item to inventory
	 * 
	 * @param key
	 *            : Each NFC Tag is initialized with unique id, that id can be
	 *            treated as the key in inventory.
	 * @param model
	 */
	private InventoryModel addItem(ResourceTagModel model) {

		InventoryModel item = new InventoryModel(model.getId(),
				model.getName(), model.getCount());
		INVENTORY_REPO.put(model.getId(), item);
		return item;
	}

	/**
	 * Remove Item from inventory
	 * 
	 * @param key
	 *            : Each NFC Tag is initialized with unique id, that id can be
	 *            treated as the key in inventory.
	 */
	public void removeItem(String key) {
		INVENTORY_REPO.remove(key);
	}

	/**
	 * To check inventory is empty or not.
	 * 
	 * @return
	 */
	boolean isInventoryEmpty() {
		return INVENTORY_REPO.isEmpty();
	}

	/**
	 * To delete entire inventory NOTE : calling this api will remove all items
	 * present in the inventory
	 */
	public void deleteInventory() {
		INVENTORY_REPO.clear();
	}

	/**
	 * Check for the specific item present in inventory or not
	 * 
	 * @param key
	 * @return
	 */
	public boolean isItemPresent(String key) {
		return INVENTORY_REPO.containsKey(key);
	}

	/**
	 * Returns the size of inventory
	 * 
	 * @return
	 */
	public int inventorySize() {
		return INVENTORY_REPO.size();
	}

	/**
	 * Return the item model for the given key, else this function will return
	 * null
	 * 
	 * @param key
	 * @return
	 */
	public InventoryModel getItem(String key) {
		return INVENTORY_REPO.get(key);

	}

	/**
	 * API to get complete list of resources
	 * 
	 * @return
	 */
	public ArrayList<InventoryModel> getItemList() {

		// will remove later on
		/*
		 * addItem("man", "mango", 1); addItem("app", "apple", 1); addItem("ba",
		 * "banana", 1);
		 */

		// Converting HashMap Values into ArrayList
		ArrayList<InventoryModel> resList = new ArrayList<InventoryModel>(
				INVENTORY_REPO.values());

		return resList;
	}

	/**
	 * API to read resource tag data
	 * 
	 * @param tag
	 * @return reference of ResourceTagModel
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public ResourceTagModel readData(Tag tag) throws IOException,
			FormatException, NfcTagException {

		return ResourceTag.readTagData(tag);

	}

	/**
	 * API to write resource data into NFC tag.
	 * 
	 * @param model
	 *            ResourceTagModel
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean writeData(ResourceTagModel model, Tag tag)
			throws IOException, FormatException, NfcTagException {

		return ResourceTag.write2Tag(model, tag);
	}

	/**
	 * API to compose resource message i order to transfer with beaming
	 * 
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public NdefMessage composeRessourceTransferMessage(InventoryModel model)
			throws UnsupportedEncodingException {

		// finally create group tag
		NdefRecord records[] = new NdefRecord[3];

		short index = 0;
		records[index] = TextRecord.createRecord(model.getId());
		records[++index] = TextRecord.createRecord(model.getName());
		records[++index] = TextRecord.createRecord("" + model.getCount());

		NdefMessage res_msg = new NdefMessage(records);
		return res_msg;
	}

	/**
	 * This function will initialize the resource tag first time.
	 * 
	 * @param model
	 * @param tag
	 * @return
	 * @throws TagModelException
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean initializeResourceTag(ResourceTagModel model, Tag tag)
			throws IOException, FormatException, NfcTagException {

		return ResourceTag.write2Tag(model, tag);
	}

	/**
	 * Resource callback listener. Called when player interacts with resource
	 * tag
	 * 
	 * @param listener
	 */
	public void setOnResourceCallback(OnResourceCallback listener) {

		this.mOnResourceCallback = listener;
	}

	/**
	 * Callback when player interact with the resource tag.
	 * 
	 * @author shubham
	 * 
	 */
	public static interface OnResourceCallback {

		public void onResourceEmpty(int error, String msg);

		public void onEarnedResource(InventoryModel addedModel);
	}
}
