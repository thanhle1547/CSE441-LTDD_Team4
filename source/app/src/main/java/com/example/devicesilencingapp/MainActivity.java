package com.example.devicesilencingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends AppCompatActivity {

	ImageButton clickadd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		clickadd = (ImageButton)findViewById(R.id.btclickadd);
		clickadd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogAdd();
			}
		});
		BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
		bottomNav.setOnNavigationItemSelectedListener(navListener);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, new LocationFragment()).commit();
	}
	private void DialogAdd(){
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.clickadd_fragment);
		final LinearLayout addvtht = (LinearLayout)dialog.findViewById(R.id.addvtht);
		final LinearLayout adddd = (LinearLayout)dialog.findViewById(R.id.adddiadiem);
		final LinearLayout addtime = (LinearLayout)dialog.findViewById(R.id.addtime);
		addvtht.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(MainActivity.this, "tạo intent add vi tri hien tai", Toast.LENGTH_SHORT).show();
			}
		});
		adddd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(MainActivity.this, "tạo intent add dia diem moi", Toast.LENGTH_SHORT).show();
			}
		});
		addtime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(MainActivity.this, "tạo intent add time", Toast.LENGTH_SHORT).show();
			}
		});
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		wlp.x = 40;   //x position
		wlp.y = 90;   //y position
		dialog.show();
	}

	private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment selectFragment = null;
			switch (item.getItemId()) {
				case R.id.nav_location:
					selectFragment = new LocationFragment();
					break;
				case R.id.nav_time:
					selectFragment = new TimeFragment();
					break;
				case R.id.nav_settings:
					selectFragment = new SettingsFragment();
					break;
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, selectFragment).commit();
			return true;
		}
	};

}
