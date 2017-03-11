package com.wq.demo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by WQ on 2016/8/25.
 */
public class BookOpenHelper extends SQLiteOpenHelper {

	private static final String CREATE_BOOK = "create table Book ("
			+ "id integer primary key autoincrement, "
			+ "book_path text"
			+ ")";

	private static final String CREATE_PROGRESS = "create table Progress ("
			+ "id integer primary key autoincrement, "
			+ "book_id integer, "
			+ "currentIndex text"
			+ ")";

	public BookOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BOOK);
		db.execSQL(CREATE_PROGRESS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
