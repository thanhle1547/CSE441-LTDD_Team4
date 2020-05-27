package com.example.devicesilencingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.devicesilencingapp.App;
import com.example.devicesilencingapp.models.UserLocationModel;
import com.example.devicesilencingapp.time.timeModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "table_contacts";
	private static final int    DB_VERSION = 1;
	private static final String TABLE_USER_LOCATION = "user_location";
	private static final String TABLE_Time = "table_time";

	private static final String LOCATION_ID = "id";
	private static final String LOCATION_NAME = "name";
	private static final String LOCATION_ADDRESS = "address";
	private static final String LOCATION_LABEL = "label";
	private static final String LOCATION_LONGITUDE = "longitude";
	private static final String LOCATION_LATITUDE = "latitude";
	private static final String LOCATION_RADIUS = "radius";
	private static final String LOCATION_EXPIRATION = "expiration";
	private static final String LOCATION_STATUS = "status";

	private static DBHelper instance;
	private SQLiteDatabase db;

	public DBHelper(@Nullable Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static synchronized DBHelper getInstance() {
		if (instance == null)
			instance = new DBHelper(App.self().getApplicationContext());
		return instance;
	}

	/**
	 * Called when the database is created for the first time.
	 * @see <a href="https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper">
	 *          SQLiteOpenHelper
	 *      </a>
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+TABLE_Time+" (id integer primary key,gio TEXT,phut TEXT,nhaclai TEXT,battat BOOLEAN)");

		db.execSQL(String.format(
				"CREATE TABLE if not exists %s " +
						"(%s INTEGER Primary key, %s TEXT, %s TEXT, %s INTEGER, " +
						" %s REAL, %s REAL, %s INTEGER, %s INTEGER, %s INTEGER) ",
				TABLE_USER_LOCATION,
				LOCATION_ID,
				LOCATION_NAME,
				LOCATION_ADDRESS,
				LOCATION_LABEL,
				LOCATION_LONGITUDE,
				LOCATION_LATITUDE,
				LOCATION_RADIUS,
				LOCATION_EXPIRATION,
				LOCATION_STATUS
		));
	}

	/**
	 * Called when the database needs to be upgraded.
	 * @see https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(String.format("DROP TABLE if exists %s", TABLE_USER_LOCATION));

		onCreate(db);
	}

	/**
	 * Called by the garbage collector on an object when garbage collection determines
	 * that there are no more references to the object.
	 *
	 * {@inheritDoc}
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		if (instance != null)
			instance.close();

		super.finalize();
	}


	/* --------  Schedule  -------- */
	//Insert
	public long insertTBTime (timeModel model) {
		ContentValues values = populateContent(model);
		return getWritableDatabase().insert("table_time", null, values);
	}

	// update
	public void updateTime(timeModel model) {
		ContentValues values = populateContent(model);
		getWritableDatabase().update("table_time", values, "table_time.id+  = ?", new String[] {String.valueOf(model.id)});
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


	/* --------  Location  -------- */
	public void editLocationStatus(UserLocationModel model) {
		db = this.getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put(LOCATION_ID, model.getId());
		value.put(LOCATION_STATUS, model.getStatus());

		db.update(TABLE_USER_LOCATION, LOCATION_ID + " = ?", new String[] { String.valueOf(id) })
	}

	public void removeLocation(long id) {
		db = this.getWritableDatabase();
		db.delete(TABLE_USER_LOCATION, LOCATION_ID + " = ?", new String[] { String.valueOf(id) });
	}

	public ArrayList<UserLocationModel> getAllLocations() {
		ArrayList<UserLocationModel> result = new ArrayList<>();
		db = this.getReadableDatabase();
		String QUERY = "SELECT * FROM " + TABLE_USER_LOCATION;
		Cursor cursor = db.rawQuery(QUERY, null);

		if (cursor.moveToFirst())
			do {
				result.add(populateUserLocationModel(cursor));
			} while (cursor.moveToNext());
		cursor.close();
		return result;
	}

	public ArrayList<UserLocationModel> getActiveLocations() {
		ArrayList<UserLocationModel> result = new ArrayList<>();
		db = this.getReadableDatabase();
		String QUERY = String.format("SELECT * FROM %s WHERE %s >= ?", TABLE_USER_LOCATION, LOCATION_ID);
		Cursor cursor = db.rawQuery(QUERY, new String[] { String.valueOf(1) });

		if (cursor.moveToFirst())
			do {
				result.add(populateUserLocationModel(cursor));
			} while (cursor.moveToNext());
		cursor.close();
		return result;
	}

	private UserLocationModel populateUserLocationModel(Cursor cursor) {
		return new UserLocationModel(
				cursor.getLong(0),
				cursor.getString(1),
				cursor.getString(2),
				cursor.getInt(3),
				cursor.getDouble(4),
				cursor.getDouble(5),
				cursor.getInt(6),
				cursor.getInt(7),
				cursor.getInt(8) != 0
		);
	}
}
