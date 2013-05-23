package edu.bonn.mobilegaming.geoquest.ui.standard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.mission.NFCScanMission;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.NFCScanMissionUI;

public class NFCScanMissionUIDefault extends NFCScanMissionUI {

	private TextView info;
	private ImageView imageView;
	private Button button;
	private EditText userInput;

	private int mode = 0;
	private static final int MODE_END = 2;

	private OnClickListener endMissionListener = new OnClickListener() {
		public void onClick(View v) {
			getNFCScanMission().finishMission();
		}
	};

	public NFCScanMissionUIDefault(NFCScanMission activity) {
		super(activity);
	}

	public void onBlockingStateUpdated(boolean isBlocking) {

	}

	@Override
	public void init() {

		// check for user input case
		if ("true".equals(getNFCScanMission().getVerifyUserInput())) {

			setUserInputVisibility(true);
		} else {
			setUserInputVisibility(false);
		}

		// set initial image
		String imagePath = getNFCScanMission().getInitImg();
		if (null != imagePath && !("".equals(imagePath))) {
			setImage(imagePath);
		}

		preTagWriteView(getNFCScanMission().getDescription(), false);
		setButtonMode(MODE_END);
	}

	@Override
	public View createView() {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.nfcscanmission, null);

		info = (TextView) view.findViewById(R.id.info);
		imageView = (ImageView) view.findViewById(R.id.imageView);
		button = (Button) view.findViewById(R.id.button);
		userInput = (EditText) view.findViewById(R.id.userInput);

		button.setText("Proceed");
		return view;
	}

	/**
	 * Until NFC tag is not scanned make view and write button disable
	 */
	private void preTagWriteView(String type, boolean mode) {

		info.setText(type);
		button.setEnabled(mode);
	}

	private void setButtonMode(int newMode) {
		if (mode == newMode)
			return;
		mode = newMode;
		switch (mode) {
		case MODE_END:
			button.setOnClickListener(endMissionListener);
			break;
		}
	}

	@Override
	public void success() {

		// set initial image
		String imagePath = getNFCScanMission().getSuccImg();
		if (null != imagePath && !("".equals(imagePath))) {
			setImage(imagePath);
		}

		preTagWriteView(getNFCScanMission().getSuccessMsg(), true);
	}

	@Override
	public void success(String data) {

		// set initial image
		String imagePath = getNFCScanMission().getSuccImg();
		if (null != imagePath && !("".equals(imagePath))) {
			setImage(imagePath);
		}

		preTagWriteView(getNFCScanMission().getSuccessMsg() + " " + data, true);
	}

	@Override
	public boolean verifyUserInput(String data) {

		String text = userInput.getEditableText().toString();
		return data.equals(text);
	}

	private void setUserInputVisibility(boolean visibility) {

		if (visibility) {
			userInput.setVisibility(View.VISIBLE);
		} else {
			userInput.setVisibility(View.GONE);
		}
	}

	private void setImage(String imagePath) {

		if (imagePath != null) {
			this.imageView.setVisibility(View.VISIBLE);
			this.imageView.setImageBitmap(BitmapUtil.loadBitmap(
					imagePath.toString(), true));
		} else {
			// unset imageview:
			this.imageView.setVisibility(View.GONE);
		}
	}

	@Override
	public void enableButton(boolean enable) {
		button.setEnabled(enable);
	}

}
