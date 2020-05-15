package com.example.devicesilencingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "table_contacts";
	private static final int DB_VERSION = 1;
	private static final String TABLE_ = "";

	public DBHelper(@Nullable Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Called when the database is created for the first time.
	 * @see https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
	 *
	 * Android SQLite Primary Key Auto Increment
	 * @see http://www.androidph.com/2012/01/android-sqlite-primary-key-auto.html
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
//		String CREATE_TABLE = String.format(
//				"CREATE TABLE if not exists %s (%s INTERGER Primary key, %s TEXT, %s TEXT, %s BLOD, %s TEXT)",
//				TABLE_NAME, KEY_ID, KEY_NAME, KEY_PHONE_NUMBER, KEY_AVATAR, KEY_EMAIL);
//		db.execSQL(CREATE_TABLE);
	}

	/**
	 * Called when the database needs to be upgraded.
	 * @see https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		String DROP_TABLE = String.format("DROP TABLE if exists %s", TABLE_NAME);
//		db.execSQL(DROP_TABLE);
//
//		onCreate(db);
	}
}
