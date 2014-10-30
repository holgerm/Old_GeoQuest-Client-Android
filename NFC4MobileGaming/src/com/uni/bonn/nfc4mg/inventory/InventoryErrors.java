package com.uni.bonn.nfc4mg.inventory;

/**
 * This class holds maps all possible group error codes.
 * @author shubham
 *
 */
public final class InventoryErrors {

	/**
	 * Class holds the error codes
	 * @author shubham
	 *
	 */
	final static class ErrorCodes{
		
		//When some one try to get resource which is already taken by another player
		public static final int ERROR_RESOURCE_ALREADY_TAKEN = 201;		
	}
	
	/**
	 * Class holds the corresponding error messages, directly mapped to error codes
	 * @author shubham
	 *
	 */
	final static class ErrorMsg{
	
		//When some one try to get resource which is already taken by another player
		public static final String ERROR_RESOURCE_ALREADY_TAKEN = "ERROR : Resource is already taken.";		
		
	}
}
