package com.example.devicesilencingapp.libs;

import android.content.Context;
import android.content.Intent;

import com.example.devicesilencingapp.App;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.receiver.StartJobIntentServiceReceiver;
import com.example.devicesilencingapp.services.AudioManagerService;
import com.example.devicesilencingapp.services.NotificationService;

public class SilentModeManagerUtil {
	/**
	 * JobIntentService.enqueueWork ở StartJobIntentServiceReceiver (JobIntentService) y/c 1 jobId
	 * jobId – A unique job ID for scheduling;
	 * `must be the same value for all work enqueued for the same class`
	 */
	private static final int JOB_ID = 15;

	public static void performAction(Context context, boolean status) {
		// Gửi Notification
		Intent intent = new Intent(context, NotificationService.class);
		intent.putExtra(
				NotificationService.NOTIFICATION_CONTENT,
				App.self().getString(status ? R.string.silent_mode_is_on : R.string.silent_mode_is_off));
		intent.putExtra(NotificationService.SILENT_MODE_STATUS, status);
		App.self().sendBroadcast(StartJobIntentServiceReceiver.getIntent(context, intent, JOB_ID));

		intent = new Intent(context, AudioManagerService.class);
		intent.putExtra(
				AudioManagerService.ARG_ACTION,
				status ? AudioManagerService.ACTION_START : AudioManagerService.ACTION_STOP);
		App.self().sendBroadcast(StartJobIntentServiceReceiver.getIntent(context, intent, JOB_ID));
	}
}
