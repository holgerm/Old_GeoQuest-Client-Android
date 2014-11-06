package com.qeevee.util;

import com.qeevee.gq.base.GeoQuestApp;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class Device {

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
