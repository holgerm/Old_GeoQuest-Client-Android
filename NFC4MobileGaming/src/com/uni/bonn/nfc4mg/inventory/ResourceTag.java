package com.uni.bonn.nfc4mg.inventory;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.constants.CommonTagErrors;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.ResourceTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;

/**
 * Class to deal with Group Tag type. User has to create object of this class in
 * order to deal with any operation related to group tags.
 * 
 * @author shubham
 * 
 */
public final class ResourceTag {

	// Number of NdefRecrds to support Resource Tag information
	private static final int NO_OF_RECORDS = 3;

	/**
	 * API to write resource information to NFC tag
	 * 
	 * @param model
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	protected static boolean write2Tag(ResourceTagModel model, Tag tag)
			throws IOException, FormatException, NfcTagException {

		// check id uniqueness
		if (null == model)
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_MODEL_NOT_INIT);

		String id = model.getId();

		// throw exception in case user has not defined tag id.
		if (null == id || "".equals(model.getId()))
			throw new NfcTagException(CommonTagErrors.ErrorMsg.TAG_ID_UNDEFINED);

		// id prefix check
		if (!id.startsWith(TagConstants.TAG_TYPE_RESOURCE_PREFIX))
			model.setId(TagConstants.TAG_TYPE_RESOURCE_PREFIX + model.getId());

		// finally create group tag
		NdefRecord records[] = new NdefRecord[NO_OF_RECORDS];

		short index = 0;
		records[index] = TextRecord.createRecord(model.getId());
		records[++index] = TextRecord.createRecord(model.getName());
		records[++index] = TextRecord.createRecord("" + model.getCount());// explicitly
																			// converting
																			// integer
																			// to
																			// string
																			// to
																			// store
																			// into
																			// tags

		NdefMessage group_msg = new NdefMessage(records);
		NfcReadWrite.writeToNfc(group_msg, tag);
		return true;
	}

	/**
	 * Read information from resource tag.
	 * 
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	protected static ResourceTagModel readTagData(Tag tag) throws IOException,
			FormatException, NfcTagException {

		NdefMessage msg = NfcReadWrite.readNfcData(tag);

		return parseResNdefMsg(msg);
	}

	/**
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	protected static ResourceTagModel parseResNdefMsg(NdefMessage msg)
			throws IOException, FormatException, NfcTagException {
		ResourceTagModel model = new ResourceTagModel();

		if (null == msg) {
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_INTERACTION_FAILED);
		}

		NdefRecord records[] = msg.getRecords();

		if (null != records && NO_OF_RECORDS == records.length) {

			short index = 0;
			String id = TextRecord.parseNdefRecord(records[index]).getData();
			if (!id.startsWith(TagConstants.TAG_TYPE_RESOURCE_PREFIX)) {
				throw new NfcTagException(
						CommonTagErrors.ErrorMsg.TAG_INVALID_API);
			}
			model.setId(id);
			model.setName(TextRecord.parseNdefRecord(records[++index])
					.getData());
			model.setCount(Integer.parseInt(TextRecord.parseNdefRecord(
					records[++index]).getData()));
			return model;
		}
		return null;
	}

}
