package com.example.devicesilencingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DBHelper mydb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mydb = new DBHelper(this);
        mydb.insertTB(2,"name 1 a","table1");
        Cursor data = mydb.getData("table1",2);
        data.moveToFirst();
        String tost = data.getString(data.getColumnIndex("name"));
        Toast.makeText(this,tost , Toast.LENGTH_SHORT).show();
	}
}
