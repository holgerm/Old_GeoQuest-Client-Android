package com.qeevee.gq.ui;

import com.qeevee.gq.BlockableAndReleasable;
import com.qeevee.gq.MissionOrToolActivity;

/**
 * Instances of InteractionBlocker are responsible for blocking and releasing
 * interactive objects, such as {@link MissionOrToolActivity}.
 * <p/>
 * Hence they have to call their method
 * {@link MissionOrToolActivity#blockInteraction(InteractionBlocker)}
 * to block and their method {@link BlockableAndReleasable#release()} for
 * releasing it again.
 * 
 * @author muegge
 * 
 */
public interface InteractionBlocker {

}
