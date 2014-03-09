package com.qeevee.gq.map;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import edu.bonn.mobilegaming.geoquest.HotspotOld;

public class GoogleHotspotOverlay extends Overlay {

	private HotspotOld hotspot;
	private boolean drawCircle = false;

	public GoogleHotspotOverlay(HotspotOld hotspot) {
		this.hotspot = hotspot;
	}

	@Override
	public void draw(Canvas canvas, MapView view, boolean shadow) {
		if (!hotspot.isVisible())
			return; // If it is invisible there is not really much to
		// draw...
		Projection projection = view.getProjection();
		Point screenPoint = new Point();
		projection.toPixels(hotspot.getGeoPoint(), screenPoint);

		// draw Bitmap
		canvas.drawBitmap(hotspot.bitmap, screenPoint.x
				- hotspot.halfBitmapWidth, screenPoint.y
				- hotspot.halfBitmapHeight, null);

		// draw interaction circle
		if (drawCircle) {
			// TODO verify that this works
			float mPixRadius = (float) (view.getProjection()
					.metersToEquatorPixels(hotspot.radius) * (1 / Math.cos(Math
					.toRadians(hotspot.getGeoPoint().getLatitudeE6() / 1E6))));
			canvas.drawCircle(screenPoint.x, screenPoint.y, mPixRadius,
					hotspot.paint);
		}
	}

	/**
	 * tap handler. Tests if the current hotspots is tapped and if so starts the
	 * a new mission.
	 */
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {

		// hit test
		Projection projection = mapView.getProjection();
		Point screenPoint = new Point();
		projection.toPixels(hotspot.getGeoPoint(), screenPoint);

		RectF hitTestRect = new RectF();
		hitTestRect.set(-hotspot.bitmap.getWidth() / 2,
				-hotspot.bitmap.getHeight() / 2, hotspot.bitmap.getWidth() / 2,
				hotspot.bitmap.getHeight() / 2);

		hitTestRect.offset(screenPoint.x, screenPoint.y);

		projection.toPixels(point, screenPoint);
		if (hitTestRect.contains(screenPoint.x, screenPoint.y)) {

			if (!hotspot.isInRange)
				drawCircle = !drawCircle;
			else {
			}

			// start the event
			Log.d("", "a");
			hotspot.runOnTapEvent();

			return true;
		}
		return false;
	}
}
