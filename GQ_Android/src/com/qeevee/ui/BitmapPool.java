package com.qeevee.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class BitmapPool {

	private List<BitmapProxy> freeBitmaps;
	private List<BitmapProxy> usedBitmaps;
	private int nextToReuseIndex = 0;
	private int width, height;
	private int capacity;

	private class BitmapProxy {
		private Bitmap bitmap = null;

		Bitmap getBitmap() {
			if (bitmap == null)
				bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			return bitmap;
		}
	}

	public BitmapPool(int capacity, int width, int height) {
		this.capacity = capacity;
		freeBitmaps = new ArrayList<BitmapProxy>(capacity);
		for (int i = 0; i < capacity; i++) {
			freeBitmaps.add(i, new BitmapProxy());
		}
		usedBitmaps = new ArrayList<BitmapProxy>(capacity);
		this.width = width;
		this.height = height;
	}

	/**
	 * @return the number of currently available bitmaps that can be used
	 *         without the need to reuse some bitmap that is already in use.
	 */
	public int size() {
		return freeBitmaps.size();
	}

	public int bitmapHeight() {
		return height;
	}

	public int bitmapWidth() {
		return width;
	}

	public Bitmap consume() {
		Bitmap result = null;
		if (freeBitmaps.size() > 0) {
			BitmapProxy bp = freeBitmaps.get(0);
			result = bp.getBitmap();
			usedBitmaps.add(bp);
			freeBitmaps.remove(0);
		} else {
			// Recycle one of the already used bitmaps:
			result = usedBitmaps.get(nextToReuseIndex++).getBitmap();
			if (nextToReuseIndex == size())
				nextToReuseIndex = 0;
		}
		return result;
	}

	/**
	 * If you want to know how many objects can still freely be acquired call
	 * {@link #size()}.
	 * 
	 * @return the fixed capacity of the pool including the used places as well
	 *         as remaining free places.
	 */
	public int capacity() {
		return this.capacity;
	}
}
