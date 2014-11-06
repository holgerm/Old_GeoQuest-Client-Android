package com.qeevee.ui;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qeevee.gq.R;
import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.res.ResourceManager;
import com.qeevee.gq.res.ResourceManager.ResourceType;

public class BitmapUtil {

	private static final String TAG = BitmapUtil.class.getCanonicalName();

	private static Map<BitmapType, BitmapPool> pools;

	public static void initializePools() {
		pools = new HashMap<BitmapType, BitmapPool>(BitmapType.values().length);
		pools.put(BitmapType.FULLSCREEN, new BitmapPool(
				BitmapType.FULLSCREEN.capacity, 405, 688));
	}

	public static BitmapPool getPool(BitmapType bitmapType) {
		return pools.get(bitmapType);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		// Bitmap output = null;
		// output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
		// Config.ARGB_8888);
		// Canvas canvas = new Canvas(output);
		//
		// final int color = 0xff424242;
		// final Paint paint = new Paint();
		// final Rect rect = new Rect(0, 0, bitmap.getWidth(),
		// bitmap.getHeight());
		// final RectF rectF = new RectF(rect);
		// final float roundPx = pixels;
		//
		// paint.setAntiAlias(true);
		// canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(color);
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		//
		// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// canvas.drawBitmap(bitmap, rect, rect, paint);
		//
		// return output;
		return bitmap;
	}

	public static Bitmap loadBitmap(String relativeResourcePath, int width,
			int height, boolean rounded) {
		Bitmap bmp;
		String path = completeImageFileSuffix(ResourceManager.getResourcePath(
				relativeResourcePath, ResourceType.IMAGE));

		try {
			if (path == null) {
				Log.e(TAG, "Path to Bitmap was null.");
				return GeoQuestApp.getInstance().getMissingBitmap();
			} else {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.inMutable = true;
				options.inPurgeable = true;
				options.inInputShareable = true;
				// options.inBitmap = lookupBitmap(width, height);
				// addInBitmapOptions(options);
				bmp = BitmapFactory.decodeFile(path, options);
				if (bmp == null) {
					Log.e(TAG, "Bitmap could not be decoded (path: " + path);
					return GeoQuestApp.getInstance().getMissingBitmap();
				}
			}
			if (rounded) {
				int radius = GeoQuestApp.getContext().getResources()
						.getDimensionPixelSize(R.dimen.button_corner_radius);
				bmp = getRoundedCornerBitmap(bmp, radius);
			}
		} catch (OutOfMemoryError oome) {
			Log.e(TAG, "OutOfMemoryError catched trying to load bitmap for "
					+ relativeResourcePath);
			bmp = GeoQuestApp.getInstance().getMissingBitmap();
		}
		return bmp;
	}

	public static Bitmap loadBitmapFromResource(int resourceID, int reqWidth,
			int reqHeight, boolean rounded) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(GeoQuestApp.getContext().getResources(),
				resourceID, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeResource(GeoQuestApp.getContext()
				.getResources(), resourceID, options);
		bmp = BitmapFactory.decodeResource(GeoQuestApp.getContext()
				.getResources(), resourceID);
		if (rounded) {
			int radius = GeoQuestApp.getContext().getResources()
					.getDimensionPixelSize(R.dimen.button_corner_radius);
			bmp = getRoundedCornerBitmap(bmp, radius);
		}
		return bmp;
	}

