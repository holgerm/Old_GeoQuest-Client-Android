package com.qeevee.gq.loc;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import android.graphics.Color;


public class Route {
	
	public static final int DEFAULT_COLOR = Color.GRAY;
	public static final float DEFAULT_WIDTH = 5;
	
	static String TAG = "Route";
	private GeoPoint fromGp, toGp;
	private String id;
	private Road road;
	private int color = DEFAULT_COLOR;
	private float width = DEFAULT_WIDTH;
	private Polyline roadOverlay;

	public Route(GeoPoint from, GeoPoint to, String id) {
		
		fromGp = from;
		toGp = to;
		this.id = id;
	}

	public GeoPoint getStartingGeoPoint() {
		return fromGp;
	}
	
	public GeoPoint getEndingGeoPoint() {
		return toGp;
	}

	public String getId() {
		return id;
	}
	
	 public Road getRoad() {
		return road;
	}

	public void setRoad(Road road) {
		this.road = road;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public Polyline getRoadOverlay() {
		return roadOverlay;
	}

	public void setRoadOverlay(Polyline roadOverlay) {
		this.roadOverlay = roadOverlay;
	}		
}
