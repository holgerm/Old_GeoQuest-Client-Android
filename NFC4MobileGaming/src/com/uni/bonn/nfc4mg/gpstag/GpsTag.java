package com.uni.bonn.nfc4mg.gpstag;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.constants.CommonTagErrors;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;

/**
 * Class to deal with GPS Tag type. User has to create object of this class in
 * order to deal with any operation related to info tags.
 * 
 * @author shubham
 * 
 */
public class GpsTag {

	// holds the single instance of GPS Tag class
	private static GpsTag INSTANCE = null;

	// Number of NdefRecrds to support GPS Tag information
	private static final int NO_OF_RECORDS = 4;

	/**
	 * Private Constructor to make this class Singelton
	 */
	private GpsTag() {

	}

	/**
	 * Get the instance of GPS tag.
	 * 
	 * @return
	 */
	public static GpsTag getInstance() {

		if (null == INSTANCE) {
			INSTANCE = new GpsTag();
		}
		return INSTANCE;
	}

	/**
	 * API to write gps data to nfc tag. Caller of the api must pass the
	 * 
	 * @param model
	 *            : proper tag model
	 * @param tag
	 *            : tag reference to write data to
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean write2Tag(GPSTagModel model, Tag tag) throws IOException,
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
		if (!id.startsWith(TagConstants.TAG_TYPE_GPS_PREFIX))
			model.setId(TagConstants.TAG_TYPE_GPS_PREFIX + model.getId());

		NdefRecord records[] = new NdefRecord[NO_OF_RECORDS];

		short index = 0;
		records[index] = TextRecord.createRecord(model.getId());
		records[++index] = TextRecord.createRecord(model.getLatitude());
		records[++index] = TextRecord.createRecord(model.getLongitude());
		records[++index] = TextRecord.createRecord(model.getData());

		NdefMessage info_msg = new NdefMessage(records);
		NfcReadWrite.writeToNfc(info_msg, tag);
		return true;
	}

	/**
	 * API to read gps tag data.
	 * 
	 * @param tag
	 *            : return GPSTagModel
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public GPSTagModel readTagData(Tag tag) throws IOException,
			FormatException, NfcTagException {

		GPSTagModel model = new GPSTagModel();
		NdefMessage msg = NfcReadWrite.readNfcData(tag);

		if (null == msg) {
			throw new NfcTagException(
					CommonTagErrors.ErrorMsg.TAG_INTERACTION_FAILED);
		}

		NdefRecord records[] = msg.getRecords();

		if (null != records && NO_OF_RECORDS == records.length) {
			short index = 0;

			String id = TextRecord.parseNdefRecord(records[index]).getData();
			if (!id.startsWith(TagConstants.TAG_TYPE_GPS_PREFIX)) {
				throw new NfcTagException(
						CommonTagErrors.ErrorMsg.TAG_INVALID_API);
			}
			model.setId(id);

			model.setLatitude(TextRecord.parseNdefRecord(records[++index])
					.getData());
			model.setLongitude(TextRecord.parseNdefRecord(records[++index])
					.getData());
			model.setData(TextRecord.parseNdefRecord(records[++index])
					.getData());
			return model;
		}
		return null;
	}
}
