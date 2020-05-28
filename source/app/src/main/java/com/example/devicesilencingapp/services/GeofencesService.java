package com.example.devicesilencingapp.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.models.UserLocationModel;
import com.example.devicesilencingapp.receiver.GeofenceBroadcastReceiver;
import com.example.devicesilencingapp.MainActivity;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.libs.GeofenceUtils;
import com.example.devicesilencingapp.libs.SharedPrefs;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class GeofencesService extends Service implements OnCompleteListener<Void> {
	private static final String PACKAGE_NAME = "com.example.devicesilencingapp.services";
	private final String TAG = GeofencesService.class.getSimpleName();

	/**
	 * The name of the channel for notifications.
	 * Android O requires a Notification Channel.
	 */
	private static final String CHANNEL_ID = "channel_geofences";

	/**
	 * The identifier for the notification displayed for the foreground service.
	 */
	private static final int NOTIFICATION_ID = 1;

	public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
	public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
	private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";
	private static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

	/**
	 * Tracks whether the user requested to add or remove geofences, or to do neither.
	 */
	private enum PendingGeofenceTask {
		ADD, REMOVE, NONE
	}

	private final IBinder mBinder = new LocalBinder();


	private NotificationManager mNotificationManager;

	/**
	 * Used to check whether the bound activity has really gone away and not unbound as part of an
	 * orientation change. We create a foreground service notification only if the former takes place.
	 */
	private boolean mChangingConfiguration = false;

	/**
	 * Provides access to the Geofencing API.
	 */
	private GeofencingClient mGeofencingClient;

	/**
	 * Used when requesting to add or remove geofences.
	 */
	private PendingIntent mGeofencePendingIntent;

	private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

	/**
	 * The list of geofences.
	 */
	private ArrayList<Geofence> mGeofenceList;

	public GeofencesService() {
	}

	@Override
	public void onCreate() {
		mGeofenceList = new ArrayList<>();

		// Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
		mGeofencePendingIntent = null;

		// get active user_location from DB
		populateGeofenceList(DBHelper.getInstance().getActiveLocations());

		mGeofencingClient = LocationServices.getGeofencingClient(this);

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.app_name);
			// Create the channel for the notification
			NotificationChannel mChannel =
					new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

			// Set the Notification Channel for the Notification Manager.
			mNotificationManager.createNotificationChannel(mChannel);
		}

		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);

		// If the user decided to remove location updates from the notification.
		if (startedFromNotification) {
			removeLocationUpdates();
			stopSelf();
		}

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
		// Called when a client (MainActivity in case of this sample) comes to the foreground
		// and binds with this service. The service should cease to be a foreground service
		// when that happens.
		stopForeground(true);
		mChangingConfiguration = false;
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		// Called when a client (MainActivity in case of this sample) returns to the foreground
		// and binds   `once again`   with this service. The service should cease to be a foreground
		// service when that happens
		stopForeground(true);
		mChangingConfiguration = false;
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// Called when the last client (MainActivity in case of this sample) unbinds from this
		// service. If this method is called due to a configuration change in MainActivity, we
		// do nothing. Otherwise, we make this service a foreground service.
		if (!mChangingConfiguration && requestingLocationUpdates())
			startForeground(NOTIFICATION_ID, getNotification());

		// Ensures onRebind() is called when a client re-binds.
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mChangingConfiguration = true;
	}

	/**
	 * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()} is available.
	 * @param task the resulting Task, containing either a result or error.
	 */
	@Override
	public void onComplete(@NonNull Task<Void> task) {
		mPendingGeofenceTask = PendingGeofenceTask.NONE;
		if (task.isSuccessful()) {
			boolean isGeofencesAdded = getGeofencesAdded();
			updateGeofencesAdded(!isGeofencesAdded);

		} else
			Log.w(TAG, GeofenceUtils.getErrorString(this, task.getException()));
	}

	@Override
	public void onDestroy() {

	}

	public void requestLocationUpdates(){
		SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, true);
		startService(new Intent(getApplicationContext(), GeofencesService.class));
		try {

		} catch (SecurityException unlikely) {
			SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, false);
			Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
		}
	}

	public void removeLocationUpdates(){
		try {
			SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, false);
			stopSelf();
		} catch (SecurityException unlikely) {
			SharedPrefs.getInstance().put(KEY_REQUESTING_LOCATION_UPDATES, true);
			Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
		}
	}

	/**
	 * Returns true if requesting location updates, otherwise returns false.
	 */
	private boolean requestingLocationUpdates() {
		return SharedPrefs.getInstance().get(KEY_REQUESTING_LOCATION_UPDATES, Boolean.class);
	}

	/**
	 * Returns the {@link NotificationCompat} used as part of the foreground service.
	 */
	private Notification getNotification() {
		Intent intent = new Intent(this, GeofencesService.class);

		CharSequence text = this.getString(R.string.monitoring_your_location);

		// Extra to help us figure out if we arrived in onStartCommand via the notification or not.
		intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

		// The PendingIntent that leads to a call to onStartCommand() in this service.
		PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// The PendingIntent to launch activity.
		PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.addAction(R.drawable.ic_launch, getString(R.string.launch_app),
						activityPendingIntent)
				.addAction(R.drawable.ic_cancel, getString(R.string.stop),
						servicePendingIntent)
				.setContentText(text)
				.setContentTitle(this.getString(R.string.app_is_on, this.getString(R.string.app_name)))
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_HIGH)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setTicker(text)
				.setWhen(System.currentTimeMillis());

		// Set the Channel ID for Android O.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(CHANNEL_ID); // Channel ID
		}

		return builder.build();
	}

	/**
	 * Returns true if this is a foreground service.
	 *
	 * @param context The {@link Context}.
	 */
	public boolean serviceIsRunningInForeground(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(
				Context.ACTIVITY_SERVICE);
		String thisClassName = getClass().getName();

		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (thisClassName.equals(service.service.getClassName())) {
				if (service.foreground) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if geofences were added, otherwise false.
	 */
	private boolean getGeofencesAdded() {
		return SharedPrefs.getInstance().get(GEOFENCES_ADDED_KEY, Boolean.class);
	}

	/**
	 * Stores whether geofences were added ore removed in {@link SharedPreferences};
	 *
	 * @param added Whether geofences were added or removed.
	 */
	private void updateGeofencesAdded (boolean added) {
		SharedPrefs.getInstance().put(GEOFENCES_ADDED_KEY, added);
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
		mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(this);
	}

	private void removeGeofences() {
		mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
	}

	/**
	 * Class used for the client Binder.  Since this service runs in the same process as its
	 * clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		GeofencesService getService() {
			return GeofencesService.this;
		}
	}
}
