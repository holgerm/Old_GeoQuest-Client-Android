package com.qeevee.gq.mission;

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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qeevee.gqdefault.R;
import com.qeevee.gq.base.GeoQuestApp;
import com.qeevee.gq.base.Globals;
import com.qeevee.gq.res.ResourceManager;
import com.qeevee.gq.res.ResourceManager.ResourceType;
import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;
import com.qeevee.gq.xml.XMLUtilities;
import com.qeevee.ui.BitmapUtil;
import com.qeevee.ui.ZoomImageView;
import com.qeevee.util.Device;

/**
 * Photo Taking Mission.
 * 
 * @author Holger Muegge
 */

public class ImageCapture extends MissionActivity implements OnClickListener {
	private static final String TAG = "ImageCapture";

	private static final String SAVED_CURRENT_PHOTO_PATH_KEY = "currentPhotoPath";

	/** button to start the QRTag Reader */
	private Button okButton;

	private TextView taskTextView;
	private ZoomImageView imageView;

	private int endButtonMode;

	private CharSequence uploadURL;

	private String mFileName;

	private String mCurrentPhotoPath;

	// private File localImageFile = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// get mCurrentPhotoPath from bundle in case this is the call during
		// onActivityResult
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imagecapture);

		// init Button at bottom:
		okButton = (Button) findViewById(R.id.imageCaptureStartButton);
		okButton.setOnClickListener(this);

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

		// in case we are called after returning from camera app we read the
		// photo path again:
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(SAVED_CURRENT_PHOTO_PATH_KEY)) {
			setCurrentPhotoPath(savedInstanceState
					.getString(SAVED_CURRENT_PHOTO_PATH_KEY));
			setMode(AFTER_UPLOAD);
		} else {
			setMode(TAKE_PICTURE);
		}
	}

	private void setImage() {
		CharSequence imagePath = getMissionAttribute("image",
				XMLUtilities.OPTIONAL_ATTRIBUTE);
		if (imagePath != null) {
			this.imageView.setVisibility(View.VISIBLE);
			this.imageView.setImageBitmap(BitmapUtil.loadBitmap(
					imagePath.toString(),
					Math.round(Device.getDisplayWidth() * 0.8f), 0, true));
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
			setCurrentPhotoPath(f.getAbsolutePath());
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

			startActivityForResult(takePictureIntent, TAKE_PICTURE);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			f = null;
			setCurrentPhotoPath(null);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(SAVED_CURRENT_PHOTO_PATH_KEY, getCurrentPhotoPath());
		super.onSaveInstanceState(outState);
	}

	private File setUpPhotoFile() throws IOException {
		File f = ResourceManager.getFile(mFileName, ResourceType.IMAGE);
		setCurrentPhotoPath(f.getAbsolutePath());
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
			if (getCurrentPhotoPath() != null) {
				setPic();
				galleryAddPic();
				setCurrentPhotoPath(null);
			}
			setMode(AFTER_UPLOAD);
		} else {
			GeoQuestApp.showMessage("Picture not taken.");
		}
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(getCurrentPhotoPath());
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		// int targetW = imageView.getWidth();
		// int targetH = imageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(getCurrentPhotoPath(), bmOptions);
		int photoW = bmOptions.outWidth;
		// int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		// if ((targetW > 0) || (targetH > 0)) {
		// scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		// }
		scaleFactor = photoW / 600;

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(getCurrentPhotoPath(),
				bmOptions);

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
			((LinearLayout.LayoutParams) okButton.getLayoutParams()).gravity = Gravity.RIGHT;
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

	private String getCurrentPhotoPath() {
		return mCurrentPhotoPath;
	}

	private void setCurrentPhotoPath(String mCurrentPhotoPath) {
		this.mCurrentPhotoPath = mCurrentPhotoPath;
	}
}
