package com.uni.bonn.nfc4mg.nfctag;

import org.dom4j.Element;

import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.tagmodels.BTTagModel;
import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;
import com.uni.bonn.nfc4mg.tagmodels.GroupTagModel;
import com.uni.bonn.nfc4mg.tagmodels.InfoTagModel;
import com.uni.bonn.nfc4mg.tagmodels.WiFiTagModel;

public class TagIntializer {

	private static final String TAG = "TagIntializer";

	// Tag type -1 for unknown tag
	private int tagType = -1;
	private ParseTagListener mParseListener;

	// At a time only one object has value, all other instances holds null value
	private InfoTagModel mInfoTagModel = null;
	private GPSTagModel mGPSTagModel = null;
	private WiFiTagModel mWiFiTagModel = null;
	private BTTagModel mBTTagModel = null;
	private GroupTagModel mGroupTagModel = null;

	public TagIntializer(ParseTagListener listener) {

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
	 * Return value of Tag type
	 * 
	 * @return
	 */
	public int getTagType() {
		return tagType;
	}

	public void initializeFromXML(Element xmlElement) {

		String type = xmlElement.attributeValue("type");
		Log.v(TAG, "type = " + type);

		// TODO framework can add error chekc also
		if ("info".equals(type)) {

			tagType = TagConstants.TAG_TYPE_INFO;

			String id = xmlElement.attributeValue("id");
			String data = xmlElement.attributeValue("data");

			Log.v(TAG, "id = " + id);
			Log.v(TAG, "data = " + data);

			mInfoTagModel = new InfoTagModel(id, data);

		} else if ("gps".equals(type)) {

			tagType = TagConstants.TAG_TYPE_GPS;
			String id = xmlElement.attributeValue("id");
			String latitude = xmlElement.attributeValue("latitude");
			String longitude = xmlElement.attributeValue("longitude");
			String data = xmlElement.attributeValue("data");
			mGPSTagModel = new GPSTagModel(id, latitude, longitude, data);
		}

		mParseListener.onParseComplete(tagType);
	}

	/**
	 * APIs to check connection with NFC Tag is still live or not
	 * 
	 * @param tag
	 * @return
	 */
	public boolean isConnected(Tag tag) {

		Ndef ndef = Ndef.get(tag);
		return ndef.isConnected();
	}
}
