package com.qeevee.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.qeevee.gqdefault.R;
import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.util.Device;

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
		int margin = GeoQuestApp.getContext().getResources()
				.getDimensionPixelSize(R.dimen.margin);
		Bitmap bitmap = BitmapUtil.loadBitmap(bitmapRelPath,
				Device.getDisplayWidth() - (2 * margin), 0, true);
		if (bitmap != null) {
			setImageBitmap(bitmap);
		} else {
			Log.e(TAG, "Bitmap file invalid: " + bitmapRelPath);
		}
	}
}
