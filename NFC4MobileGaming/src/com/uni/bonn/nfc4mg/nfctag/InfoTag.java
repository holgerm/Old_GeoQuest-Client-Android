package com.uni.bonn.nfc4mg.nfctag;

import java.io.IOException;
import java.util.HashMap;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.UriRecord;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.exception.TagModelException;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;
import com.uni.bonn.nfc4mg.utility.NfcTagUtility;

/**
 * Class to deal with info Tag type. User has to create object of this class in
 * order to deal with any operation related to info tags.
 * 
 * @author shubham
 * 
 */
public class InfoTag {

	// Supported MIME types by Info Nfc Tag
	private static HashMap<String, String> MIME_MAP;

	static {

		MIME_MAP = new HashMap<String, String>();
		MIME_MAP.put("TEXT", "TEXT");
		MIME_MAP.put("URI", "URI");
	};

	public boolean write2Tag(InfoTagModel model, Tag tag)
			throws TagModelException, IOException, FormatException,
			NfcTagException {

		// check id uniqueness
		if (null == model)
			throw new TagModelException("InfoTagModel is not initialized");

		String id = model.getId();

		// throw exception in case user has not defined tag id.
		if (null == id || "".equals(model.getId()))
			throw new TagModelException("Tag Id is not defined.");

		// id prefix check
		if (!id.startsWith(TagConstants.TAG_TYPE_INFO_PREFIX))
			model.setId(TagConstants.TAG_TYPE_INFO_PREFIX + model.getId());

		// check MIME is supported or not
		if (!MIME_MAP.containsKey(model.getMime()))
			throw new TagModelException("MIME type " + model.getMime()
					+ " not supported.");

		NdefRecord records[] = new NdefRecord[3];
		records[0] = TextRecord.createRecord(model.getId());
		records[1] = TextRecord.createRecord(model.getMime());

		// Before writing check the MIME type.
		if ("TEXT".equals(model.getMime())) {
			records[2] = TextRecord.createRecord(model.getData());
		} else if ("URI".equals(model.getMime())) {
			records[2] = UriRecord.createRecord(model.getData());
		}

		NdefMessage info_msg = new NdefMessage(records);
		NfcReadWrite.writeToNfc(info_msg, tag);
		return true;
	}

	public InfoTagModel readTagData(Tag tag) throws IOException,
			FormatException, NfcTagException {

		InfoTagModel model = new InfoTagModel();
		NdefMessage msg = NfcReadWrite.readNfcData(tag);
		
		if(null == msg){
			throw new NfcTagException("Unable to interact with tag");
		}
		
		NdefRecord records[] = msg.getRecords();
		
		if(null != records && 3 == records.length){
			model.setId(TextRecord.parseNdefRecord(records[0]).getData());
			model.setMime(TextRecord.parseNdefRecord(records[1]).getData());

			if ("TEXT".equals(NfcTagUtility.getNFCType(records[2]))) {
				model.setData(TextRecord.parseNdefRecord(records[2]).getData());
			} else if ("URI".equals(NfcTagUtility.getNFCType(records[2]))) {
				model.setData(UriRecord.parseNdefRecord(records[2]).getData());
			}

			return model;
		}
		
		return null;
	}
}
