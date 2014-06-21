package com.example.recognizer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.glass.timeline.DirectRenderingCallback;

public class RecognizerDrawer implements DirectRenderingCallback{

	private static final String TAG = RecognizerDrawer.class.getSimpleName();
	
	private final RecognizerView mRecognizerView;
	
	private SurfaceHolder mHolder;
	private boolean mRenderingPaused;
	
	public RecognizerDrawer(Context context) {
		this(new RecognizerView(context), "Nothing");
		Log.d(TAG, "No text." );
	}
	
	public RecognizerDrawer(Context context, String text) {
		this(new RecognizerView(context), text);
		Log.d(TAG, "Setting text1: " + text );
	}
	
	public RecognizerDrawer(RecognizerView recognizerView, String text) {
		mRecognizerView = recognizerView;
		Log.d(TAG, "Setting text2: " + text );
		mRecognizerView.setText(text);
		draw(mRecognizerView);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mRenderingPaused = false;
		mHolder = holder;
		updateRenderingState();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mRecognizerView.measure(measuredWidth, measuredHeight);
        mRecognizerView.layout(
                0, 0, mRecognizerView.getMeasuredWidth(), mRecognizerView.getMeasuredHeight());

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHolder = null;
        updateRenderingState();
	}

	@Override
	public void renderingPaused(SurfaceHolder arg0, boolean paused) {
		mRenderingPaused = paused;
		updateRenderingState();
	}
	
    private void updateRenderingState() {

    }
	
	private void draw(View view) {
		Canvas canvas;
		try {
			canvas = mHolder.lockCanvas();
		} catch (Exception e) {
			Log.e(TAG, "Unable to lock canvas: "+ e);
			return;
		}
		if (canvas != null) {
			view.draw(canvas);
			mHolder.unlockCanvasAndPost(canvas);
		}
	}

}
