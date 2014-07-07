package com.qeevee.ui;

import com.qeevee.util.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.qeevee.gq.GeoQuestApp;
import com.qeevee.gq.R;

public class ZoomImageView extends ImageView {

	private static final String TAG = ZoomImageView.class.getCanonicalName();
	private String bitmapRelPath;

	public ZoomImageView(Context context) {
		super(context);
		addZoomListener(context);
	}

	private void addZoomListener(final Context context) {
		setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Intent fullScreenIntent = new Intent(v.getContext(),
				// FullScreenImage.class);
				// fullScreenIntent.putExtra("bitmapPath", bitmapRelPath);
				// context.startActivity(fullScreenIntent);
			}
		});
	}

	public ZoomImageView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		addZoomListener(context);
	}

	public ZoomImageView(Context context, AttributeSet attributeSet,
			int defStyle) {
		super(context, attributeSet, defStyle);
		addZoomListener(context);
	}

	public void setImageByRelativePathToBitmap(String relativePath) {
		bitmapRelPath = relativePath;
		WindowManager wm = (WindowManager) GeoQuestApp.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int margin = GeoQuestApp.getContext().getResources()
				.getDimensionPixelSize(R.dimen.margin);
		Bitmap bitmap = BitmapUtil.loadBitmap(bitmapRelPath,
				Util.getDisplayWidth() - (2 * margin), 0, true);
		if (bitmap != null) {
			setImageBitmap(bitmap);
		} else {
			Log.e(TAG, "Bitmap file invalid: " + bitmapRelPath);
		}
	}
}
