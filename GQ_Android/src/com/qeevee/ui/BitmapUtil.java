package com.qeevee.ui;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.qeevee.gq.res.ResourceManager;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class BitmapUtil {

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();

		return output;
	}

	public static Bitmap loadBitmap(String relativeResourcePath, int reqWidth,
			int reqHeight, boolean rounded) {
		Bitmap bmp;
		String path = completeImageFileSuffix(ResourceManager
				.getResourcePath(relativeResourcePath));
		// String path =
		// completeImageFileSuffix(getGameBitmapFile(relativeResourcePath));

		if (path == null) {
			bmp = loadBitmapFromResource(R.drawable.missingbitmap, reqWidth,
					reqHeight, rounded);
			return bmp;
		} else {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bmp = BitmapFactory.decodeFile(path, options);
		}
		if (rounded) {
			int radius = GeoQuestApp.getContext().getResources()
					.getDimensionPixelSize(R.dimen.button_corner_radius);
			bmp = getRoundedCornerBitmap(bmp, radius);
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
		if (hasKnownImageSuffix(absolutePath))
			return absolutePath;
		else if (new File(absolutePath + ".png").canRead())
			return absolutePath + ".png";
		else if (new File(absolutePath + ".jpg").canRead())
			return absolutePath + ".jpg";
		else if (new File(absolutePath + ".gif").canRead())
			return absolutePath + ".gif";
		else
			throw new IllegalArgumentException(
					"Invalid image path (not found): " + absolutePath);
	}

	private static boolean hasKnownImageSuffix(String path) {
		int suffixStartingIndex = path.lastIndexOf('.');
		if (suffixStartingIndex <= 0)
			return false;
		else {
			String suffix = path.substring(suffixStartingIndex + 1)
					.toLowerCase(Locale.US);
			return KNOWN_BITMAP_SUFFIXES.contains(suffix);
		}
	}
}
