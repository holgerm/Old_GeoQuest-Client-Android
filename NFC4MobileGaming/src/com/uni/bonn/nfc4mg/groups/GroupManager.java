package com.uni.bonn.nfc4mg.groups;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.Tag;
import android.util.Log;

import com.uni.bonn.nfc4mg.constants.TagConstants;
import com.uni.bonn.nfc4mg.exception.NfcTagException;
import com.uni.bonn.nfc4mg.tagmodels.GroupTagModel;

public class GroupManager {

	private static GroupManager INSTANCE = null;
	private OnGroupErrorListener mGroupErrorListener = null;

	/**
	 * One Instance of Group Manager will be available for a game. Group Manager
	 * is the one dealing with group data stored into player mobile. RULES : 1.
	 * Managing Group List 2. Managing Group Permission
	 * 
	 * Group Id - Group Name - Group Permission
	 * 
	 * 1. Direct Api's to get information about user group saved in shared
	 * preference. 2. Group Permissions - Read Mode - to read the information
	 * you have to be the member of group Write Mode - Anyone can write the
	 * information, but then this will not delete the existing member
	 * information
	 */

	private static final String TAG = "GroupManager";

	/**
	 * Singleton Class
	 */
	private GroupManager() {
	}

	/**
	 * Get the instance of inventory manager.
	 * 
	 * @return
	 */
	public static GroupManager getGroupManager() {

		if (null == INSTANCE) {
			INSTANCE = new GroupManager();
		}
		return INSTANCE;
	}

	/**
	 * Function to set group error listener
	 * 
	 * @param listener
	 */
	public void setGroupErrorListener(OnGroupErrorListener listener) {
		this.mGroupErrorListener = listener;
	}

	/**
	 * API to join group. NOTE : Player can be in one group at a time, in case
	 * of joining new group without leaving old group, information will be
	 * overridden.
	 * 
	 * @param ctx
	 * @param tag
	 * @throws FormatException
	 * @throws IOException
	 * @throws NfcTagException
	 * @throws TagModelException
	 */
	public boolean joinGroup(Context ctx, Tag tag) throws IOException,
			FormatException, NfcTagException {

		// get the groupTagModel
		GroupTagModel model = GroupTag.readTagData(tag);

		SharedPreferences settings = ctx.getSharedPreferences(
				TagConstants.NFC4MG_PREF, 0);
		String group_Id = settings.getString(TagConstants.GROUP_INFO, null);

		Log.d(TAG, "group_Id = " + group_Id);

		// check for already a group member or not
		if (model.getId().equals(group_Id)) {
			Log.d(TAG, "Already member of group");
			if (mGroupErrorListener != null) {

				mGroupErrorListener.onGroupError(
						GroupErrors.ErrorCodes.ERROR_ALREADY_IN_GROUP,
						GroupErrors.ErrorMsg.ERROR_ALREADY_IN_GROUP);
			}
			return false;
		}

		// check for group capacity
		int occupied = model.getOccupied();
		if (TagConstants.MAX_GROUP_CAPACITY == occupied) {

			if (mGroupErrorListener != null) {

				mGroupErrorListener.onGroupError(
						GroupErrors.ErrorCodes.ERROR_GROUP_FULL,
						GroupErrors.ErrorMsg.ERROR_GROUP_FULL);
			}
			return false;
		} else {

			// In case not present in the group, add into group and increment
			// the
			// count and write back data to tag
			model.setOccupied(model.getOccupied() + 1);
		}

		if (GroupTag.write2Tag(model, tag)) {
			// On successful writing into NFC tag, update data into user local
			// shared preference
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(TagConstants.GROUP_INFO, model.getId());
			editor.commit();
		}

		return true;
	}

	/**
	 * API to leave existing group
	 * 
	 * @param ctx
	 * @param tag
	 * @return false, in case not a group member else true
	 * @throws TagModelException
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean leaveGroup(Context ctx, Tag tag) throws IOException,
			FormatException, NfcTagException {

		// get the groupTagModel
		GroupTagModel model = GroupTag.readTagData(tag);

		SharedPreferences settings = ctx.getSharedPreferences(
				TagConstants.NFC4MG_PREF, 0);
		String group_Id = settings.getString(TagConstants.GROUP_INFO, null);

		if (model.getId().equals(group_Id)) {

			Log.d(TAG, "Already member of group");
			// In case not present in the group, add into group and decrement
			// the count and write back data to tag
			model.setOccupied(model.getOccupied() - 1);

			if (GroupTag.write2Tag(model, tag)) {
				// On successful writing into NFC tag, update data into user
				// local shared preference
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(TagConstants.GROUP_INFO, null);
				editor.commit();
			}
			return true;
		}

		// call error callback when player try to leave group. he/she does not
		// belongs to
		if (mGroupErrorListener != null) {

			mGroupErrorListener.onGroupError(
					GroupErrors.ErrorCodes.ERROR_NOT_IN_GROUP,
					GroupErrors.ErrorMsg.ERROR_NOT_IN_GROUP);
		}
		return false;
	}

	/**
	 * API to read data from group. NOTE : Only existing member of group can
	 * read data from data.
	 * 
	 * @param ctx
	 * @param tag
	 * @return
	 * @throws IOException
	 * @throws FormatException
	 * @throws TagModelException
	 * @throws NfcTagException
	 */
	public GroupTagModel readGroupData(Context ctx, Tag tag)
			throws IOException, FormatException, NfcTagException {

		SharedPreferences settings = ctx.getSharedPreferences(
				TagConstants.NFC4MG_PREF, 0);
		String group_Id = settings.getString(TagConstants.GROUP_INFO, null);

		// get the groupTagModel
		GroupTagModel model = GroupTag.readTagData(tag);

		int permission = model.getPermission();

		switch (permission) {
		case GroupPermission.ALL_READ_WRITE:
		case GroupPermission.GROUP_WRITE_ALL_READ:
			return model;

		case GroupPermission.GROUP_READ_ALL_WRITE:
		case GroupPermission.GROUP_READ_WRITE:

			if (model.getId().equals(group_Id)) {
				Log.d(TAG, "Already member of group");
				return model;

			} else {
				if (mGroupErrorListener != null) {

					mGroupErrorListener.onGroupError(
							GroupErrors.ErrorCodes.ERROR_GROUP_READ_ONLY,
							GroupErrors.ErrorMsg.ERROR_GROUP_READ_ONLY);
				}
				return null;
			}

		default: // we assume that permission is not set correctly
			if (mGroupErrorListener != null) {
				mGroupErrorListener.onGroupError(
						GroupErrors.ErrorCodes.ERROR_INVALID_GROUP_PERMISSION,
						GroupErrors.ErrorMsg.ERROR_INVALID_GROUP_PERMISSION);
			}
			return null;
		}
	}

