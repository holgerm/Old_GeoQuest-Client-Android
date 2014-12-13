package com.qeevee.gq.ui.anim;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.qeevee.gq.base.GeoQuestActivity;
import com.qeevee.gq.base.GeoQuestApp;

public class AnimationSurfaceView extends SurfaceView {
	protected static final String TAG = AnimationSurfaceView.class
			.getCanonicalName();
	private SurfaceHolder surfaceHolder;
	private AnimationThread myThread;

	GeoQuestActivity mainActivity;
	private boolean loop;

	public AnimationSurfaceView(Context context) {
		super(context);
	}

	public AnimationSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimationSurfaceView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(Context c, String pathToAnimationArchive, int framerate,
			boolean loop) {
		if (!(c instanceof GeoQuestActivity))
			throw new IllegalArgumentException(
					"AnimationSurfaceView needs a GeoQuestActivity as Context.");

		this.loop = loop;

		loadBitmaps(pathToAnimationArchive); // TODO do in parallel in
												// background

		mainActivity = (GeoQuestActivity) c;
		myThread = new AnimationThread(this, framerate);

		surfaceHolder = getHolder();

		surfaceHolder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				myThread.setRunning(true);
				myThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				Log.w(TAG, "Surface has changed unexpectedly.");
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				synchronized (AnimationSurfaceView.this) {
					if (bitmaps != null)
						for (int i = 0; i < bitmaps.length; i++) {
							if (bitmaps[i] != null) {
								bitmaps[i].recycle();
								bitmaps[i] = null;
							}
						}
					bitmaps = null;
				}
				boolean retry = true;
				myThread.setRunning(false);
				while (retry) {
					try {
						myThread.join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}

	int nextFrameID = 0;

	protected void drawNextFrame() {
		synchronized (AnimationSurfaceView.this) {
			if (nextFrameID == bitmaps.length) {
				if (loop) {
					nextFrameID = 0;
				} else {
					myThread.setRunning(false); // why not stop()?
					return;
				}
			}

			Canvas canvas = getHolder().lockCanvas();
			if (bitmaps[nextFrameID] == null) {
				getHolder().unlockCanvasAndPost(canvas);
				return;
			}
			if (canvas == null) {
				return;
			}
			canvas.drawBitmap(bitmaps[nextFrameID++], 0, 0, null);
			getHolder().unlockCanvasAndPost(canvas);
		}
	}

	private Bitmap[] bitmaps;
	private int nrOfFrames;

	public int getNrOfFrames() {
		return nrOfFrames;
	}

	private void loadBitmaps(String pathToAnimationArchive) {
		String animationFolderName = pathToAnimationArchive.substring(0,
				pathToAnimationArchive.length() - ".zip".length());
		File animationDirPath = new File(GeoQuestApp.getRunningGameDir(),
				animationFolderName);

		// prepare image files for iteration:
		File[] frameFiles = animationDirPath.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return (filename.endsWith(".jpg") || filename.endsWith(".png") || filename
						.endsWith(".gif"));
			}
		});

		nrOfFrames = frameFiles.length;
		bitmaps = new Bitmap[nrOfFrames];

		for (int i = 0; i < nrOfFrames; i++) {
			bitmaps[i] = BitmapFactory.decodeFile(frameFiles[i]
					.getAbsolutePath());
		}
	}

}
