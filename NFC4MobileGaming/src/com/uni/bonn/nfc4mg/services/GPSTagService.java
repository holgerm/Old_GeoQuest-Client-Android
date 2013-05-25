package com.uni.bonn.nfc4mg.services;

import com.uni.bonn.nfc4mg.tagmodels.GPSTagModel;

import android.nfc.NdefMessage;

/**
 * This class has following features implement
 * 
 * 1. Switch on/off GPS in device
 * 2. Read GPS data from NFC tags
 * 3. Write GPS data into NFC Tags
 * 
 * @author shubham
 *
 */
public final class GPSTagService {
	
	private static  String TAG = "GPSTagService";
	
	private static boolean gpsStatus = false;
	
	
	public GPSTagModel parseGPSTag(NdefMessage gpsMsg){
		
		return null;
	}
	
	public boolean initializeGPSTag(String id, double latitude, double longitude){
		return false;
	}
	
	
	public static void enableGps(){
		
	}
	
	public static void disableGps(){
		
	}
	
	
	public static boolean getGpsStatus(){
		
		return gpsStatus;
	}
}
