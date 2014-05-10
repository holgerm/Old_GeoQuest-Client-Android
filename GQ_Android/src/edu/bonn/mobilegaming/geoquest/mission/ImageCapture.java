package edu.bonn.mobilegaming.geoquest.mission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.qeevee.gq.res.ResourceManager;
import com.qeevee.gq.res.ResourceManager.ResourceType;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.ui.ZoomImageView;
import com.qeevee.util.Util;

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

	private String mFileName;

	private String mCurrentPhotoPath;

	// private File localImageFile = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imagecapture);

		// init Button at bottom:
		okButton = (Button) findViewById(R.id.imageCaptureStartButton);
		okButton.setOnClickListener(this);
		setMode(TAKE_PICTURE);

		// init task description text:
		taskTextView = (TextView) findViewById(R.id.imageCaptureTextView);
		taskTextView.setText(getMissionAttribute("task",
				R.string.imageCapture_taskdescription_default));

		// init upload url etc.:
		uploadURL = getMissionAttribute("uploadURL",
				XMLUtilities.OPTIONAL_ATTRIBUTE);

		mFileName = getMissionAttribute("file",
				R.string.imageCapture_file_default).toString();

		// initial image:
		imageView = (ZoomImageView) findViewById(R.id.imageCaptureImageView);
		setImage();
	}

	private void setImage() {
		CharSequence imagePath = getMissionAttribute("image",
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		if (imagePath != null) {
			this.imageView.setVisibility(View.VISIBLE);
			this.imageView.setImageBitmap(BitmapUtil.loadBitmap(
					imagePath.toString(),
					Math.round(Util.getDisplayWidth() * 0.8f), 0, true));
			this.imageView.setImageByRelativePathToBitmap(imagePath.toString());
		} else {
			// unset imageview:
			this.imageView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private static final int TAKE_PICTURE = 123;
	private static final int AFTER_UPLOAD = 2;

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
			// takePicture();
			takePicture();
			break;
		case AFTER_UPLOAD:
			finish(Globals.STATUS_SUCCEEDED);
			break;
		}
		return;
	}

	private void takePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File f = null;

		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, TAKE_PICTURE);
	}

	private File setUpPhotoFile() throws IOException {
		File f = ResourceManager.getFile(mFileName, ResourceType.IMAGE);
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
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
					mFileName));
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
			if (mCurrentPhotoPath != null) {
				setPic();
				galleryAddPic();
				mCurrentPhotoPath = null;
			}
			setMode(AFTER_UPLOAD);
		} else {
			GeoQuestApp.showMessage("Picture not taken.");
		}
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
		imageView.setImageBitmap(bitmap);
	}

	private void setMode(final int mode) {
		this.endButtonMode = mode;
		switch (mode) {
		case TAKE_PICTURE:
			okButton.setText(getMissionAttribute("buttontext",
					R.string.imageCapture_button_takePicture));
			break;
		case AFTER_UPLOAD:
			okButton.setText(getText(R.string.button_text_proceed));
			taskTextView.setText(getMissionAttribute("replyOnDone",
					R.string.imageCapture_replyDone_default));
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
