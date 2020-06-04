package com.example.devicesilencingapp.libs;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
	private static final String TAG = LocationUtils.class.getSimpleName();

	/**
	 *
	 * @param latitude
	 * @param longitude
	 * @return String
	 *
	 * {@code
	 * LocationUtils.toString(40.7127837, -74.0059413);
	 * return => N 40°42'46.02132" W 74°0'21.38868"
	 * }
	 *
	 * @see <a href="https://stackoverflow.com/a/38551230">Android How to Convert Latitude Longitude into Degree format</a>
	 */
	public static String toString(double latitude, double longitude) {
		StringBuilder builder = new StringBuilder();

		if (latitude < 0) {
			builder.append("S ");
		} else {
			builder.append("N ");
		}

		String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
		String[] latitudeSplit = latitudeDegrees.split(":");
		builder.append(latitudeSplit[0]);
		builder.append("°");
		builder.append(latitudeSplit[1]);
		builder.append("'");
		builder.append(latitudeSplit[2]);
		builder.append("\"");

		builder.append(" ");

		if (longitude < 0) {
			builder.append("W ");
		} else {
			builder.append("E ");
		}

		String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
		String[] longitudeSplit = longitudeDegrees.split(":");
		builder.append(longitudeSplit[0]);
		builder.append("°");
		builder.append(longitudeSplit[1]);
		builder.append("'");
		builder.append(longitudeSplit[2]);
		builder.append("\"");

		return builder.toString();
	}

	/**
	 *
	 * @param latitude
	 * @param longitude
	 * @param context
	 * @param handler
	 *
	 * @see <a href="https://javapapers.com/android/android-get-address-with-street-name-city-for-location-with-geocoding/"></a>
	 */
	public static void getAddressFromLocation(final double latitude, final double longitude,
	                                          final Context context, final Handler handler) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				String result = null;
				try {
					List<Address> addressList = geocoder.getFromLocation(
							latitude, longitude, 1);
					if (addressList != null && addressList.size() > 0) {
						Address address = addressList.get(0);
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
							sb.append(address.getAddressLine(i)).append("\n");
						}
						sb.append(address.getAddressLine(0));
						result = sb.toString();
					}
				} catch (IOException e) {
					Log.e(TAG, "Unable connect to Geocoder", e);
				} finally {
					Message message = Message.obtain();
					message.setTarget(handler);
					message.what = 1;
					Bundle bundle = new Bundle();
					if (result == null)
						result = "Không thể lấy được địa chỉ";
					bundle.putString("address", result);
					message.setData(bundle);
					message.sendToTarget();
				}
			}
		};
		thread.start();
	}
}
