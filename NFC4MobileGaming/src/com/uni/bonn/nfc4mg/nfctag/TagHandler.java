package com.uni.bonn.nfc4mg.nfctag;

import java.io.IOException;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

import com.uni.bonn.nfc4mg.TextRecord;
import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.exception.TagModelException;
import com.uni.bonn.nfc4mg.tagmodels.BTTagModel;
import com.uni.bonn.nfc4mg.tagmodels.BaseTagModel;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.GroupTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;
import com.uni.bonn.nfc4mg.tagmodels.WiFiTagModel;
import com.uni.bonn.nfc4mg.utility.NfcReadWrite;

/**
 * Class responsible to handle scanned NFC Tag. This class provide various
 * utility methods for information stored inside tags.
 * 
 * @author shubham
 * 
 */
public class TagHandler {

	private static final String TAG = "TagHandler";

	// Tag type
	private int tagType;
	private ParseTagListener mParseListener;

	// At a time only one object has value, all other instances holds null value
	private InfoTagModel mInfoTagModel = null;
	private GPSTagModel mGPSTagModel = null;
	private WiFiTagModel mWiFiTagModel = null;
	private BTTagModel mBTTagModel = null;
	private GroupTagModel mGroupTagModel = null;

	private Tag mTag = null;

	/**
	 * Function to get the instance of scanned tag
	 * 
	 * @return
	 */
	public Tag getmTag() {
		return mTag;
	}

	/***
	 * Constructor for parsing information inside NFC Tag and return
	 * corresponding model.
	 * 
	 * @param listener
	 * @throws NfcTagException
	 */
	public TagHandler(ParseTagListener listener) throws NfcTagException {

		if (null == listener) {
			throw new NfcTagException("ParseTagListener is null");
		}

		mParseListener = listener;

	}

	/**
	 * Return Model object for Info Tag Type
	 * 
	 * @return null in case tag type is of other type.
	 */
	public InfoTagModel getmInfoTagModel() {
		return mInfoTagModel;
	}

	/**
	 * Return Model object for GPS Tag Type
	 * 
	 * @return null in case tag type is of other type.
	 */
	public GPSTagModel getmGPSTagModel() {
		return mGPSTagModel;
	}

	/**
	 * Return Model object for WIFI Tag Type
	 * 
	 * @return null in case tag type is of other type.
	 */
	public WiFiTagModel getmWiFiTagModel() {
		return mWiFiTagModel;
	}

	/**
	 * Return Model object for Bluetooth Tag Type
	 * 
	 * @return null in case tag type is of other type.
	 */
	public BTTagModel getmBTTagModel() {
		return mBTTagModel;
	}

	/**
	 * Return Model object for Group Tag Type
	 * 
	 * @return null in case tag type is of other type.
	 */
	public GroupTagModel getmGroupTagModel() {
		return mGroupTagModel;
	}

	/**
	 * Function to parse and process the NFC intent
	 * 
	 * @param nfcIntent
	 * @throws TagModelException
	 * @throws FormatException
	 * @throws IOException
	 * @throws NfcTagException
	 */
	public void processIntent(Intent nfcIntent) throws TagModelException,
			IOException, FormatException, NfcTagException {

		Log.v(TAG, "Intent Action :: " + nfcIntent.getAction());

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(nfcIntent.getAction())) {

			mTag = nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			/**
			 * As per Android API we can write only one NDEFMessage in NFC tag
			 */
			if (null != mTag) {

				mParseListener.onStartParsing("processing tag parsing ...");
				parseScannedTag(mTag);

			} else {
				throw new TagModelException("Tag is empty.");
			}
		} else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent
				.getAction())) {

		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(nfcIntent
				.getAction())) {

		}
	}

	/**
	 * Return value of Tag type
	 * 
	 * @return
	 */
	public int getTagType() {
		return tagType;
	}

	/**
	 * API to check data in the tag is supported by framework or not. This
	 * framework has its own tag model to structure information. First record in
	 * the tag is always represents the id of the tag. Every Id has a prefix
	 * value, which can be used to determine the tag type.
	 * 
	 * @param tag
	 * @throws TagModelException
	 * @throws FormatException
	 * @throws IOException
	 * @throws NfcTagException
	 */
	public void parseScannedTag(Tag tag) throws TagModelException, IOException,
			FormatException, NfcTagException {

		mTag = tag;
		NdefMessage msg = NfcReadWrite.readNfcData(tag);

		if (msg == null) {
			throw new TagModelException(
					"Tag data cannot be parsed by framework.");
		}

		NdefRecord records[] = msg.getRecords();

		// In case record length is zero throw error
		if (0 == records.length)
			throw new TagModelException(
					"Tag data cannot be parsed by framework.");

		// check for tag Id field to know tag type.
		// NOTE : Id field MIME type is always TEXT
		BaseTagModel model = TextRecord.parseNdefRecord(records[0]);

		// pass tag id fields to identify the tag type
		findTagType(model.getData(), tag);

		// Parsing of tag data is complete, invoke callback
		mParseListener.onParseComplete(tagType);

	}

	/**
	 * Internal function for parsing tag of type GPS
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	private void parseGpsTag(Tag tag) throws IOException, FormatException,
			NfcTagException {

		GpsTag gTag = new GpsTag();
		mGPSTagModel = gTag.readTagData(tag);

	}

	/**
	 * Internal function for parsing tag of type Info
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	private void parseInfoTag(Tag tag) throws IOException, FormatException,
			NfcTagException {

		InfoTag iTag = new InfoTag();
		mInfoTagModel = iTag.readTagData(tag);

	}

	/**
	 * Internal function for parsing tag of type Bluetooth
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void parseBtTag(Tag tag) throws IOException, FormatException {

		BTTag bTag = new BTTag();
		mBTTagModel = bTag.readTagData(tag);

	}

	/**
	 * Internal function for parsing tag of type Wifi
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void parseWifiTag(Tag tag) throws IOException, FormatException {

		WiFiTag wTag = new WiFiTag();
		mWiFiTagModel = wTag.readTagData(tag);

	}

	/**
	 * Internal Function for getting tag type and corresponding model.
	 * 
	 * @param id
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 * @throws TagModelException
	 * @throws NfcTagException
	 */
	private void findTagType(String id, Tag tag) throws IOException,
			FormatException, TagModelException, NfcTagException {

		if (id.startsWith(TagConstants.TAG_TYPE_INFO_PREFIX)) {

			tagType = TagConstants.TAG_TYPE_INFO;
			parseInfoTag(tag);

		} else if (id.startsWith(TagConstants.TAG_TYPE_GPS_PREFIX)) {

			tagType = TagConstants.TAG_TYPE_GPS;
			parseGpsTag(tag);

		} else if (id.startsWith(TagConstants.TAG_TYPE_BT_PREFIX)) {

			tagType = TagConstants.TAG_TYPE_BT;
			parseBtTag(tag);

		} else if (id.startsWith(TagConstants.TAG_TYPE_WIFI_PREFIX)) {

			tagType = TagConstants.TAG_TYPE_WIFI;
			parseWifiTag(tag);

		} else if (id.startsWith(TagConstants.TAG_TYPE_GROUP_PREFIX)) {

			tagType = TagConstants.TAG_TYPE_GROUP;

		} else {

			// Throw exception in case id is not matched with any of the above
			// defined ids
			throw new TagModelException(
					"Tag data cannot be parsed by framework.");
		}
	}

}
