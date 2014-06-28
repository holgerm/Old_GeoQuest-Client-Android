package com.uni.bonn.nfc4mg.utility;

import java.io.IOException;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import com.uni.bonn.nfc4mg.exception.NfcTagException;

/**
 * This class is implementing basic read/write operation for NFC Tags.
 * 
 * @author shubham
 * 
 */
public class NfcReadWrite {

	// Supported TAG Technology. In case of other tag technology Exception will
	// be thrown.
	public static String TECH_NDEF = "android.nfc.tech.Ndef";
	public static String TECH_NDEFFORMATABLE = "android.nfc.tech.NdefFormatable";

	private static boolean isNdefSupport = false;
	private static boolean isNdefForSupport = false;

	/**
	 * Internal utility method to set class members to their default state.
	 */
	private static void initializeToDefault() {

		isNdefSupport = false;
		isNdefForSupport = false;
	}

	/**
	 * Function to write any NdefMessage to the passed tag object. Caller must
	 * verify 'msg' should be the type of NdefMessage.
	 * 
	 * @param msg
	 *            : NdefMessage type
	 * @param tag
	 *            : current tag instance
	 * @throws IOException
	 * @throws FormatException
	 * @throws TagTechnologyNotSupportedException
	 * @throws TagReadOnlyException 
	 * @throws NfcTagException 
	 */
	public static void writeToNfc(NdefMessage msg, Tag tag) throws IOException,
			FormatException, NfcTagException {
		
		// getting the supported tech technology
		getTagTech(tag);

		if (isNdefSupport) {

			Ndef ndef = Ndef.get(tag);

			//Perform a pre check conditions before writing into Nfc tag
			preConditionToWrite(msg, ndef);
			
			ndef.writeNdefMessage(msg);
			ndef.close(); // this is mandatory to close communication channel

		} else if (isNdefForSupport) {

			NdefFormatable nf = NdefFormatable.get(tag);
			nf.connect();
			nf.format(msg);
			nf.close();

		} else {

			// Throw custom framework exception object
			throw new NfcTagException(
					"NFC tag technology not supported by framwork");
		}
	}

	/**
	 * Internal method to perform Ndef check before writing actual data to Tag.
	 * @param msg
	 * @param ndef
	 * @throws IOException
	 * @throws TagReadOnlyException
	 * @throws NfcTagException
	 */
	private static void preConditionToWrite(NdefMessage msg, Ndef ndef)
			throws IOException, NfcTagException {
		
		//getting size of message, to check capacity
		int size = msg.toByteArray().length;
		
		if(null != ndef){
			//Open connection with tag
			ndef.connect();
			
			//check for connection
			if(ndef.isConnected()){
				
				//check for tag write permission.
				if(ndef.isWritable()){
					
						//check for tag capacity, in case data is more, an exception will be thrown
						if(ndef.getMaxSize() > size){
						}else{
							throw new NfcTagException("Tag cannot hold this amount of data");
						}
				}else{
					throw new NfcTagException("Tag is read only. Cannot Write data!");
				}				
			}else{
				throw new TagLostException("");
			}
		}
	}	
	
	/**
	 * Internal class function to determine supported tech technologies.
	 * 
	 * @param tag
	 *            : current tag instance
	 */
	private static void getTagTech(Tag tag) {

		// Setting class variables to their default state
		initializeToDefault();

		// Getting technology list supported by tag
		String techList[] = tag.getTechList();
		for (String t : techList) {
			if (t.equals(TECH_NDEFFORMATABLE)) {
				isNdefForSupport = true;
			}
			if (t.equals(TECH_NDEF)) {
				isNdefSupport = true;
			}
		}
	}

	/**
	 * Function to read NdefMessage stored inside a NFC tag.
	 * 
	 * @param tag
	 *            : current tag instance
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 */
	public static NdefMessage readNfcData(Tag tag) throws IOException,
			FormatException {

		Ndef ndef = Ndef.get(tag);
		ndef.connect();
		NdefMessage msg = ndef.getNdefMessage();
		ndef.close();
		return msg;
	}
}
