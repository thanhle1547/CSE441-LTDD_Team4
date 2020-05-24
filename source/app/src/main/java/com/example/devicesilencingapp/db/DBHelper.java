package com.example.devicesilencingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.devicesilencingapp.time.timeModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "table_contacts";
	private static final int DB_VERSION = 1;
	private static final String TABLE_ = "";
	private static final String TABLE_Time = "table_time";

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
		db.execSQL("create table "+TABLE_Time+" (id integer primary key AUTOINCREMENT,gio TEXT,phut TEXT,nhaclai TEXT,battat BOOLEAN)");

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

	//Insert
	public long insertTBTime (timeModel model) {
		ContentValues values = populateContent(model);
		return getWritableDatabase().insert("table_time", null, values);
	}

	// update
	public long updateTime(timeModel model) {
		ContentValues values = populateContent(model);
		return getWritableDatabase().update("table_time", values, "table_time.id+  = ?", new String[] {String.valueOf(model.id)});
	}
	//delete
	public Integer deleteTBTime (Integer id) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_Time,
				"id = ? ",
				new String[] { Integer.toString(id) });
	}
	public timeModel getTime(long id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM table_time WHERE table_time.id  = " + id;

		Cursor c = db.rawQuery(select, null);
		if (c.moveToNext()) {
			return populateModel(c);
		}

		return null;
	}
	public ArrayList<timeModel> getAlarms() {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM table_time";

		Cursor c = db.rawQuery(select, null);

		ArrayList<timeModel> alarmList = new ArrayList<timeModel>();

		while (c.moveToNext()) {
			alarmList.add(populateModel(c));
		}

		if (!alarmList.isEmpty()) {
			return alarmList;
		}

		return null;
	}
	private timeModel populateModel(Cursor c) {
		timeModel model = new timeModel();
		model.id = c.getLong(c.getColumnIndex("id"));
		model.timeHour = c.getInt(c.getColumnIndex("gio"));
		model.timeMinute = c.getInt(c.getColumnIndex("phut"));
		model.isEnabled = c.getInt(c.getColumnIndex("battat")) != 0;

		String[] repeatingDays = c.getString(c.getColumnIndex("nhaclai")).split(",");
		for (int i = 0; i < repeatingDays.length; ++i) {
			model.setRepeatingDay(i, !repeatingDays[i].equals("false"));
		}

		return model;
	}

	private ContentValues populateContent(timeModel model) {
		ContentValues values = new ContentValues();
		values.put("gio", model.timeHour);
		values.put("phut", model.timeMinute);
		values.put("battat", model.isEnabled);

		StringBuilder repeatingDays = new StringBuilder();
		for (int i = 0; i<7; ++i) {
			repeatingDays.append(model.getRepeatingDay(i)).append(",");
		}
		values.put("nhaclai", repeatingDays.toString());

		return values;
	}

}
