package com.uni.bonn.nfc4mg.bttag;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.constants.CommonTagErrors;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.BTTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;

/**
 * Class to deal with Bluetooth Tag type. User has to create object of this
 * class in order to deal with any operation related to info tags.
 * 
 * @author shubham
 * 
 */
public class BTTag {

	// holds the single instance of BTTag class
	private static BTTag INSTANCE = null;

	// Number of NdefRecrds to support BT information
	private static final int NO_OF_RECORDS = 2;

	/**
	 * Private Constructor to make this class Singelton
	 */
	private BTTag() {

	}

	/**
	 * Get the instance of Bluetooth tag.
	 * 
	 * @return
	 */
	public static BTTag getInstance() {

		if (null == INSTANCE) {
			INSTANCE = new BTTag();
		}
		return INSTANCE;
	}

	/**
	 * API to write BT action modes to nfc tag
	 * 
	 * @param model
	 *            : pass Bluetooth tag model, else exception will be thrown
	 * @param tag
	 * @return true in case of success else false
	 * @throws TagModelException
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean write2Tag(BTTagModel model, Tag tag) throws IOException,
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
		if (!id.startsWith(TagConstants.TAG_TYPE_BT_PREFIX))
			model.setId(TagConstants.TAG_TYPE_BT_PREFIX + model.getId());

		NdefRecord records[] = new NdefRecord[NO_OF_RECORDS];
		short index = 0;
		records[index] = TextRecord.createRecord(model.getId());
		records[++index] = TextRecord.createRecord("" + model.getActionMode());

		NdefMessage info_msg = new NdefMessage(records);
		NfcReadWrite.writeToNfc(info_msg, tag);
		return true;
	}

	/**
	 * API to read BT action mode from nfc tag to perform corresponding
	 * operation.
	 * 
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public BTTagModel readTagData(Tag tag) throws IOException, FormatException,
			NfcTagException {

		BTTagModel model = new BTTagModel();
		NdefMessage msg = NfcReadWrite.readNfcData(tag);

		if (null == msg) {
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_INTERACTION_FAILED);
		}

		NdefRecord records[] = msg.getRecords();

		if (null != records && NO_OF_RECORDS == records.length) {
			short index = 0;

			// Use case : In case number of record is same, but tag is not BT
			// tag then we have to throw error

			String id = TextRecord.parseNdefRecord(records[index]).getData();
			if (!id.startsWith(TagConstants.TAG_TYPE_BT_PREFIX)) {
				throw new NfcTagException(
						CommonTagErrors.ErrorMsg.TAG_INVALID_API);
			}
			model.setId(id);
			model.setActionMode(Integer.parseInt(TextRecord.parseNdefRecord(
					records[++index]).getData()));
			return model;
		}
		return null;
	}
}
