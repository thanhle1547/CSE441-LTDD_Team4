package com.example.devicesilencingapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    private HashMap hp;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        // TODO Auto-generated method stub
        db.execSQL(
                "create table table1 " +
                        "(id integer primary key, name text)"
        );
        db.execSQL(
                "create table table2 " +
                        "(id integer primary key, name text)"
        );
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS table1");
        db.execSQL("DROP TABLE IF EXISTS table2");
        onCreate(db);
    }
    //Insert
    public boolean insertTB (Integer id, String name, String TableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("id", id);
        db.insert(TableName, null, contentValues);
        return true;
    }

    // update
    public boolean updateContact (Integer id, String name, String TableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        db.update(TableName, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    public Integer deleteContact (Integer id, String TableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TableName,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    //get data
    public Cursor getData(String TableName,Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data =  db.rawQuery( "select * from "+TableName+" where id="+id+"", null );
        return data;
    }
    public ArrayList<String> getAllCotacts(String TableName) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TableName+"", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
