package com.qeevee.gq.loc;


public class Distance {
	
	/** 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return distance between point 1 and point 2 in kilometers
	 */
	static public double distance(double lat1, double lon1, double lat2,
			double lon2) {
		// Distance calculation taken from:
		// http://www.zipcodeworld.com/samples/distance.java.html
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist) * 60 * 1.1515 * 1.609344;
		return (dist);
	}

	static private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	static private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}


}
  
