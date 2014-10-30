package com.qeevee.gq;

import com.qeevee.gq.ui.InteractionBlocker;

public interface BlockableAndReleasable {

	/**
	 * Blocks user interaction options until the returned call back is called on
	 * its release method. This is for example used for disabling buttons while
	 * a sound file is playing.
	 * 
	 * @param blocker
	 *            a monitor object that is used to track who is blocking the
	 *            Activity.
	 * @return
	 */
	BlockableAndReleasable blockInteraction(InteractionBlocker blocker);

	/**
	 * @param blocker
	 *            the same object that previously blocked this object.
	 */
	void releaseInteraction(InteractionBlocker blocker);

	/**
	 * This is the primitive operation within the template method
	 * {@link #blockInteractionUntilReleased()}.
	 * <p/>
	 * Hence, concrete classes will implement this method to realize blocking of
	 * the specific interaction elements like buttons etc.
	 * <p/>
	 * Subclasses do not have to care about registering blockers and keeping
	 * track of them releasing again. They have to implement the primitive
	 * operation only, i.e. {@link #onBlockingStateUpdated(boolean)}.
	 * 
	 * @param isBlocking
	 */
	void onBlockingStateUpdated(boolean isBlocking);

}
