package com.qeevee.gq.ui.anim;

public class AnimationThread extends Thread {
	AnimationSurfaceView myView;
	private boolean running = false;
	private int duration;

	public AnimationThread(AnimationSurfaceView view, int framerate) {
		myView = view;
		this.duration = 1000 / framerate;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		while (running) {
			myView.drawNextFrame();
		}

		try {
			sleep(duration); // TODO stabilize time independent from CPU
								// usage
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
