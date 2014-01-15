package com.qeevee.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {

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
		fullScreenIntent.putExtra("bitmapPath",
					  bitmapRelPath);
		context.startActivity(fullScreenIntent);
	    }
	});
    }

    public ZoomImageView(Context context,
			 AttributeSet attributeSet) {
	super(context, attributeSet);
	addZoomListener(context);
    }

    public ZoomImageView(Context context,
			 AttributeSet attributeSet,
			 int defStyle) {
	super(context, attributeSet, defStyle);
	addZoomListener(context);
    }

    public void setRelativePathToImageBitmap(String relativePath) {
	bitmapRelPath = relativePath;
	Bitmap bitmap = BitmapUtil.loadBitmap(bitmapRelPath,
					      true);
	if (bitmap != null) {
	    setImageBitmap(bitmap);
	} else {
	    throw new IllegalArgumentException("Bitmap file invalid: "
		    + bitmapRelPath);
	}
    }

}
