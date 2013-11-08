package edu.bonn.mobilegaming.geoquest.ui.standard;

import org.dom4j.Element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.NFCTagReadingProduct;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCTagReadingProductUI;

public class NFCTagReadingProductUIDefault extends NFCTagReadingProductUI {

	
	private TextView taskDescription;
	
	public NFCTagReadingProductUIDefault(NFCTagReadingProduct activity) {
		super(activity);
	}

	@Override
	public void init(String data) {

		if(null != taskDescription){
			taskDescription.setText(data);
		}
		
	}

	public void onBlockingStateUpdated(boolean isBlocking) {
		// TODO Auto-generated method stub

	}

	@Override
	public View createContentView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		contentView = inflater.inflate(R.layout.nfc, null);
		taskDescription = (TextView) contentView.findViewById(R.id.taskDescription);
		return contentView;
	}

	@Override
	protected Element getMissionXML() {
		// TODO Auto-generated method stub
		return null;
	}
}
