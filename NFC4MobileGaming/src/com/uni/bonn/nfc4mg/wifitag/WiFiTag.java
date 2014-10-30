package com.uni.bonn.nfc4mg.wifitag;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.util.Log;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.constants.CommonTagErrors;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.WiFiTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;

/**
 * Class to deal with Bluetooth Tag type. User has to create object of this
 * class in order to deal with any operation related to info tags.
 * 
 * @author shubham
 * 
 */
public class WiFiTag {

	// holds the single instance of WiFi Tag class
	private static WiFiTag INSTANCE = null;

	// Number of NdefRecrds to support BT information
	private static final int NO_OF_RECORDS = 4;

	private static final String TAG = "WiFiTag";

	/**
	 * Private Constructor to make this class Singelton
	 */
	private WiFiTag() {

	}

	/**
	 * Get the instance of WIFI tag.
	 * 
	 * @return
	 */
	public static WiFiTag getInstance() {

		if (null == INSTANCE) {
			INSTANCE = new WiFiTag();
		}
		return INSTANCE;
	}

	/**
	 * API to write wifi action modes to nfc tag
	 * 
	 * @param model
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean write2Tag(WiFiTagModel model, Tag tag) throws IOException,
			FormatException, NfcTagException {

		// check id uniqueness
		if (null == model)
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_MODEL_NOT_INIT);

		String id = model.getId();

		// throw exception in case user has not defined tag id.
		if (null == id || "".equals(model.getId()))
			throw new NfcTagException(CommonTagErrors.ErrorMsg.TAG_ID_UNDEFINED);

		// id prefix check
		if (!id.startsWith(TagConstants.TAG_TYPE_WIFI_PREFIX))
			model.setId(TagConstants.TAG_TYPE_WIFI_PREFIX + model.getId());

		NdefRecord records[] = new NdefRecord[NO_OF_RECORDS];
		short index = 0;
		records[index] = TextRecord.createRecord(model.getId());
		records[++index] = TextRecord.createRecord("" + model.getActionMode());
		records[++index] = TextRecord.createRecord(model.getSsid());
		records[++index] = TextRecord.createRecord("" + model.getPassword());

		NdefMessage info_msg = new NdefMessage(records);
		NfcReadWrite.writeToNfc(info_msg, tag);
		return true;
	}

	/**
	 * API to read wifi action mode from nfc tag to perform corresponding
	 * operation.
	 * 
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public WiFiTagModel readTagData(Tag tag) throws IOException,
			FormatException, NfcTagException {

		WiFiTagModel model = new WiFiTagModel();
		NdefMessage msg = NfcReadWrite.readNfcData(tag);

		if (null == msg) {
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_INTERACTION_FAILED);
		}

		NdefRecord records[] = msg.getRecords();

		if (null != records && NO_OF_RECORDS == records.length) {
			short index = 0;
			String id = TextRecord.parseNdefRecord(records[index]).getData();

			Log.d(TAG, id);

			if (!id.startsWith(TagConstants.TAG_TYPE_WIFI_PREFIX)) {
				Log.d(TAG, "Invalid API call");
				throw new NfcTagException(
						CommonTagErrors.ErrorMsg.TAG_INVALID_API);
			}
			
			model.setId(id);
			model.setActionMode(Integer.parseInt(TextRecord.parseNdefRecord(
					records[++index]).getData()));

			model.setSsid(TextRecord.parseNdefRecord(
					records[++index]).getData());
			
			model.setPassword(TextRecord.parseNdefRecord(
					records[++index]).getData());
			
			return model;
		}
		return null;
	}
}
