package com.qeevee.ui;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.WindowManager;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

public class BitmapUtil {

    public static Bitmap scaleBitmapToScreenWidth(Bitmap origBitmap) {
	WindowManager wm = (WindowManager) GeoQuestApp.getContext()
		.getSystemService(Context.WINDOW_SERVICE);
	int newWidth = wm.getDefaultDisplay().getWidth();
	float scaleBy = ((float) newWidth) / origBitmap.getWidth();
	Matrix matrix = new Matrix();
	matrix.postScale(scaleBy,
			 scaleBy);
	return Bitmap.createBitmap(origBitmap,
				   0,
				   0,
				   origBitmap.getWidth(),
				   origBitmap.getHeight(),
				   matrix,
				   true);
    }

    /**
     * @param filePath
     * @param context
     * @return the bitmap decoded from the given file or null, if no bitmap
     *         could be decoded.
     */
    private static Bitmap readBitmapFromFile(String filePath,
					     Context context) {
	// WindowManager wm = (WindowManager)
	// context.getSystemService(Context.WINDOW_SERVICE);
	// DisplayMetrics displayMetrics = new DisplayMetrics();
	// wm.getDefaultDisplay().getMetrics(displayMetrics);

	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inPurgeable = true;
	// options.inDensity = displayMetrics.densityDpi;
	// options.inScreenDensity = displayMetrics.densityDpi;
	// options.inTargetDensity = displayMetrics.densityDpi;
	// options.inScaled = true;

	String completedFilePath = completeImageFileSuffix(filePath);

	Bitmap bitmap = BitmapFactory.decodeFile(completedFilePath,
						 options);
	if (bitmap == null) {
	    Log.d(BitmapUtil.class.getCanonicalName(),
		  "Could not decode bitmap from file " + filePath);
	    bitmap = BitmapFactory.decodeResource(context.getResources(),
						  R.drawable.missingbitmap);
	}
	return bitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,
						int pixels) {
	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					    bitmap.getHeight(),
					    Config.ARGB_8888);
	Canvas canvas = new Canvas(output);

	final int color = 0xff424242;
	final Paint paint = new Paint();
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	final RectF rectF = new RectF(rect);
	final float roundPx = pixels;

	paint.setAntiAlias(true);
	canvas.drawARGB(0,
			0,
			0,
			0);
	paint.setColor(color);
	canvas.drawRoundRect(rectF,
			     roundPx,
			     roundPx,
			     paint);

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawBitmap(bitmap,
			  rect,
			  rect,
			  paint);

	return output;
    }

    /**
     * Loads a Bitmap and optionally scales it to the actual screen width.
     * 
     * @param scale
     *            if true the bitmap is scaled to the current screen width, else
     *            it is loaded as it is.
     * @param ressourcePath
     *            as given in the game.xml to specify e.g. images
     * @return
     */
    public static Bitmap loadBitmap(String relativeResourcePath,
				    boolean scale) {
	String bitmapFilePath = getGameBitmapFile(relativeResourcePath);
	Bitmap bitmap = readBitmapFromFile(bitmapFilePath,
					   GeoQuestApp.getContext());
	if (scale)
	    bitmap = scaleBitmapToScreenWidth(bitmap);
	return bitmap;
    }

    private static String getGameBitmapFile(String ressourceFilePath) {
	String resourcePath = GeoQuestApp.getRunningGameDir().getAbsolutePath()
		+ "/" + ressourceFilePath;
	resourcePath = completeImageFileSuffix(resourcePath);
	File file = new File(resourcePath);
	if (file.exists() && file.canRead())
	    return resourcePath;
	else
	    throw new IllegalArgumentException(
		    "No ressource file found at path \"" + resourcePath + "\".");
    }

    private static Set<String> KNOWN_BITMAP_SUFFIXES = new HashSet<String>();
    static {
	KNOWN_BITMAP_SUFFIXES.add("png");
	KNOWN_BITMAP_SUFFIXES.add("jpg");
    };

    private static String completeImageFileSuffix(String absolutePath) {
	if (hasKnownImageSuffix(absolutePath))
	    return absolutePath;
	else if (new File(absolutePath + ".png").canRead())
	    return absolutePath + ".png";
	else if (new File(absolutePath + ".jpg").canRead())
	    return absolutePath + ".jpg";
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
