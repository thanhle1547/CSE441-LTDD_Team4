package com.example.devicesilencingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.devicesilencingapp.App;
import com.example.devicesilencingapp.location.model.UserLocationModel;
import com.example.devicesilencingapp.time.timeModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "devicesilencingapp";
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
	public void editTimeStatus(timeModel time_modal){
		ContentValues values = new ContentValues();
		values.put("battat", time_modal.isEnabled);
		this.getWritableDatabase().update(
				TABLE_Time, values, "id=?", new String[]{
						String.valueOf(time_modal.getId())
				}
		);
	}
	//Insert
	public long insertTBTime (timeModel model) {
		ContentValues values = populateContent(model);
        return getWritableDatabase().insert("table_time", null, values);
    }

	// update
	public void updateTime(timeModel model) {
		ContentValues values = populateContent(model);
		getWritableDatabase().update("table_time", values, "id = ?", new String[]{String.valueOf(model.getId())});
	}
	//delete
	public void deleteTBTime (long id) {
		getWritableDatabase().delete(TABLE_Time,
				"id = ? ",
				new String[] { String.valueOf(id) }
		);
	}
	public timeModel getTime(int id) {
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

		return alarmList;
	}
	private timeModel populateModel(Cursor c) {
		timeModel model = new timeModel();
		model.id = c.getLong(c.getColumnIndex("id"));
		model.timeHour = c.getInt(c.getColumnIndex("gio"));
		model.timeMinute = c.getInt(c.getColumnIndex("phut"));
		model.isEnabled = c.getInt(c.getColumnIndex("battat")) == 0 ? false : true;

		String[] repeatingDays = c.getString(c.getColumnIndex("nhaclai")).split(",");
		for (int i = 0; i < repeatingDays.length; ++i) {
			model.setRepeatingDay(i, repeatingDays[i].equals("false") ? false : true);
		}

		return model;
	}

	private ContentValues populateContent(timeModel model) {
		ContentValues values = new ContentValues();
		values.put("gio", model.timeHour);
		values.put("phut", model.timeMinute);
		values.put("battat", model.isEnabled);

		String repeatingDays = "";
		for (int i = 0; i<7; ++i) {
			repeatingDays += model.getRepeatingDay(i) + ",";
		}
		values.put("nhaclai", repeatingDays);

		return values;
	}


	/* --------  Location  -------- */
	public long addLocation(UserLocationModel model) {
		return this.getWritableDatabase()
				.insert(TABLE_USER_LOCATION, null, populateUserLocationContent(model));
	}

	public void editLocation(UserLocationModel model) {
		this.getWritableDatabase()
				.update(
						TABLE_USER_LOCATION,
						populateUserLocationContent(model),
						LOCATION_ID + " = ?",
						new String[] { String.valueOf(model.getId()) }
				);
	}

	public void editLocationStatus(UserLocationModel model) {
		ContentValues value = new ContentValues();
		value.put(LOCATION_STATUS, model.getStatus());

		this.getWritableDatabase().update(
			TABLE_USER_LOCATION,
			value,
			LOCATION_ID + " = ?",
			new String[] { String.valueOf( model.getId()) }
		);
	}

	public void removeLocation(long id) {
		this.getWritableDatabase()
				.delete(
						TABLE_USER_LOCATION,
						LOCATION_ID + " = ?",
						new String[] { String.valueOf(id) }
				);
	}

	public ArrayList<UserLocationModel> getAllLocations() {
		ArrayList<UserLocationModel> result = new ArrayList<>();
		String QUERY = "SELECT * FROM " + TABLE_USER_LOCATION;
		Cursor cursor = this.getReadableDatabase().rawQuery(QUERY, null);

		if (cursor.moveToFirst())
			do {
				result.add(populateUserLocationModel(cursor));
			} while (cursor.moveToNext());
		cursor.close();
		return result;
	}

	public ArrayList<UserLocationModel> getActiveLocations() {
		ArrayList<UserLocationModel> result = new ArrayList<>();
		String QUERY = String.format("SELECT * FROM %s WHERE %s >= ?", TABLE_USER_LOCATION, LOCATION_ID);
		Cursor cursor = this.getReadableDatabase().rawQuery(QUERY, new String[] { String.valueOf(1) });

		if (cursor.moveToFirst())
			do {
				result.add(populateUserLocationModel(cursor));
			} while (cursor.moveToNext());
		cursor.close();
		return result;
	}

	private ContentValues populateUserLocationContent(UserLocationModel model) {
		ContentValues values = new ContentValues();
		values.put(LOCATION_NAME, model.getName());
		values.put(LOCATION_ADDRESS, model.getAddress());
		values.put(LOCATION_LABEL, model.getLabel());
		values.put(LOCATION_LONGITUDE, model.getLongitude());
		values.put(LOCATION_LATITUDE, model.getLatitude());
		values.put(LOCATION_RADIUS, model.getRadius());
		values.put(LOCATION_EXPIRATION, model.getExpiration());
		values.put(LOCATION_STATUS, model.getStatus());

		return values;
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
