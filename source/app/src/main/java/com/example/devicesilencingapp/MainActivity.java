package com.example.devicesilencingapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.example.devicesilencingapp.libs.Fab;
import com.example.devicesilencingapp.location.fragments.LocationDetailFragment;
import com.example.devicesilencingapp.services.GeofencesService;
import com.example.devicesilencingapp.time.fragments.AddTimeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private Toolbar toolbar;
	private MaterialSheetFab materialSheetFab;
	private int statusBarColor;

	// setup Services Connection
	private final ServiceConnection mGeofencesConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			GeofencesService.LocalBinder binder = (GeofencesService.LocalBinder) service;
			mGeofencesService = binder.getService();
			mGeofencesBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mGeofencesService = null;
			mGeofencesBound = false;
		}
	};;

	private GeofencesService mGeofencesService = null;

	// Tracks the bound state of the service.
	private boolean mGeofencesBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupToolbar();
		setupFab();

		BottomNavigationView bottom_nav = findViewById(R.id.bottom_nav);
		// Passing each  `menu ID`  as a set of Ids because each
		// menu should be considered as top level destinations.
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_location, R.id.nav_schedule, R.id.nav_settings)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.fragment_main);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(bottom_nav, navController);

		/*bottomNav.setOnNavigationItemSelectedListener(navListener);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, LocationListFragment.newInstance()).commit();*/
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, GeofencesService.class), mGeofencesConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		if (mGeofencesBound) {
			// Unbind from the service. This signals to the service that this activity is no longer in the foreground
			unbindService(mGeofencesConnection);
			mGeofencesBound = false;
		}
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fab_sheet_item_add_current_location:
				getSupportFragmentManager().beginTransaction()
						.replace(
								R.id.fragment_detail,
								LocationDetailFragment.newInstance(LocationDetailFragment.ACTION_ADD))
						.addToBackStack(null)
						.commit();
				materialSheetFab.hideSheet();
				break;
			case R.id.fab_sheet_item_add_new_location:
				materialSheetFab.hideSheet();
				break;
			case R.id.fab_sheet_item_add_new_time:
				getSupportFragmentManager().beginTransaction()
						.replace(
								R.id.fragment_detail,
								AddTimeFragment.newInstance())
						.addToBackStack(null)
						.commit();
				materialSheetFab.hideSheet();
				break;
		}
	}

	/*private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
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
					selectFragment = TimeFragment.newInstance();
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
	};*/

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
