package com.uni.bonn.nfc4mg.constants;

/**
 * This class holds maps all possible Tag interaction error codes.
 * 
 * @author shubham
 * 
 */
public class CommonTagErrors {

	/**
	 * Class holds the error codes
	 * 
	 * @author shubham
	 * 
	 */
	public final static class ErrorCodes {

		// In case tag model is not initialised before writing data into tag.
		public static final int TAG_MODEL_NOT_INIT = 301;

		// In case tag id is not specified by caller
		public static final int TAG_ID_UNDEFINED = 302;

		// In case interaction failed with NFC tag
		public static final int TAG_INTERACTION_FAILED = 303;

		// In case wrong API is used to parse the tag
		public static final int TAG_INVALID_API = 304;

	}

	/**
	 * Class holds the corresponding error messages, directly mapped to error
	 * codes
	 * 
	 * @author shubham
	 * 
	 */
	public final static class ErrorMsg {

		// In case tag model is not initialised before writing data into tag.
		public static final String TAG_MODEL_NOT_INIT = "ERROR : TagModel is not initialized";

		// In case tag id is not specified by caller
		public static final String TAG_ID_UNDEFINED = "ERROR : Tag Id is not defined.";

		// In case interaction failed with NFC tag
		public static final String TAG_INTERACTION_FAILED = "ERROR : Interaction Failed with NFC Tag.";

		// In case wrong API is used to parse the tag
		public static final String TAG_INVALID_API = "ERROR : Invalid API call.";

	}
}
