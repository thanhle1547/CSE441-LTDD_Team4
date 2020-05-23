package com.example.devicesilencingapp.libs;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.example.devicesilencingapp.App;
import com.example.devicesilencingapp.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonyama on 5/30/16.
 */
public class GeofenceUtils {
	private GeofenceUtils() {}

	/**
	 * Maps geofence transition types to their human-readable equivalents.
	 *
	 * @param transitionType    A transition type constant defined in Geofence
	 * @return                  A String indicating the type of transition
	 *
	 * @see <a href="https://github.com/android/location-samples/blob/f08652239fbc04e616d48ff27c1fdaee60c2bca0/Geofencing/app/src/main/java/com/google/android/gms/location/sample/geofencing/GeofenceTransitionsJobIntentService.java#L190">
	 *     Github</a>
	 */
	public static String getTransitionString(int transitionType) {
		switch (transitionType) {
			case Geofence.GEOFENCE_TRANSITION_ENTER:
				return App.self().getString(R.string.geofence_transition_entered);
			case Geofence.GEOFENCE_TRANSITION_EXIT:
				return App.self().getString(R.string.geofence_transition_exited);
			default:
				return App.self().getString(R.string.unknown_geofence_transition);
		}
	}

	/**
	 *
	 * @param geofenceTransition    The ID of the geofence transition (kiểu di chuyển).
	 * @param triggeringGeofences   The geofence(s) triggered.
	 * @return                      The transition details formatted as String.
	 */
	public static String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
		String geofenceTransitionString = GeofenceUtils.getTransitionString(geofenceTransition);

		// lấy kiểu di chuyển (IDs) của mỗi geofence đã được kích hoạt
		ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
		for (Geofence geofence : triggeringGeofences)
			triggeringGeofencesIdsList.add(geofence.getRequestId());

		String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

		return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
	}

	/**
	 * Returns the error string for a geofencing exception.
	 *
	 * @param context
	 * @param e
	 * @return              ErrorMessages
	 */
	public static String getErrorString(Context context, Exception e) {
		if (e instanceof ApiException) {
			return getErrorString(context, ((ApiException) e).getStatusCode());
		} else {
			return context.getResources().getString(R.string.unknown_geofence_error);
		}
	}

	/**
	 * Returns the error string for a geofencing error code.
	 *
	 * @param context
	 * @param errorCode
	 * @return              ErrorMessages
	 */
	public static String getErrorString(Context context, int errorCode) {
		Resources mResources = context.getResources();
		switch (errorCode) {
			case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
				return mResources.getString(R.string.geofence_not_available);
			case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
				return mResources.getString(R.string.geofence_too_many_geofences);
			case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
				return mResources.getString(R.string.geofence_too_many_pending_intents);
			default:
				return mResources.getString(R.string.unknown_geofence_error);
		}
	}
}
