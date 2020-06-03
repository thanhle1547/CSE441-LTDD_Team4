package com.example.devicesilencingapp.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.models.UserLocationModel;
import com.example.devicesilencingapp.receiver.GeofenceBroadcastReceiver;
import com.example.devicesilencingapp.MainActivity;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.libs.GeofenceUtils;
import com.example.devicesilencingapp.libs.SharedPrefs;
import com.example.devicesilencingapp.settings.SettingsFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class GeofencesService extends Service implements OnCompleteListener<Void>, OnFailureListener {
	private static final String PACKAGE_NAME = "com.example.devicesilencingapp.services";
	private final String TAG = GeofencesService.class.getSimpleName();

	public static final String KEY_IS_LOCATION_LIST_UPDATE = "is_location_list_update";
	public static final String EXTRA_TASK = "extra_task";

	/**
	 * Tracks whether the user requested to add or remove geofences, or to do neither.
	 */
	public enum PendingGeofenceTask {
		ADD, REMOVE, NONE
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Provides access to the Geofencing API.
	 */
	private GeofencingClient mGeofencingClient;

	/**
	 * Used when requesting to add or remove geofences.
	 */
	private PendingIntent mGeofencePendingIntent;

	private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

	// The BroadcastReceiver used to listen from broadcasts from the service.
	private Receiver receiver;

	/**
	 * The list of geofences.
	 */
	private ArrayList<Geofence> mGeofenceList;

	private class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.hasExtra(EXTRA_TASK))
				return;
			mPendingGeofenceTask = (PendingGeofenceTask) intent.getSerializableExtra(EXTRA_TASK);
			performPendingGeofenceTask();
		}
	}

	public GeofencesService() {
	}

	@Override
	public void onCreate() {
		receiver = new Receiver();

		mGeofenceList = new ArrayList<>();

		// Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
		mGeofencePendingIntent = null;

		// get active user_location from DB
		populateGeofenceList(DBHelper.getInstance().getActiveLocations());

		mGeofencingClient = LocationServices.getGeofencingClient(this);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (sharedPrefs.getBoolean(SettingsFragment.KEY_APP_STATUS, true)
				&& sharedPrefs.getBoolean(SettingsFragment.KEY_LOCATION_STATUS, true)) {
			mPendingGeofenceTask = PendingGeofenceTask.ADD;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		performPendingGeofenceTask();

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
		return START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(SettingsFragment.ACTION_BROADCAST));
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (isLocationListUpdate()) {
			removeGeofences();
			addGeofences();
		}

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

		// Ensures onRebind() is called when a client re-binds.
		return true;
	}

	/**
	 * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()} is available.
	 * @param task the resulting Task, containing either a result or error.
	 */
	@Override
	public void onComplete(@NonNull Task<Void> task) {
		mPendingGeofenceTask = PendingGeofenceTask.NONE;
		if (!task.isSuccessful()) {
			Log.w(TAG, GeofenceUtils.getErrorString(this, task.getException()));
			Log.w(TAG, task.getException());
		}
	}

	@Override
	public void onFailure(@NonNull Exception e) {
		Log.e(TAG, e.getMessage());
	}

	@Override
	public void onDestroy() {

	}

	private boolean isLocationListUpdate() {
		return SharedPrefs.getInstance().get(KEY_IS_LOCATION_LIST_UPDATE, Boolean.class);
	}

	private void populateGeofenceList(ArrayList<UserLocationModel> modelList) {
		for (UserLocationModel model : modelList) {
			mGeofenceList.add(new Geofence.Builder()
					// Set the request ID of the geofence. This is a string to identify this geofence.
					.setRequestId(String.valueOf(model.getId()))
					.setCircularRegion(
							model.getLatitude(),
							model.getLongitude(),
							model.getRadius()
					)
					// Set the expiration duration of the geofence.
					// This geofence gets automatically removed after this period of time.
					.setExpirationDuration(model.getExpiration())
					// Set the transition types of interest. Alerts are only generated for these
					// transition. We track entry and exit transitions in this sample.
					.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
					.build()
			);
		}
	}

	/**
	 * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
	 * Also specifies how the geofence notifications are initially triggered.
	 */
	private GeofencingRequest getGeofencingRequest() {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

		// The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
		// GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
		// is already inside that geofence.
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

		// Add the geofences to be monitored by geofencing service.
		builder.addGeofences(mGeofenceList);

		// Return a GeofencingRequest.
		return builder.build();
	}

	/**
	 * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
	 * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
	 * current list of geofences.
	 *
	 * @return A PendingIntent for the IntentService that handles geofence transitions.
	 */
	private PendingIntent getGeofencePendingIntent() {
		// Reuse the PendingIntent if we already have it.
		if (mGeofencePendingIntent != null)
			return mGeofencePendingIntent;

		Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
		// addGeofences() and removeGeofences().
		mGeofencePendingIntent = PendingIntent.getBroadcast(
				this,
				0,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return mGeofencePendingIntent;
	}

	/**
	 * Performs the geofencing task that was pending until location permission was granted.
	 */
	private void performPendingGeofenceTask() {
		if (mPendingGeofenceTask == PendingGeofenceTask.ADD)
			addGeofences();
		else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE)
			removeGeofences();
	}

	private void addGeofences() {
		mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
				.addOnCompleteListener(this)
				.addOnFailureListener(this);
	}

	private void removeGeofences() {
		mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
	}

	/**
	 * Class used for the client Binder.  Since this service runs in the same process as its
	 * clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public GeofencesService getService() {
			return GeofencesService.this;
		}
	}
}
