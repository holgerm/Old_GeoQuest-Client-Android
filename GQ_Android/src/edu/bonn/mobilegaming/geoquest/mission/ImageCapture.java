package edu.bonn.mobilegaming.geoquest.mission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.ui.ZoomImageView;

import edu.bonn.mobilegaming.geoquest.GeoQuestApp;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

/**
 * Photo Taking Mission.
 * 
 * @author Holger Muegge
 */

public class ImageCapture extends MissionActivity implements OnClickListener {
	private static final String TAG = "ImageCapture";

	/** button to start the QRTag Reader */
	private Button okButton;

	WebView webview;

	private TextView taskTextView;
	private ZoomImageView imageView;

	private int endButtonMode;

	private CharSequence uploadURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imagecapture);

		// init Button at bottom:
		okButton = (Button) findViewById(R.id.imageCaptureStartButton);
		okButton.setOnClickListener(this);
		setEndbuttonMode(TAKE_PICTURE);

		// init task description text:
		taskTextView = (TextView) findViewById(R.id.imageCaptureTextView);
		taskTextView.setText(getMissionAttribute("taskdescription",
				R.string.qrtagreading_taskdescription_default));

		// init upload url etc.:
		uploadURL = getMissionAttribute("uploadURL",
				R.string.imageCaptureUploadURLDefault);

		// initial image:
		imageView = (ZoomImageView) findViewById(R.id.imageCaptureImageView);
		setImage("initial_image");
	}

	private void setImage(String attributeName) {
		CharSequence imagePath = getMissionAttribute(attributeName,
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		if (imagePath != null) {
			this.imageView.setVisibility(View.VISIBLE);
			this.imageView.setImageBitmap(BitmapUtil.loadBitmap(
					imagePath.toString(), true));
			this.imageView.setRelativePathToImageBitmap(imagePath.toString());
		} else {
			// unset imageview:
			this.imageView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private static final int TAKE_PICTURE = 1;
	private static final int AFTER_UPLOAD = 2;

	/**
	 * File where the taken picture is stored intermediary before upload. (uses
	 * openFileOutput())
	 */
	private static final String PICTURE_FILE_NAME = "picture.jpg";

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * On Click handler for the button at the bottom.
	 */
	public void onClick(View v) {
		switch (endButtonMode) {
		case TAKE_PICTURE:
			Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intentTakePhoto, TAKE_PICTURE);
			break;
		case AFTER_UPLOAD:
			finish(Globals.STATUS_SUCCEEDED);
			break;
		}
		return;
	}

	public void uploadBitmap(Bitmap bitmap) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(uploadURL.toString());

		try {
			// Adding data:
			MultipartEntity requestEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			requestEntity.addPart("uploaded_file", new InputStreamBody(
					new ByteArrayInputStream(bao.toByteArray()), "image/jpeg",
					PICTURE_FILE_NAME));
			requestEntity.addPart("secret", new StringBody(TAG));
			httppost.setEntity(requestEntity);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			taskTextView.setText(makeReadableResponse(response));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CharSequence makeReadableResponse(HttpResponse response) {
		HttpEntity responseEntity = response.getEntity();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			responseEntity.writeTo(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String responseText = os.toString();
		if (responseText.startsWith("OK")) {
			return getText(R.string.imageCapture_UploadOK);
		} else {
			String errno = responseText.substring(0, responseText.indexOf(':'));
			return getText(R.string.imageCapture_UploadERROR) + "  Error: "
					+ errno;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
			Bitmap bitmap = (Bitmap) intent.getExtras().get("data");

			// redefine UI:
			imageView.setImageBitmap(bitmap);
			uploadBitmap(bitmap);
			setEndbuttonMode(AFTER_UPLOAD);

		} else {
			GeoQuestApp.showMessage("Picture not taken.");
		}
	}

	private void setEndbuttonMode(final int mode) {
		this.endButtonMode = mode;
		switch (mode) {
		case TAKE_PICTURE:
			okButton.setText(getMissionAttribute("buttontext",
					R.string.qrtagreading_startscanbutton_default));
			break;
		case AFTER_UPLOAD:
			okButton.setText(getText(R.string.button_text_proceed));
			break;
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
