package com.example.devicesilencingapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.example.devicesilencingapp.MainActivity;
import com.example.devicesilencingapp.R;

public class NotificationService extends JobIntentService {
	public static final String NOTIFICATION_CONTENT = "notification_content";
	public static final String SILENT_MODE_STATUS = "silent_mode_status";

	/**
	 * The name of the channel for notifications.
	 * Android O requires a Notification Channel.
	 */
	private static final String CHANNEL_ID = "channel_location";

	/**
	 * The identifier for the notification displayed for the foreground service.
	 */
	private static final int NOTIFICATION_ID = 1;

	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.app_name);
			// Create the channel for the notification
			NotificationChannel mChannel =
					new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

			// Set the Notification Channel for the Notification Manager.
			mNotificationManager.createNotificationChannel(mChannel);
		}

		// Issue the notification
		mNotificationManager.notify(NOTIFICATION_ID, getNotification(
				intent.getStringExtra(NOTIFICATION_CONTENT),
				intent.getBooleanExtra(SILENT_MODE_STATUS, true)
		));
	}

	/**
	 * Returns the {@link NotificationCompat} used as part of the foreground service.
	 */
	private Notification getNotification(CharSequence contentText, boolean silentModeStatus) {
		CharSequence text = this.getString(R.string.app_is_on, this.getString(R.string.app_name));
		// The PendingIntent to launch activity.
		PendingIntent activityPendingIntent = PendingIntent.getActivity(
				this,
				0,
				new Intent(this, MainActivity.class),
				0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.addAction(R.drawable.ic_launch, getString(R.string.launch_app),
						activityPendingIntent)
				.setContentText(contentText)
				.setContentTitle(text)
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_HIGH)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setTicker(text)
				.setWhen(System.currentTimeMillis());

		// Dừng chế độ yên lặng nếu đang được bật
		if (silentModeStatus) {
			Intent intent = new Intent(this, AudioManagerService.class);
			intent.putExtra(AudioManagerService.ARG_ACTION, AudioManagerService.ACTION_STOP);
			PendingIntent servicePendingIntent = PendingIntent.getBroadcast(
					this,
					0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.addAction(R.drawable.ic_cancel, getString(R.string.stop_silent_mode), servicePendingIntent);
		}

		// Set the Channel ID for Android O.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(CHANNEL_ID); // Channel ID
		}

		return builder.build();
	}
}