	/**
	 * This function will change the group data only, it will not going to
	 * effect the group member information
	 * 
	 * @param ctx
	 * @param data
	 * @throws FormatException
	 * @throws IOException
	 * @throws NfcTagException
	 * @throws TagModelException
	 */
	public boolean writeDataToGroup(Context ctx, Tag tag, String data)
			throws IOException, FormatException, NfcTagException {

		SharedPreferences settings = ctx.getSharedPreferences(
				TagConstants.NFC4MG_PREF, 0);
		String group_Id = settings.getString(TagConstants.GROUP_INFO, null);

		// get the groupTagModel
		GroupTagModel model = GroupTag.readTagData(tag);

		if (null == model) {
			return false;
		}

		int permission = model.getPermission();

		switch (permission) {

		case GroupPermission.ALL_READ_WRITE:
		case GroupPermission.GROUP_READ_ALL_WRITE:

			// set new data into model
			model.setData(data);
			return GroupTag.write2Tag(model, tag);

		case GroupPermission.GROUP_READ_WRITE:
		case GroupPermission.GROUP_WRITE_ALL_READ:

			if (model.getId().equals(group_Id)) {

				model.setData(data);
				return GroupTag.write2Tag(model, tag);

			} else {

				if (mGroupErrorListener != null) {

					mGroupErrorListener.onGroupError(
							GroupErrors.ErrorCodes.ERROR_GROUP_WRITE_ONLY,
							GroupErrors.ErrorMsg.ERROR_GROUP_WRITE_ONLY);
				}
				return false;
			}
		default:
			if (mGroupErrorListener != null) {
				mGroupErrorListener.onGroupError(
						GroupErrors.ErrorCodes.ERROR_INVALID_GROUP_PERMISSION,
						GroupErrors.ErrorMsg.ERROR_INVALID_GROUP_PERMISSION);
			}
			return false;
		}
	}

	/**
	 * This function will initialize the group tag first time.
	 * 
	 * @param model
	 *            : Model to initialize the tag
	 * @param tag
	 *            : scanned NFC tag reference
	 * @return
	 * @throws TagModelException
	 * @throws IOException
	 * @throws FormatException
	 * @throws NfcTagException
	 */
	public boolean initializeGroupTag(GroupTagModel model, Tag tag)
			throws IOException, FormatException, NfcTagException {

		// check for group permission
		int permission = model.getPermission();
		if (permission != GroupPermission.ALL_READ_WRITE
				|| permission != GroupPermission.GROUP_READ_ALL_WRITE
				|| permission != GroupPermission.GROUP_READ_WRITE
				|| permission != GroupPermission.GROUP_WRITE_ALL_READ) {

			if (mGroupErrorListener != null) {
				mGroupErrorListener.onGroupError(
						GroupErrors.ErrorCodes.ERROR_INVALID_GROUP_PERMISSION,
						GroupErrors.ErrorMsg.ERROR_INVALID_GROUP_PERMISSION);
				return false;
			}
		}

		// check for group capacity
		int capacity = model.getCapacity();
		if (capacity <= 0 || capacity > TagConstants.MAX_GROUP_CAPACITY) {
			if (mGroupErrorListener != null) {
				mGroupErrorListener.onGroupError(
						GroupErrors.ErrorCodes.ERROR_CAPACITY,
						GroupErrors.ErrorMsg.ERROR_CAPACITY);
				return false;
			}
		}
		return GroupTag.write2Tag(model, tag);
	}

	/**
	 * Interface to deal with group error, developers can override this
	 * interface to get the callback while iteracting with group tags
	 * 
	 * @author shubham
	 * 
	 */
	public interface OnGroupErrorListener {

		public void onGroupError(int code, String msg);

	}

}
