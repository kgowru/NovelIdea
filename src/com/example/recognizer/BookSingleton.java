package com.example.recognizer;

import java.util.ArrayList;

public class BookSingleton {
	private static BookSingleton instance = null;
	private static String isbn;
	
	private static ArrayList<bookrec.Book> suggestedBooks;
	
	public static ArrayList<bookrec.Book> getSuggestedBooks() {
		return suggestedBooks;
	}
	public static void setSuggestedBooks(ArrayList<bookrec.Book> suggestedBooks) {
		BookSingleton.suggestedBooks = suggestedBooks;
	}
	public static String getIsbn() {
		return isbn;
	}
	public static void setIsbn(String isbn) {
		BookSingleton.isbn = isbn;
	}
	protected BookSingleton(){
		
	}
	public static BookSingleton getInstance(){
		if(instance == null){
			instance = new BookSingleton();
			isbn = "";
			suggestedBooks = new ArrayList<bookrec.Book>();
		}
		return instance;
	}
	
}
