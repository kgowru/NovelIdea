package com.example.recognizer;

import java.util.ArrayList;

import bookrec.Book;
import bookrec.BookRecommendationServiceImpl;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuActivity extends Activity {
	
	public String isbn;
	public ArrayList<bookrec.Book> suggestedBooks; 
	
	public void setSuggestedBooks(ArrayList<bookrec.Book> suggestedBooks) {
		this.suggestedBooks = suggestedBooks;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public MenuActivity(){
		isbn = "";
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		openOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.recognizer, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stop:
			stopService(new Intent(MenuActivity.this, RecognizerService.class));
			return true;
		case R.id.more_info:
			// launch the SuggestionScrollActivity
			new RestSuggestionHelper().execute(isbn);
			
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		finish();
	}
	
	public class RestSuggestionHelper extends AsyncTask<String,Void,ArrayList<bookrec.Book>>{

		@Override
		protected ArrayList<bookrec.Book> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			ArrayList<bookrec.Book> b = new ArrayList<bookrec.Book>();
			BookRecommendationServiceImpl test = new BookRecommendationServiceImpl();
			b = test.getRelatedBooks(arg0[0]);
			return b;
		
		}
		
		protected void onPostExecute(ArrayList<bookrec.Book> b){
			Intent moreInfo = (new Intent(MenuActivity.this, SuggestionScrollActivity.class));
			moreInfo.putExtra("suggestedBooks", suggestedBooks);
			startActivityForResult(moreInfo, 0);
			
		}
		
	
	}

}
