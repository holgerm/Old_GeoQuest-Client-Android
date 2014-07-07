package com.qeevee.util;

import com.qeevee.gq.GeoQuestApp;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class Util {

	public static int getDisplayWidth() {
		return getDisplay().getWidth();
	}

	public static int getDisplayHeight() {
		return getDisplay().getHeight();
	}

	private static Display getDisplay() {
		WindowManager wm = (WindowManager) GeoQuestApp.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay();
	}

}
