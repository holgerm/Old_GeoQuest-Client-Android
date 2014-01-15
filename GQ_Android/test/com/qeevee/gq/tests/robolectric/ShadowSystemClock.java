/**
 * Implementation for the diploma thesis "Adaption in digitalen mobilen Lernspielen - Anwendung in GeoQuest"
 * 
 * @author Sabine Polko
 */
package com.qeevee.gq.tests.robolectric;

import android.os.SystemClock;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(SystemClock.class)
public class ShadowSystemClock {
    private static long mockTime = 0;

    @Implementation
    public static void setCurrentTimeMillis(long milliseconds) {
	mockTime = milliseconds;
    }

    @Implementation
    public static long elapsedRealtime() {
	return mockTime;
    }

}
