package com.uni.bonn.nfc4mg.infotag;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.constants.CommonTagErrors;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;

/**
 * Class to deal with info Tag type. User has to create object of this class in
 * order to deal with any operation related to info tags.
 * 
 * @author shubham
 * 
 */
public class InfoTag {

	// holds the single instance of Info Tag class
	private static InfoTag INSTANCE = null;

	// Number of NdefRecrds to support Info Tag information
	private static final int NO_OF_RECORDS = 2;

	/**
	 * Private Constructor to make this class Singelton
	 */
	private InfoTag() {

	}

	/**
	 * Get the instance of info tag.
	 * 
	 * @return
	 */
	public static InfoTag getInstance() {

		if (null == INSTANCE) {
			INSTANCE = new InfoTag();
		}
		return INSTANCE;
	}

	/**
	 * Write Info tag data model to NFC tag.
	 * 
	 * @param model
	 *            : pass model to write data to NFC tag
	 * @param tag
	 *            : tag reference to write into
	 * @return
	 * @throws TagModelException
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean write2Tag(InfoTagModel model, Tag tag) throws IOException,
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
		if (!id.startsWith(TagConstants.TAG_TYPE_INFO_PREFIX))
			model.setId(TagConstants.TAG_TYPE_INFO_PREFIX + model.getId());

		NdefRecord records[] = new NdefRecord[NO_OF_RECORDS];
		short index = 0;
		records[index] = TextRecord.createRecord(model.getId());
		records[++index] = TextRecord.createRecord(model.getData());

		NdefMessage info_msg = new NdefMessage(records);
		NfcReadWrite.writeToNfc(info_msg, tag);
		return true;
	}

	/**
	 * Read Info Tag model data
	 * 
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public InfoTagModel readTagData(Tag tag) throws IOException,
			FormatException, NfcTagException {

		InfoTagModel model = new InfoTagModel();
		NdefMessage msg = NfcReadWrite.readNfcData(tag);

		if (null == msg) {
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_INTERACTION_FAILED);
		}

		NdefRecord records[] = msg.getRecords();

		if (null != records && NO_OF_RECORDS == records.length) {
			short index = 0;
			String id = TextRecord.parseNdefRecord(records[index]).getData();
			if (!id.startsWith(TagConstants.TAG_TYPE_INFO_PREFIX)) {
				throw new NfcTagException(
						CommonTagErrors.ErrorMsg.TAG_INVALID_API);
			}
			model.setId(id);
			model.setData(TextRecord.parseNdefRecord(records[++index])
					.getData());
			return model;
		}
		return null;
	}
}
