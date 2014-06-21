package com.example.recognizer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	private static final String LIVE_CARD_TAG = "novelidea";
	
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
			Log.d(null,"VoiceEntry: " + voiceResults.get(0));
		}
		
		if (mLiveCard == null) {
			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
			
			// inflate low frequency update view
			mLiveCardView = new RemoteViews(getPackageName(), R.layout.book_summary);
			
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
			Log.d(LIVE_CARD_TAG, "Book: " + b.getTitle()  
					+ " by " +   b.getAuthors().get(0) 
					+ " with image: " + b.getImageURL());
			
			mLiveCardView.setTextViewText(R.id.book_author, b.getAuthors().get(0));
			mLiveCardView.setTextViewText(R.id.book_title, b.getTitle());
			new ImageDownloader().execute(b.getImageURL());
			mLiveCardView.setTextViewText(R.id.book_rating, "Rating " + Double.toString(b.getRating()));
			mLiveCard.setViews(mLiveCardView);
		}
	}
	
	public class ImageDownloader extends AsyncTask<String,Void,Bitmap> {
		@Override
		protected Bitmap doInBackground(String... arg0) {
			try {
				Log.d("IMAGEDOWNLOADER", arg0[0]);
		        URL url = new URL(arg0[0]);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        Bitmap myBitmap = BitmapFactory.decodeStream(input);
		        return myBitmap;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
		}
		
		@Override
		protected void onPostExecute(Bitmap b) {
			mLiveCardView.setImageViewBitmap(R.id.book_image, b);
			mLiveCard.setViews(mLiveCardView);
		}
	}

}
