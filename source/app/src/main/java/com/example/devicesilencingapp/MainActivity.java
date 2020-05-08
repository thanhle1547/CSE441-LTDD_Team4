package com.example.devicesilencingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
		bottomNav.setOnNavigationItemSelectedListener(navListener);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, new LocationFragment()).commit();
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
