package com.example.recognizer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RecognizerView extends FrameLayout {

	private final TextView mRecognizedView;
	
	public RecognizerView(Context context) {
		this(context, null, 0);
	}
	
	public RecognizerView(Context context, AttributeSet attrs) {
		this(context,attrs, 0);
	}
	
	public RecognizerView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		LayoutInflater.from(context).inflate(R.layout.card_recognized, this);
		mRecognizedView = (TextView)findViewById(R.id.spoken_text);
	}
	
	public void setText(String text) {
		mRecognizedView.setText(text);
	}
}
