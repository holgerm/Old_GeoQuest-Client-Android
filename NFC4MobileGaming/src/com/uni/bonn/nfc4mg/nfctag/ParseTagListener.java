package com.uni.bonn.nfc4mg.nfctag;

/**
 * Interface implements callback function when tag data parsing is in process or
 * complete.
 * 
 * @author shubham
 * 
 */
public interface ParseTagListener {

	// callback function when tag parsing started.
	public void onStartParsing(String msg);
	
	// Callback when NFC tag parsing is complete
	public void onParseComplete(int tagType);

}
