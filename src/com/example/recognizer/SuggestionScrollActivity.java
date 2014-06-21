package com.example.recognizer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import bookrec.Book;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

public class SuggestionScrollActivity extends Activity {

	private List<Card> mCards;
    private CardScrollView mCardScrollView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ArrayList<bookrec.Book> suggestedBooks = (ArrayList<Book>) getIntent().getExtras().get("suggestedBooks");
        createCards(suggestedBooks);

        mCardScrollView = new CardScrollView(this);
        ExampleCardScrollAdapter adapter = new ExampleCardScrollAdapter();
        mCardScrollView.setAdapter(adapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }
    
    private void createCards(ArrayList<bookrec.Book> suggestedBooks) {
        mCards = new ArrayList<Card>();

        Card card;
        
        for (int i = 0; i < 5; i++){
        	card = new Card(this);
        	card.setText(suggestedBooks.get(i).getTitle() + "\n" + suggestedBooks.get(i).getAuthor());
        	card.setImageLayout(Card.ImageLayout.LEFT);
        	card.addImage(getBitmapFromURL(suggestedBooks.get(i).getImageURL()));
        	card.setFootnote(suggestedBooks.get(i).getLink());
        	mCards.add(card);	
        }
        
//        card = new Card(this);
//        card.setText("This is the first book.");
//        card.setFootnote("By: Jesun David");
//        mCards.add(card);
//
//        card = new Card(this);
//        card.setText("This is the second book.");
//        card.setFootnote("By: Kapil Gowru");
//        mCards.add(card);
//
//        card = new Card(this);
//        card.setText("This is the third book.");
//        card.setFootnote("By: Hammad Bashir");
//        mCards.add(card);
    }
    
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
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
    
    
    private class ExampleCardScrollAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return Card.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView,
                ViewGroup parent) {
            return  mCards.get(position).getView(convertView, parent);
        }
    }
}