	// private static Set<SoftReference<Bitmap>> reusableBitmaps = Collections
	// .synchronizedSet(new HashSet<SoftReference<Bitmap>>());
	//
	// @SuppressWarnings("unused")
	// private static void addInBitmapOptions(BitmapFactory.Options options) {
	// // inBitmap only works with mutable bitmaps, so force the decoder to
	// // return mutable bitmaps.
	// options.inMutable = true;
	//
	// // Try to find a bitmap to use for inBitmap.
	// Bitmap inBitmap = getBitmapFromReusableSet(options);
	//
	// if (inBitmap != null) {
	// // If a suitable bitmap has been found, set it as the value of
	// // inBitmap.
	// options.inBitmap = inBitmap;
	// }
	// }
	//
	// public static void addBitmapToSetOfReusables(Bitmap bmp) {
	// reusableBitmaps.add(new SoftReference<Bitmap>(bmp));
	// }
	//
	// // This method iterates through the reusable bitmaps, looking for one
	// // to use for inBitmap:
	// private static Bitmap getBitmapFromReusableSet(BitmapFactory.Options
	// options) {
	// Bitmap bitmap = null;
	//
	// if (reusableBitmaps != null && !reusableBitmaps.isEmpty()) {
	// synchronized (reusableBitmaps) {
	// final Iterator<SoftReference<Bitmap>> iterator = reusableBitmaps
	// .iterator();
	// Bitmap item;
	//
	// while (iterator.hasNext()) {
	// item = iterator.next().get();
	//
	// if (null != item && item.isMutable()) {
	// // Check to see it the item can be used for inBitmap.
	// if (canUseForInBitmap(item, options)) {
	// bitmap = item;
	//
	// // Remove from reusable set so it can't be used
	// // again.
	// iterator.remove();
	// break;
	// }
	// } else {
	// // Remove from the set if the reference has been
	// // cleared.
	// iterator.remove();
	// }
	// }
	// }
	// }
	// return bitmap;
	// }
	//
	// static boolean canUseForInBitmap(Bitmap candidate,
	// BitmapFactory.Options targetOptions) {
	//
	// // On earlier versions than 4.4 (Kitkat), the dimensions must match
	// // exactly and the inSampleSize must be 1
	// return candidate.getWidth() == targetOptions.outWidth
	// && candidate.getHeight() == targetOptions.outHeight
	// && targetOptions.inSampleSize == 1;
	// }

	/**
	 * A helper function to return the byte usage per pixel of a bitmap based on
	 * its configuration.
	 */
	static int getBytesPerPixel(Config config) {
		if (config == Config.ARGB_8888) {
			return 4;
		} else if (config == Config.RGB_565) {
			return 2;
		} else if (config == Config.ARGB_4444) {
			return 2;
		} else if (config == Config.ALPHA_8) {
			return 1;
		}
		return 1;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (reqWidth == 0 && reqHeight > 0) {
			if (height > reqHeight) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
		}
		if (reqHeight == 0 && reqWidth > 0) {
			if (width > reqWidth) {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		if (reqHeight > 0 && reqWidth > 0) {
			int widthProportion = Math.round((float) width / (float) reqWidth);
			int heightProportion = Math.round((float) height
					/ (float) reqHeight);
			inSampleSize = widthProportion > heightProportion ? heightProportion
					: widthProportion;
		}

		return inSampleSize;
	}

	private static Set<String> KNOWN_BITMAP_SUFFIXES = new HashSet<String>();
	static {
		KNOWN_BITMAP_SUFFIXES.add("png");
		KNOWN_BITMAP_SUFFIXES.add("jpg");
		KNOWN_BITMAP_SUFFIXES.add("gif");
	};

	private static String completeImageFileSuffix(String absolutePath) {
		if (absolutePath == null)
			throw new IllegalArgumentException("Invalid image path (null)."
					+ absolutePath);
		if (hasKnownImageSuffix(absolutePath))
			return absolutePath;
		else if (new File(absolutePath + ".png").canRead())
			return absolutePath + ".png";
		else if (new File(absolutePath + ".jpg").canRead())
			return absolutePath + ".jpg";
		else if (new File(absolutePath + ".gif").canRead())
			return absolutePath + ".gif";
		else
			return null;
	}

	private static boolean hasKnownImageSuffix(String path) {
		if (path == null)
			return false;
		int suffixStartingIndex = path.lastIndexOf('.');
		if (suffixStartingIndex <= 0)
			return false;
		else {
			String suffix = path.substring(suffixStartingIndex + 1)
					.toLowerCase(Locale.US);
			return KNOWN_BITMAP_SUFFIXES.contains(suffix);
		}
	}

	private static Map<String, Bitmap> bitmapCache = new HashMap<String, Bitmap>();

	private static Bitmap lookupBitmap(int width, int height) {
		String key = generateKey(width, height);
		Bitmap reusableBitmap = bitmapCache.get(key);
		if (reusableBitmap == null) {
			reusableBitmap = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
			bitmapCache.put(generateKey(width, height), reusableBitmap);
		}
		return reusableBitmap;
	}

	@SuppressLint("DefaultLocale")
	private static String generateKey(int width, int height) {
		return String.format("%05d.%05d", width, height);
	}

	public static void clearBitmapCache() {
		for (Iterator<Entry<String, Bitmap>> iterator = bitmapCache.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, Bitmap> entry = iterator.next();
			entry.getValue().recycle();
		}
		bitmapCache.clear();
	}

}
