package com.example.devicesilencingapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.example.devicesilencingapp.MainActivity;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.libs.GeofenceUtils;
import com.example.devicesilencingapp.receiver.StartJobIntentServiceReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 *
 * The only difference with JobIntentService is that JobIntentService gets restarted
 * if the application gets killed while the service was executing.
 * OnHandleWork() get's restarted after the application get's killed
 *
 * @see <a href="https://viblo.asia/p/lam-mot-task-voi-geofencing-trong-android-thi-mat-bao-lau-3KbvZqELGmWB#_ii-tim-tai-lieu-va-nghien-cuu-tai-lieu-ve-no-2-tieng-cho-no-tha-ga-1">
 *     Làm một task với Geofencing trong Android thì mất bao lâu?</a>
 *     <br>
 * @see <a href="https://github.com/android/location-samples/blob/master/Geofencing/app/src/main/java/com/google/android/gms/location/sample/geofencing/GeofenceTransitionsJobIntentService.java">
 *     Github GeofenceTransitionsJobIntentService.java</a>
 */
public class GeofencesManagingService extends JobIntentService {
	private static final String TAG = GeofencesManagingService.class.getSimpleName();

	private static final int JOB_ID = 1;

	/**
	 * The name of the channel for notifications.
	 * Android O requires a Notification Channel.
	 */
	private static final String CHANNEL_ID = "channel_geofence";
	private static final int NOTIFICATION_ID = 1;

	private NotificationManager mNotificationManager;

	/**
	 * Handles incoming intents.
	 * @param intent    sent by Location Services. This Intent is provided to
	 *                  Location Services (inside a PendingIntent) when addGeofences() is called.
	 */
	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		// Check sự kiện Geofencing
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

		if (geofencingEvent.hasError()) {
			String errorMessage = GeofenceUtils.getErrorString(this, geofencingEvent.getErrorCode());
			return;
		}

		// lấy kiểu di chuyển (vào hay ra khỏi geofence)
		int geofenceTransition = geofencingEvent.getGeofenceTransition();

		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
			Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
			return;
		}

		// Lấy ra list các geofences đã kích hoạt Intent. Một sự kiện có thể kích hoạt nhiều geofences
		List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

		// Tổng hợp lại vào message
		String geofenceTransitionDetails =
				GeofenceUtils.getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

		// Gửi Notification về nếu kiểu di chuyển thuộc loại vào hoặc ra geofence
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
			performAction(true);
		} else {
			performAction(false);
		}
	}

	/**
	 * Convenience method for enqueuing work in to this service.
	 */
	public static void enqueueWork(Context context, Intent intent) {
		enqueueWork(context, GeofencesManagingService.class, JOB_ID, intent);
	}

	private void performAction(boolean status) {
		// Gửi Notification
		Intent intent = new Intent(this, NotificationService.class);
		intent.putExtra(NotificationService.NOTIFICATION_CONTENT, getString(status ? R.string.silent_mode_is_on : R.string.silent_mode_is_off));
		sendBroadcast(StartJobIntentServiceReceiver.getIntent(this, intent, JOB_ID));

		intent = new Intent(this, AudioManagerService.class);
		intent.putExtra(AudioManagerService.ARG_ACTION, status ? AudioManagerService.ACTION_START : AudioManagerService.ACTION_STOP);
		sendBroadcast(StartJobIntentServiceReceiver.getIntent(this, intent, JOB_ID));
	}
}
