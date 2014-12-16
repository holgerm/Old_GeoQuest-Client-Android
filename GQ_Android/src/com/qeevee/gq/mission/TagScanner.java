package com.qeevee.gq.mission;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.base.Globals;
import com.qeevee.gq.base.Variables;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.gqdefault.R;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.util.Device;

/**
 * Convenient Tag Scanner Class that represents QR Code Scanner as well as NFC
 * Code Scanner.
 * 
 * @author Holger Muegge
 */

public class TagScanner extends InteractiveMission implements OnClickListener {
	private static final String TAG = TagScanner.class.getCanonicalName();

	/** button to start the QRTag Reader */
	private Button okButton;

	private TextView taskTextView;
	private ImageView imageView;

	private static final int START_SCAN = 1;
	private static final int END_MISSION = 2;
	private int buttonMode;

	private static final int QRCODE = 1;
	private static final int NFCCODE = 2;
	private int mode;

	private static final int TREASURE = 1;
	private static final int PRODUCT = 2;
	private int expectationMode = TREASURE;

	private List<CharSequence> expectedCodes;

	private CharSequence ifWrongText;
	private CharSequence ifRightText;
	private CharSequence feedbackText;
	private CharSequence endButtonText;
	private CharSequence scanButtonText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tag_scanner); // TODO TagScanner Layout machen

		// init Start Scan Button at bottom:
		okButton = (Button) findViewById(R.id.tagscanner_startbutton);
		scanButtonText = getMissionAttribute("buttontext",
				R.string.tagscanner_startscanbutton_default);
		okButton.setText(scanButtonText);
		okButton.setOnClickListener(this);
		// init endbuttontext:
		this.endButtonText = getMissionAttribute("endbuttontext",
				R.string.button_text_proceed);
		buttonMode = START_SCAN;

		// init task description text:
		taskTextView = (TextView) findViewById(R.id.tsTextView);
		taskTextView.setText(getMissionAttribute("taskdescription",
				R.string.tagscanner_taskdescription_default));

		// initial image:
		imageView = (ImageView) findViewById(R.id.qrImageView);
		setImage("initial_image");

		// init scan mode:
		String modeS = mission.xmlMissionNode.attributeValue("mode");
		this.mode = (modeS == null || modeS.equals("QR-Code")) ? QRCODE
				: NFCCODE;

		// init expectations:
		expectedCodes = new ArrayList<CharSequence>();
		for (Element elem : this.getMissionElements("expectedCode")) {
			expectedCodes.add(elem.getText());
		}
		this.expectationMode = (expectedCodes.size() == 0) ? TREASURE : PRODUCT;

		// init feedback:
		this.feedbackText = getMissionAttribute("feedbacktext",
				R.string.qrtagreader_treasure_feedback);
		this.ifRightText = getMissionAttribute("if_right",
				R.string.qrtagreader_product_ifright);
		this.ifWrongText = getMissionAttribute("if_wrong",
				R.string.qrtagreader_product_ifwrong);
	}

	private void setImage(String attributeName) {
		CharSequence imagePath = getMissionAttribute(attributeName,
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		if (imagePath != null) {
			this.imageView.setVisibility(View.VISIBLE);
			this.imageView.setImageBitmap(BitmapUtil.loadBitmap(
					imagePath.toString(),
					Math.round(Device.getDisplayWidth() * 0.8f), 0, true));
		} else {
			// unset imageview:
			this.imageView.setVisibility(View.GONE);
		}
	}

	private static final String TOKEN_SCAN_RESULT = "@result@";

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * On Click handler for the button at the bottom.
	 */
	public void onClick(View v) {
		switch (buttonMode) {
		case END_MISSION:
			finish(Globals.STATUS_SUCCEEDED);
			break;
		case START_SCAN:
			if (mode == QRCODE) {
				// Start QR Code Reader:
				Intent intentScan = new Intent(
						"com.google.zxing.client.android.SCAN");
				intentScan.addCategory(Intent.CATEGORY_DEFAULT);
				try {
					startActivityForResult(intentScan, 0x0ba7c0de);
				} catch (ActivityNotFoundException e) {
					showDownloadDialog(this, DEFAULT_TITLE, DEFAULT_MESSAGE,
							DEFAULT_YES, DEFAULT_NO);
				}
			} else {
				// TODO Start NFC Scan:
				Intent intentScan = null;
				try {
					intentScan = new Intent(GeoQuestApp.getContext(),
							Class.forName(MissionActivity.getPackageBaseName()
									+ "NFCScanMission"));
				} catch (ClassNotFoundException e1) {
					Log.e(TAG,
							"Unable to start intent for class "
									+ e1.getMessage());
					return;
				}
				intentScan.addCategory(Intent.CATEGORY_DEFAULT);
				startActivityForResult(intentScan, 2);
			}
			break;
		default:
			Log.e(TAG, "unknown buttonMode: " + buttonMode);
		}
		return;
	}

	public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
	public static final String DEFAULT_MESSAGE = "This application requires Barcode Scanner. Would you like to install it?";
	public static final String DEFAULT_YES = "Yes";
	public static final String DEFAULT_NO = "No";

	private AlertDialog showDownloadDialog(final Activity activity,
			CharSequence stringTitle, CharSequence stringMessage,
			CharSequence stringButtonYes, CharSequence stringButtonNo) {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
		downloadDialog.setTitle(stringTitle);
		downloadDialog.setMessage(stringMessage);
		downloadDialog.setPositiveButton(stringButtonYes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Uri uri = Uri
								.parse("market://search?q=pname:com.google.zxing.client.android");
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						activity.startActivity(intent);
					}
				});
		downloadDialog.setNegativeButton(stringButtonNo,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				});
		return downloadDialog.show();
	}

	public void onPause() {
		super.onPause();
	}

	public void onStop() {
		super.onStop();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (intent != null && scanResult != null) {
			String scannedResult = scanResult.getContents();
			// set scanned result in mission specific variable:
			Variables.registerMissionResult(mission.id, scannedResult);

			// handle scan result depending on mode:
			switch (expectationMode) {
			case TREASURE:
				taskTextView.setText(this.feedbackText.toString().replaceAll(
						TOKEN_SCAN_RESULT, scannedResult));
				setImage("if_right_image");
				buttonMode = END_MISSION;
				okButton.setText(endButtonText);
				invokeOnSuccessEvents();
				break;
			case PRODUCT:
				// check content:
				boolean asExpected = false;
				for (CharSequence curExpect : expectedCodes) {
					if (scannedResult.equals(curExpect)) {
						asExpected = true;
						break;
					}
				}
				if (asExpected) {
					taskTextView.setText(this.ifRightText);
					setImage("if_right_image");
					buttonMode = END_MISSION;
					okButton.setText(endButtonText);
					invokeOnSuccessEvents();
				} else {
					taskTextView.setText(this.ifWrongText);
					setImage("if_wrong_image");
					buttonMode = START_SCAN;
					okButton.setText(scanButtonText);
					invokeOnFailEvents();
				}
				break;
			default:
				Log.e(TAG, "undefined QRTagReading mission mode: "
						+ expectationMode);
			}
		} else {
			buttonMode = START_SCAN;
			okButton.setText(scanButtonText);
			setImage("initial_image");
			taskTextView.setText(getMissionAttribute("taskdescription",
					R.string.tagscanner_taskdescription_default));

			// taskTextView.setText(R.string.error_qrtagreader_noresult);
			// Log.e(TAG, "scanning rsulted in null");
		}
	}

	public void onBlockingStateUpdated(boolean blocking) {
		// TODO Auto-generated method stub
	}

	public MissionOrToolUI getUI() {
		// TODO Auto-generated method stub
		return null;
	}
}