package com.uni.bonn.nfc4mg.groups;

/**
 * This class holds the constants for group tag permission. Here we have
 * introduces four different permission types.
 * 
 * @author shubham
 * 
 */
public final class GroupPermission {

	// anyone can perform read, write operation on group tag
	public static final int ALL_READ_WRITE = 1;

	// only group members can read, write
	public static final int GROUP_READ_WRITE = 2;

	// only group members can read, but anyone can write
	public static final int GROUP_READ_ALL_WRITE = 3;

	// only group members can write, but anyone can read
	public static final int GROUP_WRITE_ALL_READ = 4;

	// default permission set to ALL_READ_WRITE
	public static final int DEFAULT_PERMISSION = ALL_READ_WRITE;
}
