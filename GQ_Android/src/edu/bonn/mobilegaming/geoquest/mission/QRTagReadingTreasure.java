package edu.bonn.mobilegaming.geoquest.mission;

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
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;

import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.Variables;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

/**
 * QR Tag Reading Mission.
 * 
 * The following modes are supported:
 * 
 * <ol>
 * <li>Finding a treasure (as text). Attributes must be:
 * <ul>
 * <li>{@code mode="treasure".} This is the default mode, if the {@code mode}
 * attribute is omitted.
 * <li>{@code initial_image} local path to an image that is shown before the
 * mission has been done.
 * <li>{@code if_right_image} local path to an image that is shown after the
 * treasure has been found (i.e. bar code has been scanned)
 * <li>The tag itself should then contain a simple text which is shown to the
 * player after he scanned the tag.. Later on images, links to a web page,
 * sound, or movies could be supported by a special formatted link within the
 * encoded text.
 * </ul>
 * <li>Finding a real world object (e.g. a book). Attributes must be:
 * <ul>
 * <li>{@code mode="product"},
 * <li>{@code initial_image} local path to an image that is shown before the
 * mission has been done.
 * <li>{@code expected_content} must contain the real products data,
 * <li>The Tag itself must encode the given product data in the given format.
 * <li>The attribute {@code if_wrong} may contain text that is shown to the
 * player when he scans a wrong tag. If not specified the generic resource
 * string {@code qrtagreader_product_ifwrong} is used.
 * <li>{@code if_wrong_image} local path to an image that is shown after a wrong
 * scan.
 * <li>The attribute {@code if_right} may contain text that is shown to the
 * player when he scans the right tag. If not specified the generic resource
 * string {@code qrtagreader_product_ifright} is used.
 * <li>{@code if_right_image} local path to an image that is shown after the
 * searched product has been found (i.e. the right bar code has been scanned)
 * </ul>
 * </ol>
 * 
 * Independent of the mode, the {@code taskdescription} attribute should contain
 * the descriptive text shown to the player before he plays the QR Tag Reading
 * mission. <br/>
 * <br/>
 * TODO: Add an optional image shown above the description text.
 * 
 * For details see wiki documentation page.
 * 
 * @author Holger Muegge
 */

public class QRTagReadingTreasure extends InteractiveMission implements
	OnClickListener {
    private static final String TAG = "QRTagReadingProduct";

    /** button to start the QRTag Reader */
    private Button okButton;

    private TextView taskTextView;
    private ImageView imageView;

    private static final int START_SCAN = 1;
    private static final int END_MISSION = 2;

    private int buttonMode;

    private CharSequence feedbackText;
    private CharSequence endButtonText;
    private CharSequence scanButtonText;

    private String scannedResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.qrtagreading);

	// init Start Scan Button at bottom:
	okButton = (Button) findViewById(R.id.qrtagreaderstartbutton);
	scanButtonText = getMissionAttribute("buttontext",
					     R.string.qrtagreading_startscanbutton_default);
	okButton.setText(scanButtonText);
	okButton.setOnClickListener(this);
	// init endbuttontext:
	this.endButtonText = getMissionAttribute("endbuttontext",
						 R.string.button_text_proceed);
	buttonMode = START_SCAN;

	// init task description text:
	taskTextView = (TextView) findViewById(R.id.qrTextView);
	taskTextView
		.setText(getMissionAttribute("taskdescription",
					     R.string.qrtagreading_taskdescription_default));

	// initial image:
	imageView = (ImageView) findViewById(R.id.qrImageView);
	setImage("initial_image");

	this.feedbackText = getMissionAttribute("feedbacktext",
						R.string.qrtagreader_treasure_feedback);
    }

    private void setImage(String attributeName) {
	CharSequence imagePath = getMissionAttribute(attributeName,
						     XMLUtilities.OPTIONAL_ATTRIBUTE);
	if (imagePath != null) {
	    this.imageView.setVisibility(View.VISIBLE);
	    this.imageView.setImageBitmap(BitmapUtil
		    .loadBitmap(imagePath.toString(),
				true));
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
	    Intent intentScan = new Intent(
		    "com.google.zxing.client.android.SCAN");
	    intentScan.addCategory(Intent.CATEGORY_DEFAULT);
	    try {
		startActivityForResult(intentScan,
				       0x0ba7c0de);
	    } catch (ActivityNotFoundException e) {
		showDownloadDialog(this,
				   DEFAULT_TITLE,
				   DEFAULT_MESSAGE,
				   DEFAULT_YES,
				   DEFAULT_NO);
	    }
	    break;
	default:
	    Log.e(TAG,
		  "unknown buttonMode: " + buttonMode);
	}
	return;
    }

    public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
    public static final String DEFAULT_MESSAGE = "This application requires Barcode Scanner. Would you like to install it?";
    public static final String DEFAULT_YES = "Yes";
    public static final String DEFAULT_NO = "No";

    private AlertDialog showDownloadDialog(final Activity activity,
					   CharSequence stringTitle,
					   CharSequence stringMessage,
					   CharSequence stringButtonYes,
					   CharSequence stringButtonNo) {
	AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
	downloadDialog.setTitle(stringTitle);
	downloadDialog.setMessage(stringMessage);
	downloadDialog.setPositiveButton(stringButtonYes,
					 new DialogInterface.OnClickListener() {
					     public
						     void
						     onClick(DialogInterface dialogInterface,
							     int i) {
						 Uri uri = Uri
							 .parse("market://search?q=pname:com.google.zxing.client.android");
						 Intent intent = new Intent(
							 Intent.ACTION_VIEW,
							 uri);
						 activity.startActivity(intent);
					     }
					 });
	downloadDialog.setNegativeButton(stringButtonNo,
					 new DialogInterface.OnClickListener() {
					     public
						     void
						     onClick(DialogInterface dialogInterface,
							     int i) {
					     }
					 });
	return downloadDialog.show();
    }

    public void onActivityResult(int requestCode,
				 int resultCode,
				 Intent intent) {
	IntentResult scanResult = IntentIntegrator
		.parseActivityResult(requestCode,
				     resultCode,
				     intent);
	if (intent != null && scanResult != null) {
	    scannedResult = scanResult.getContents();
	    // set scanned result in mission specific variable:
	    Variables.registerMissionResult(mission.id,
					    scannedResult);
	    invokeOnSuccessEvents();

	    // handle scan result:
	    taskTextView.setText(this.feedbackText.toString()
		    .replaceAll(TOKEN_SCAN_RESULT,
				scannedResult));
	    setImage("if_right_image");
	    buttonMode = END_MISSION;
	    okButton.setText(endButtonText);
	} else {
	    buttonMode = START_SCAN;
	    okButton.setText(scanButtonText);
	    setImage("initial_image");
	    taskTextView
		    .setText(getMissionAttribute("taskdescription",
						 R.string.qrtagreading_taskdescription_default));
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
