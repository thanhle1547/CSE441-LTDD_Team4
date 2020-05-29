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
			// Gửi Notification

		} else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

		}
	}

	/**
	 * Convenience method for enqueuing work in to this service.
	 */
	public static void enqueueWork(Context context, Intent intent) {
		enqueueWork(context, GeofencesManagingService.class, JOB_ID, intent);
	}

	/**
	 * Posts a notification in the notification bar when a transition is detected.
	 * If the user clicks the notification, control goes to the MainActivity.
	 */
	private void sendNotification(String notificationDetails) {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.app_name);
			// Create the channel for the notification
			NotificationChannel mChannel =
					new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

			// Set the Notification Channel for the Notification Manager.
			mNotificationManager.createNotificationChannel(mChannel);
		}

		/**
		 * Create an  `explicit content Intent`  that starts the main Activity.
		 *
		 * Explicit Intents:
		 *
		 * Intent đã được xác định thuộc tính component,
		 * nghĩa là đã chỉ rõ thành phần sẽ nhận và xử lý intent
		 * (xác định một cách rõ ràng các thành phần sẽ được gọi bởi hệ thống android).
		 * Thông thường intent dạng này sẽ không bổ sung thêm các thuộc tính khác như action, data.
		 * Explicit Intent thương được sử dụng để khởi chạy các activity trong cùng 1 ứng dụng.
		 *
		 * @see https://viblo.asia/p/huong-dan-lam-mot-app-nghe-nhac-online-va-offline-don-gian-7prv3PzjRKod
		*/
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);

		// The PendingIntent that leads to a call to onStartCommand() in this service.
		PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// The PendingIntent to launch activity.
		PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.addAction(R.drawable.ic_launch, getString(R.string.launch_app),
						activityPendingIntent)
				.addAction(R.drawable.ic_cancel, getString(R.string.stop_location_update),
						servicePendingIntent)
				.setContentTitle(
						String.format(
								this.getString(R.string.app_is_on),
								this.getString(R.string.app_name)))
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_HIGH)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setWhen(System.currentTimeMillis());

		// Set the Channel ID for Android O.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(CHANNEL_ID); // Channel ID
		}

		// Issue the notification
		mNotificationManager.notify(0, builder.build());
	}
}
