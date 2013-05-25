package com.uni.bonn.nfc4mg.tagmodels;

/**
 * This class represents the model of Group. NFC Tag which is storing this structure is called GroupTag.
 * 
 * @author shubham
 *
 */

public class GroupTagModel {

	//Represents the id of a group. Every id value is prefixed by 'grp_'
	String _id;
	
	//Represents capacity of a group. MAX CAPACITY = 4
	int _capacity;
	
	//Hold information about group members. An array each element represents a NDEFRecord
	String _members[];
	
}
