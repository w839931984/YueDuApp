package com.wq.demo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wq.demo.bean.Book;
import com.wq.demo.bean.Progress;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/8/25.
 */
public class BookDB {
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "book";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	private static BookDB bookDB;
	private SQLiteDatabase db;

	private BookDB(Context context) {
		BookOpenHelper openHelper = new BookOpenHelper(context, DB_NAME, null, VERSION);
		db = openHelper.getWritableDatabase();
	}

	public synchronized static BookDB getInstance(Context context) {
		if (bookDB == null) {
			bookDB = new BookDB(context);
		}
		return bookDB;
	}

	public void saveBook(Book book) {
		if (book != null) {
			Cursor cursor = db.query("Book", new String[]{"book_path"}, "book_path=?", new String[]{book.getPath()}, null, null, null);
			if (cursor.moveToNext()) {
				cursor.close();
				return;
			}
			cursor.close();
			ContentValues values = new ContentValues();
			values.put("book_path", book.getPath());
			db.insert("Book", null, values);
		}
	}

	public Book loadBook(String path) {
		Cursor cursor = db.query("Book", null, "book_path=?", new String[]{path}, null, null, null);
		if (cursor.moveToNext()) {
			Book book = new Book();
			book.setPath(cursor.getString(cursor.getColumnIndex("book_path")));
			book.setId(cursor.getInt(cursor.getColumnIndex("id")));
			cursor.close();
			return book;
		}
		cursor.close();
		return null;
	}

	public ArrayList<Book> loadAllBook() {
		ArrayList<Book> books = new ArrayList<>();
		Cursor cursor = db.query("Book", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Book book = new Book();
			book.setPath(cursor.getString(cursor.getColumnIndex("book_path")));
			book.setId(cursor.getInt(cursor.getColumnIndex("id")));
			books.add(book);
		}
		cursor.close();
		return books;
	}

	public void saveProgress(Progress progress) {
		if (progress != null) {
			Cursor cursor = db.query("Progress", new String[]{"currentIndex"}, "book_id=?", new String[]{progress.getBookId()+""}, null, null, null);
			ContentValues values = new ContentValues();
			if (cursor.moveToNext()) {
				values.put("currentIndex", progress.getCurrentIndex());
				db.update("Progress", values, "book_id=?", new String[]{progress.getBookId()+""});
				cursor.close();
				return;
			}
			cursor.close();
			values.put("currentIndex", progress.getCurrentIndex());
			values.put("book_id", progress.getBookId());
			db.insert("Progress", null, values);
		}
	}

	public long loadProgress(int book_id) {
		Cursor cursor = db.query("Progress", new String[]{"currentIndex"}, "book_id=?", new String[]{book_id+""}, null, null, null);
		if (cursor.moveToNext()) {
			long l = Long.parseLong(cursor.getString(0));
			cursor.close();
			if (l<0){
				return 0;
			}
			return l;
		}
		return 0;
	}
}
