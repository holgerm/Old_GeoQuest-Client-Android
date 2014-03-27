package com.qeevee.gq.loc.map;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import com.qeevee.util.Util;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.R;

import android.content.Context;
import android.util.AttributeSet;

public class GQ_OSM_MapView extends MapView {

	public GQ_OSM_MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Zoom the map to enclose the specified bounding box, as closely as
	 * possible. Must be called after display layout is complete, or screen
	 * dimensions are not known, and will always zoom to center of zoom level 0.
	 * Suggestion: Check getScreenRect(null).getHeight() > 0
	 */
	public void zoomToBoundingBox(final BoundingBoxE6 boundingBox) {
		final BoundingBoxE6 currentBox = getBoundingBox(Util.getDisplayWidth()
				- 2 * R.dimen.margin, Util.getDisplayHeight() - 2
				* R.dimen.margin);

		// Calculated required zoom based on latitude span
		final double maxZoomLatitudeSpan = getZoomLevel(false) == getMaxZoomLevel() ? currentBox
				.getLatitudeSpanE6() : currentBox.getLatitudeSpanE6()
				/ Math.pow(2, getMaxZoomLevel() - getZoomLevel(false));

		if (maxZoomLatitudeSpan < 0.001) {

		}
		final double requiredLatitudeZoom = maxZoomLatitudeSpan < 0.001 ? getMaxZoomLevel()
				: getMaxZoomLevel()
						- Math.ceil(Math.log(boundingBox.getLatitudeSpanE6()
								/ maxZoomLatitudeSpan)
								/ Math.log(2));

		// Calculated required zoom based on longitude span
		final double maxZoomLongitudeSpan = getZoomLevel(false) == getMaxZoomLevel() ? currentBox
				.getLongitudeSpanE6() : currentBox.getLongitudeSpanE6()
				/ Math.pow(2, getMaxZoomLevel() - getZoomLevel(false));

		final double requiredLongitudeZoom = maxZoomLongitudeSpan < 0.001 ? getMaxZoomLevel()
				: getMaxZoomLevel()
						- Math.ceil(Math.log(boundingBox.getLongitudeSpanE6()
								/ maxZoomLongitudeSpan)
								/ Math.log(2));

		// Zoom to boundingBox center, at calculated maximum allowed zoom level
		int newZoomLevel = (int) (requiredLatitudeZoom < requiredLongitudeZoom ? requiredLatitudeZoom
				: requiredLongitudeZoom);
		getController().setZoom(newZoomLevel);
		GeoPoint newGP = new GeoPoint(boundingBox.getCenter().getLatitudeE6(),
				boundingBox.getCenter().getLongitudeE6());
		getController().setCenter(newGP);
		getController().animateTo(newGP);

	}

}
