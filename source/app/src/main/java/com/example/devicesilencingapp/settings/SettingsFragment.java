package com.example.devicesilencingapp.settings;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceFragmentCompat;

import com.example.devicesilencingapp.BuildConfig;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.services.GeofencesService;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = SettingsFragment.class.getSimpleName();
	private static final String PACKAGE_NAME = "com.example.devicesilencingapp";

	public static final String KEY_APP_STATUS = "app_status";
    public static final String KEY_LOCATION_STATUS = "location_status";
    private static final String KEY_SCHEDULE_STATUS = "schedule_status";

	public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
	private static final int REQUEST_PERMISSIONS_CODE = 34;

	private SharedPreferences preference = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
        preference = getPreferenceScreen().getSharedPreferences();
        preference.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        preference.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        preference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_APP_STATUS:
            	// TODO: Dừng cả 2 service
	            if (sharedPreferences.getBoolean(KEY_APP_STATUS, true)) {
	            	if (sharedPreferences.getBoolean(KEY_LOCATION_STATUS, true))
	                    performGeofenceServiceAction(true);
	            } else {
		            performGeofenceServiceAction(false);
	            }
                break;
            case KEY_LOCATION_STATUS:
	            performGeofenceServiceAction(sharedPreferences.getBoolean(KEY_LOCATION_STATUS, true));
	            break;
            case KEY_SCHEDULE_STATUS:
                // TODO: Dừng service alarm
                break;
            default:
                break;
        }
    }

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		Log.i(TAG, "onRequestPermissionResult");
		if (requestCode == REQUEST_PERMISSIONS_CODE) {
			if (grantResults.length <= 0) {
				// If user interaction was interrupted, the permission request is cancelled and you
				// receive empty arrays.
				Log.i(TAG, "User interaction was cancelled.");
			} else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.i(TAG, "Permission granted.");
				performGeofenceTask(GeofencesService.PendingGeofenceTask.ADD);
			} else {
				// Permission denied.

				// Notify the user via a SnackBar that they have rejected a core permission for the
				// app, which makes the Activity useless. In a real app, core permissions would
				// typically be best requested during a welcome-screen flow.

				// Additionally, it is important to remember that a permission might have been
				// rejected without asking the user for permission (device policy or "Never ask
				// again" prompts). Therefore, a user interface affordance is typically implemented
				// when permissions are denied. Otherwise, your app could appear unresponsive to
				// touches or interactions which have required permissions.
				showSnackbar(R.string.permission_denied_explanation, R.string.settings,
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								// Build intent that displays the App settings screen.
								Intent intent = new Intent();
								intent.setAction(
										Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								Uri uri = Uri.fromParts("package",
										BuildConfig.APPLICATION_ID, null);
								intent.setData(uri);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						});
				performGeofenceTask(GeofencesService.PendingGeofenceTask.NONE);
			}
		}
	}

	/**
	 * Return the current state of the permissions needed.
	 */
	private boolean checkPermissions() {
		int permissionState = ActivityCompat.checkSelfPermission(
				requireActivity().getApplicationContext(),
				Manifest.permission.ACCESS_FINE_LOCATION);
		return permissionState == PackageManager.PERMISSION_GRANTED;
	}

	private void requestPermissions() {
		boolean shouldProvideRationale =
				ActivityCompat.shouldShowRequestPermissionRationale(
						requireActivity(),
						Manifest.permission.ACCESS_FINE_LOCATION);

		// Provide an additional rationale to the user. This would happen if the user denied the
		// request previously, but didn't check the "Don't ask again" checkbox.
		if (shouldProvideRationale) {
			Log.i(TAG, "Displaying permission rationale to provide additional context.");
			showSnackbar(R.string.permission_rationale, android.R.string.ok,
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							// Request permission
							ActivityCompat.requestPermissions(
									requireActivity(),
									new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
									REQUEST_PERMISSIONS_CODE);
						}
					});
		} else {
			Log.i(TAG, "Requesting permission");
			// Request permission. It's possible this can be auto answered if device policy
			// sets the permission in a given state or the user denied the permission
			// previously and checked "Never ask again".
			ActivityCompat.requestPermissions(
					requireActivity(),
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_PERMISSIONS_CODE);
		}
	}
	/**
	 * Shows a {@link Snackbar}.
	 *
	 * @param mainTextStringId The id for the string resource for the Snackbar text.
	 * @param actionStringId   The text of the action item.
	 * @param listener         The listener associated with the Snackbar action.
	 */
	private void showSnackbar(final int mainTextStringId, final int actionStringId,
	                          View.OnClickListener listener) {
		Snackbar.make(
				requireActivity().findViewById(android.R.id.content),
				getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE)
				.setAction(getString(actionStringId), listener).show();
	}

	private void performGeofenceTask(GeofencesService.PendingGeofenceTask task) {
		// Notify anyone listening for broadcasts about the new location.
		Intent intent = new Intent(ACTION_BROADCAST);
		intent.putExtra(GeofencesService.EXTRA_TASK, task);

		LocalBroadcastManager.getInstance(requireActivity().getApplicationContext()).sendBroadcast(intent);
	}

	private void performGeofenceServiceAction(boolean status) {
		if (status) {
			if (!checkPermissions()) {
				requestPermissions();
			} else {
				performGeofenceTask(GeofencesService.PendingGeofenceTask.ADD);
			}
		} else {
			performGeofenceTask(GeofencesService.PendingGeofenceTask.REMOVE);
		}
	}
}
