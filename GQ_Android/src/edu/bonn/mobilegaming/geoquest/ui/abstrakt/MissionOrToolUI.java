package edu.bonn.mobilegaming.geoquest.ui.abstrakt;

import edu.bonn.mobilegaming.geoquest.BlockableAndReleasable;

public interface MissionOrToolUI extends BlockableAndReleasable {

	/**
	 * Disables some UI functionality e.g. when onPause() on the holding
	 * activity is called.
	 * 
	 * Concrete subclasses should implement their own version of this method if
	 * needed.
	 */
	void disable();

	/**
	 * Enables some UI functionality e.g. when onResume() on the holding
	 * activity is called.
	 * 
	 * Concrete subclasses should implement their own version of this method if
	 * needed.
	 */
	void enable();

	/**
	 * Releases all objects so that memory leaks are avaoided, e.g. Bitmaps are not referenced any more.
	 */
	void release();

}
