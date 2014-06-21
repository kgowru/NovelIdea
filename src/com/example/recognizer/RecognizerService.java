package com.example.recognizer;

import java.io.IOException;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.RemoteViews;
import book.Book;
import book.BookInfoService;
import book.BookInfoServiceImpl;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class RecognizerService extends Service {

	private static final String LIVE_CARD_TAG = "recognizer";
	
	private LiveCard mLiveCard;
	
	// Direct Rendering
	//private RecognizerDrawer mCallback;
	
	// Low frequency updating
	private RemoteViews mLiveCardView;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ArrayList<String> voiceResults = intent.getExtras()
	            .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		
		if (voiceResults.size() > 0 ) {
			for (String r : voiceResults) {
				Log.d(LIVE_CARD_TAG, r);
			}
		}
		
		if (mLiveCard == null) {
			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
			
			// inflate low frequency update view
			mLiveCardView = new RemoteViews(getPackageName(), R.layout.card_recognized);
			
			// DISABLED Direct rendering for high frequency
			/*
			mCallback = new RecognizerDrawer(this, voiceResults.get(0));
			mLiveCard.setDirectRenderingEnabled(true).getSurfaceHolder().addCallback(mCallback);
			*/
			
			Intent menuIntent = new Intent(this, MenuActivity.class);
			menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			
			mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
			mLiveCard.attach(this);
			mLiveCard.publish(PublishMode.REVEAL);
		} else {
			mLiveCard.navigate();
		}
		
		new RestHelper().execute(voiceResults.get(0).replace(' ', '+'));
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		if (mLiveCard != null && mLiveCard.isPublished()) {
			mLiveCard.unpublish();
			mLiveCard = null;
		}
		super.onDestroy();
	}
	
	public class RestHelper extends AsyncTask<String,Void,Book>{

		@Override
		protected Book doInBackground(String... arg0) {
			Book c = new Book();
			BookInfoService s = new BookInfoServiceImpl();
			try {
				c = s.retrieveInfo(arg0[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return c;	
		}
		
		@Override
		protected void onPostExecute(Book b){
			mLiveCardView.setTextViewText(R.id.spoken_text, b.getAuthors().get(0));
			mLiveCard.setViews(mLiveCardView);
		}
		
		
	}

}
