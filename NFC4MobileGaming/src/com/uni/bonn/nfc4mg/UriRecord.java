package com.uni.bonn.nfc4mg;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import android.net.Uri;
import android.nfc.NdefRecord;
import android.util.Log;

import com.uni.bonn.nfc4mg.tagmodels.BaseTagModel;
import com.uni.bonn.nfc4mg.utility.NfcTagUtility;

/**
 * Base Class to deal with NdefRecord of type URI. This class included basic
 * operations to create and parse record type.
 * 
 * @author shubham
 * 
 * 
 *         This class is not currently used in the framework, but can be later
 *         used for creating URI records.
 * 
 */
public class UriRecord {

	// class log identifier, help debugging at class level
	private static final String TAG = "UriRecord";

	// Pre-stored the length of Uri prefix map
	private static int URI_PREFIX_MAP_LEN = NfcTagUtility.URI_PREFIX_MAP.length;

	/**
	 * This function create a NDEFRecord of type URI
	 * 
	 * @param val
	 *            : URI to write into tag
	 * @param args
	 * @return NdefRecord
	 * @throws UnsupportedEncodingException
	 */
	public static NdefRecord createRecord(String val, String... args) {

		// Explicitly check the value of URI passed by.
		if (null == val || ("".equals(val))) {
			throw new NullPointerException("Uri value cannot be null.");
		}

		Uri uri = normalizeScheme(Uri.parse(val));
		String uriString = uri.toString();
		if (uriString.length() == 0)
			throw new IllegalArgumentException("Uri is empty");

		byte prefix = 0;
		for (int i = 1; i < URI_PREFIX_MAP_LEN; i++) {
			if (uriString.startsWith(NfcTagUtility.URI_PREFIX_MAP[i])) {
				prefix = (byte) i;
				uriString = uriString.substring(NfcTagUtility.URI_PREFIX_MAP[i]
						.length());
				break;
			}
		}

		byte[] uriBytes = uriString.getBytes(Charset.forName("UTF-8"));
		byte[] recordBytes = new byte[uriBytes.length + 1];
		recordBytes[0] = prefix;
		System.arraycopy(uriBytes, 0, recordBytes, 1, uriBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,
				new byte[] {}, recordBytes);
	}

	/**
	 * This function parse the NdefRecord of type URI
	 * 
	 * @param ndefRecord
	 *            : record to be parsed. Caller of this function must check the
	 *            NdefRecord type.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static BaseTagModel parseNdefRecord(NdefRecord ndefRecord) {

		byte[] payload = ndefRecord.getPayload();
		if (payload.length < 2) {
			return null;
		}

		// payload[0] contains the URI Identifier Code, as per
		// NFC Forum "URI Record Type Definition" section 3.2.2.
		int prefixIndex = (payload[0] & (byte) 0xFF);
		if (prefixIndex < 0 || prefixIndex >= URI_PREFIX_MAP_LEN) {
			return null;
		}
		String prefix = NfcTagUtility.URI_PREFIX_MAP[prefixIndex];
		String suffix = new String(Arrays.copyOfRange(payload, 1,
				payload.length), Charset.forName("UTF-8"));

		BaseTagModel baseModel = new BaseTagModel();
		baseModel.setData(prefix + suffix);
		baseModel.setType("URI");
		return baseModel;
	}

	/**
	 * Internal method to normalize a uri
	 * 
	 * @param uri
	 * @return
	 */
	private static Uri normalizeScheme(Uri uri) {
		String scheme = uri.getScheme();
		Log.v(TAG, "scheme = " + scheme);
		if (scheme == null)
			return uri; // give up
		String lowerScheme = scheme.toLowerCase(Locale.US);
		if (scheme.equals(lowerScheme))
			return uri; // no change

		return uri.buildUpon().scheme(lowerScheme).build();
	}
}
