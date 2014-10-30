package com.qeevee.gq.loc.map;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.util.AttributeSet;

public class GQ_OSM_MapView extends MapView {

	final private int MAX_LAT_SPAN = 360000000;
	final private int MAX_LONG_SPAN = 170000000;

	public GQ_OSM_MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Zoom the map to enclose the specified bounding box, as closely as
	 * possible. Must be called after display layout is complete, or screen
	 * dimensions are not known, and will always zoom to center of zoom level 0.
	 * Suggestion: Check getScreenRect(null).getHeight() > 0
	 * 
	 * <p>
	 * Call this method only after width and height of this map have already
	 * been calculated! E.g. in an onGlobalLayoutListener within the activity.
	 */
	public void zoomToBoundingBox(final BoundingBoxE6 boundingBox) {
		int maxZoomLevelLongitude = calculateMaxZoomLevel(getWidth(),
				boundingBox.getLongitudeSpanE6(), MAX_LONG_SPAN);
		int maxZoomLevelLatitude = calculateMaxZoomLevel(getHeight(),
				boundingBox.getLatitudeSpanE6(), MAX_LAT_SPAN);
		int zoomLevel = Math.min(
				Math.min(maxZoomLevelLatitude, maxZoomLevelLongitude),
				getMaxZoomLevel());

		getController().setZoom(zoomLevel);
		getController().setCenter(
				new GeoPoint(boundingBox.getCenter().getLatitudeE6(),
						boundingBox.getCenter().getLongitudeE6()));
	}

	private int calculateMaxZoomLevel(int availableLengthInPixel, int boxSpan,
			int maxSpan) {
		double length = (double) availableLengthInPixel;
		return (int) Math.round(Math.log((maxSpan / boxSpan) * length)
				/ Math.log(2)) - 8;
	}
}
