package com.example.devicesilencingapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.devicesilencingapp.libs.Fab;
import com.example.devicesilencingapp.location.fragments.LocationDetailFragment;
import com.example.devicesilencingapp.location.fragments.LocationListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private Toolbar toolbar;
	private MaterialSheetFab materialSheetFab;
	private int statusBarColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupToolbar();
		setupFab();

		BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
		bottomNav.setOnNavigationItemSelectedListener(navListener);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, LocationListFragment.newInstance()).commit();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fab_sheet_item_add_current_location:
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, LocationDetailFragment.newInstance()).commit();
				break;
			case R.id.fab_sheet_item_add_new_location:
				break;
			case R.id.fab_sheet_item_add_new_time:
				break;
		}
	}

	private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment selectFragment = null;
			switch (item.getItemId()) {
				case R.id.nav_location:
					selectFragment = LocationListFragment.newInstance();
					toolbar.setTitle(R.string.your_location);
					toolbar.setPopupTheme(R.style.ColorPrimary);
					break;
				case R.id.nav_time:
					selectFragment = new TimeFragment();
					toolbar.setTitle(R.string.time);
					toolbar.setPopupTheme(R.style.ColorSecondary);
					break;
				case R.id.nav_settings:
					selectFragment = new SettingsFragment();
					toolbar.setTitle(R.string.settings);
					toolbar.setPopupTheme(R.style.ColorTertiary);
					break;
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, selectFragment).commit();
			return true;
		}
	};

	private void setupToolbar(){
		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.your_location);
		toolbar.setPopupTheme(R.style.ColorPrimary);
		setSupportActionBar(toolbar);
	}

	/**
	 * Sets up the Floating action button.
	 */
	private void setupFab(){
		Fab fab = (Fab) findViewById(R.id.fab);
		View sheetView = findViewById(R.id.fab_sheet);
		View overlay = findViewById(R.id.overlay);
		int sheetColor = getResources().getColor(R.color.white);
		int fabColor = getResources().getColor(R.color.grey_600);

		// Create material sheet FAB
		materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

		// Set material sheet event listener
		materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
			@Override
			public void onShowSheet() {
				// Save current status bar color
				statusBarColor = getStatusBarColor();
				// Set darker status bar color to match the dim overlay
				setStatusBarColor(getResources().getColor(R.color.theme_primary_dark2));
			}

			@Override
			public void onHideSheet() {
				// Restore status bar color
				setStatusBarColor(statusBarColor);
			}
		});

		// Set material sheet item click listeners
		findViewById(R.id.fab_sheet_item_add_current_location).setOnClickListener(this);
		findViewById(R.id.fab_sheet_item_add_new_location).setOnClickListener(this);
		findViewById(R.id.fab_sheet_item_add_new_time).setOnClickListener(this);
	}

	private int getStatusBarColor() {
		return getWindow().getStatusBarColor();
	}

	private void setStatusBarColor(int color) {
		getWindow().setStatusBarColor(color);
	}
}
