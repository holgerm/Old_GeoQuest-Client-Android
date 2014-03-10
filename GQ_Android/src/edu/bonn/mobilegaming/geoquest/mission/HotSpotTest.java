package edu.bonn.mobilegaming.geoquest.mission;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import edu.bonn.mobilegaming.geoquest.Hotspot;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public class HotSpotTest extends MissionActivity {

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotspottest);

		String hotspotID = mission.xmlMissionNode.attributeValue("targetID");
		Hotspot hotspot = Hotspot.get(hotspotID);
		GeoPoint gp = hotspot.getGeoPoint();
		int longitude = gp.getLongitudeE6();
		int latitude = gp.getLatitudeE6();

		TextView nameView = (TextView) findViewById(R.id.hotspotName);
		nameView.setText(hotspot.getName());
		TextView latView = (TextView) findViewById(R.id.latitude);
		latView.setText(String.valueOf(latitude));
		TextView longView = (TextView) findViewById(R.id.longitude);
		longView.setText(String.valueOf(longitude));

	}

}
