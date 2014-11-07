package com.qeevee.gq.ui.standard;

import org.dom4j.Element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qeevee.gqtours.R;
import com.qeevee.gq.mission.NFCTagReadingProduct;
import com.qeevee.gq.ui.abstrakt.NFCTagReadingProductUI;


public class NFCTagReadingProductUIDefault extends NFCTagReadingProductUI {

	private TextView taskDescription;

	public NFCTagReadingProductUIDefault(NFCTagReadingProduct activity) {
		super(activity);
	}

	@Override
	public void init(String data) {

		if (null != taskDescription) {
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
		taskDescription = (TextView) contentView
				.findViewById(R.id.taskDescription);
		return contentView;
	}

	@Override
	protected Element getMissionXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		contentView.destroyDrawingCache();
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).removeAllViews();
		}

		contentView = null;
	}
}
