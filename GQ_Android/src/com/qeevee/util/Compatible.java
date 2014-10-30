package com.qeevee.util;

import android.os.AsyncTask;
import android.os.Build;

public class Compatible {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void execute(AsyncTask task, Object... params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		else
			task.execute(params);

	}

}
