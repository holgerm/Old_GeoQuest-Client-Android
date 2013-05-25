package com.uni.bonn.nfc4mg;

import java.io.UnsupportedEncodingException;

import android.nfc.NdefRecord;

import com.uni.bonn.nfc4mg.tagmodels.BaseTagModel;

/**
 * Base Class to deal with NdefRecord of type TEXT. This class included basic
 * operations to create and parse record type.
 * 
 * @author shubham
 * 
 */
public final class TextRecord {

	/**
	 * This function create a NDEFRecord of type TEXT
	 * @param val : data to write into tag
	 * @param args
	 * @return NdefRecord
	 * @throws UnsupportedEncodingException
	 */
	public static NdefRecord createRecord(String val, String... args)
			throws UnsupportedEncodingException {
		// create the message in according with the standard
		String lang = "en";
		byte[] textBytes = val.getBytes();
		byte[] langBytes = lang.getBytes("US-ASCII");
		int langLength = langBytes.length;
		int textLength = textBytes.length;

		byte[] payload = new byte[1 + langLength + textLength];
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		System.arraycopy(langBytes, 0, payload, 1, langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], payload);
		return recordNFC;
	}

	/**
	 * This function parse the NdefRecord of type TEXT
	 * @param ndefRecord : record to be parsed. Caller of this function must check the NdefRecord type.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static BaseTagModel parseNdefRecord(NdefRecord ndefRecord)
			throws UnsupportedEncodingException {

		byte[] payload = ndefRecord.getPayload();

		/*
		 * payload[0] contains the "Status Byte Encodings" field, per the NFC
		 * Forum "Text Record Type Definition" section 3.2.1.
		 * 
		 * bit7 is the Text Encoding Field.
		 * 
		 * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1): The
		 * text is encoded in UTF16
		 * 
		 * Bit_6 is reserved for future use and must be set to zero.
		 * 
		 * Bits 5 to 0 are the length of the IANA language code.
		 */
		String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
		int languageCodeLength = payload[0] & 0077;
		String languageCode = new String(payload, 1, languageCodeLength,
				"US-ASCII");

		String text = new String(payload, languageCodeLength + 1,
				payload.length - languageCodeLength - 1, textEncoding);

		BaseTagModel baseModel = new BaseTagModel();
		baseModel.setData(text);
		baseModel.setType("TEXT");
		return baseModel;
	}
}
