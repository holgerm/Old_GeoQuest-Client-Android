package com.uni.bonn.nfc4mg.groups;

/**
 * This class holds maps all possible group error codes.
 * @author shubham
 *
 */
public final class GroupErrors {

	/**
	 * Class holds the error codes
	 * @author shubham
	 *
	 */
	final static class ErrorCodes{
		
		//When some one try to initialize group more than a max capacity defined in settings.
		public static final int ERROR_CAPACITY = 101;
		
		//When some one try to join a group and group is already full.
		public static final int ERROR_GROUP_FULL = 102;
		
		//When external player try to perform read or write operation.
		public static final int ERROR_GROUP_READ_WRITE = 103;
		
		//When external player try to read group content.
		public static final int ERROR_GROUP_READ_ONLY = 104;
		
		//When external player try to modify the group content.
		public static final int ERROR_GROUP_WRITE_ONLY = 105;
		
		//When player try to join the same group
		public static final int ERROR_ALREADY_IN_GROUP = 106;
		
		//When try to initialize group with wrong permission
		public static final int ERROR_INVALID_GROUP_PERMISSION = 107;
		
		//When player try to leave group, he/she does not belongs to
		public static final int ERROR_NOT_IN_GROUP = 108;
	}
	
	/**
	 * Class holds the corresponding error messages, directly mapped to error codes
	 * @author shubham
	 *
	 */
	final static class ErrorMsg{
	
		//When some one try to initialize group more than a max capacity defined in settings.
		public static final String ERROR_CAPACITY = "ERROR : Exceeding Group Capacity.";
		
		//When some one try to join a group and group is already full.
		public static final String ERROR_GROUP_FULL = "ERROR : This Group is full.";
		
		//When external player try to perform read or write operation.
		public static final String ERROR_GROUP_READ_WRITE = "ERROR : Only Group Members can Read and Write.";
		
		//When external player try to read group content.
		public static final String ERROR_GROUP_READ_ONLY = "ERROR : Only Group Members has Read permission.";
		
		//When external player try to modify the group content.
		public static final String ERROR_GROUP_WRITE_ONLY = "ERROR : Only Group Members has Write permission.";
		
		//When player try to join the same group
		public static final String ERROR_ALREADY_IN_GROUP = "ERROR : You are already in Group.";
		
		//When try to initialize group with wrong permission
		public static final String ERROR_INVALID_GROUP_PERMISSION = "ERROR : Invalid Group Permission.";
		
		//When player try to leave group, he/she does not belongs to
		public static final String ERROR_NOT_IN_GROUP = "ERROR : You are not in this group.";
		
	}
}
