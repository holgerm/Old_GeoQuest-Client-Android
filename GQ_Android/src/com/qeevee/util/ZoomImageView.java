package com.qeevee.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.qeevee.ui.BitmapUtil;
import com.qeevee.ui.FullScreenImage;

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
				Intent fullScreenIntent = new Intent(v.getContext(),
						FullScreenImage.class);
				fullScreenIntent.putExtra("bitmapPath", bitmapRelPath);
				context.startActivity(fullScreenIntent);
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

}
