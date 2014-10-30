package com.qeevee.gq.ui;

import java.util.ArrayList;
import java.util.List;

import com.qeevee.gq.BlockableAndReleasable;

import android.util.Log;

public class InteractionBlockingManager {

    private BlockableAndReleasable blockable;

    public InteractionBlockingManager(BlockableAndReleasable blockable) {
	this.blockable = blockable;
    }

    protected List<InteractionBlocker> blockers = new ArrayList<InteractionBlocker>();

    public BlockableAndReleasable
	    blockInteraction(InteractionBlocker newBlocker) {
	blockers.add(newBlocker);
	if (blockers.size() == 1) {
	    blockable.onBlockingStateUpdated(true);
	    // primitive operation implemented by subclasses
	}
	return blockable; // i.e. the release() method of the subclass will be
			  // called
    }

    public void releaseInteraction(InteractionBlocker blocker) {
	if (!blockers.contains(blocker)) {
	    Log.e(this.getClass().getSimpleName(),
		  "Tried to release a non registered blocker: "
			  + blocker.toString());
	    return;
	}
	// blocker is registered:
	blockers.remove(blocker);
	if (blockers.isEmpty()) {
	    // primitive operation implemented by subclasses
	    blockable.onBlockingStateUpdated(false);
	}
    }

    public boolean isBlocking() {
	return blockers.isEmpty();
    }

}
