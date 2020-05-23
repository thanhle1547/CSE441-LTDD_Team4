package com.example.devicesilencingapp.location.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.devicesilencingapp.libs.SharedPrefs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GPSTrackerService extends Service {
	private static final String PACKAGE_NAME = "com.example.devicesilencingapp.services";
	private final String TAG = GPSTrackerService.class.getSimpleName();

	public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
	public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
	public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

	private final IBinder mBinder = new LocalBinder();

	/**
	 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
	 */
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 1; // 1 minute ~ OLD: 10000 milisec ~ 10 sec

	/**
	 * The fastest rate for active location updates. Updates will never be more frequent than this value.
	 */
	private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

	/**
	 * Provides access to the Fused Location Provider API.
	 */
	private FusedLocationProviderClient mFusedLocationProvider;

	/**
	 * Stores parameters for requests to the FusedLocationProviderApi.
	 * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
	 */
	private LocationRequest mLocationRequest;

	/**
	 * Callback for Location events (when location changed).
	 * Empty Class -> Need to Override method
	 */
	private LocationCallback mLocationCallback;

	private Handler mServiceHandler;

	/**
	 * Represents a geographical location.
	 * The current location.
	 */
	private Location mCurrentLocation;

	public GPSTrackerService() {
	}

	@Override
	public void onCreate() {
		mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				super.onLocationResult(locationResult);
				onNewLocation(locationResult.getLastLocation());
			}
		};

		createLocationRequest();
		getLastLocation();

		HandlerThread handlerThread = new HandlerThread(TAG);
		handlerThread.start();

		mServiceHandler = new Handler(handlerThread.getLooper());
	}

	/**
	 * <p>
	 * Tells the system to not try to recreate the service after it has been killed.
	 *
	 * Nếu hệ thống kill service khi giá trị này được trả về thì service này không được khởi động lại
	 * trừ khi có một Intent đang được chờ ở onStartCommand().
	 * Đây là lựa chọn an toàn nhất để tránh chạy Service khi không cần thiết
	 * và khi ứng dụng có thể khởi động lại một cách đơn giản các công việc chưa hoàn thành.
	 * </p>
	 *
	 * @see https://viblo.asia/p/tong-quan-service-trong-android-maGK7M8elj2
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// Ensures onRebind() is called when a client re-binds.
		return true;
	}

	@Override
	public void onDestroy() {
		mServiceHandler.removeCallbacksAndMessages(null);
	}

	/**
	 * Sets the location request parameters.
	 */
	private void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	public void getLastLocation() {
		try {
			mFusedLocationProvider.getLastLocation()
					.addOnCompleteListener(new OnCompleteListener<Location>() {
						@Override
						public void onComplete(@NonNull Task<Location> task) {
							if (task.isSuccessful() && task.getResult() != null)
								mCurrentLocation = task.getResult();
							else
								Log.w(TAG, "Failed to get location.");
						}
					});
		} catch (SecurityException unlikely) {
			Log.e(TAG, "Lost location permission." + unlikely);
		}
	}

	private void onNewLocation(Location location) {
		mCurrentLocation = location;

		// Notify anyone listening for broadcasts about the new location.
		Intent intent = new Intent(ACTION_BROADCAST);
		intent.putExtra(EXTRA_LOCATION, location);

		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}

	public void requestLocationUpdates(){
		SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, true);
		startService(new Intent(getApplicationContext(), GPSTrackerService.class));
		try {
			mFusedLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
		} catch (SecurityException unlikely) {
			SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, false);
			Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
		}
	}

	public void removeLocationUpdates(){
		try {
			mFusedLocationProvider.removeLocationUpdates(mLocationCallback);
			SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, false);
			stopSelf();
		} catch (SecurityException unlikely) {
			SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, true);
			Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
		}
	}

	/**
	 * Class used for the client Binder.  Since this service runs in the same process as its
	 * clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public GPSTrackerService getService() {
			return GPSTrackerService.this;
		}
	}
}
