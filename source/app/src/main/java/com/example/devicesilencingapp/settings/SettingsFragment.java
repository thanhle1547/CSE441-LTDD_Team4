package com.example.devicesilencingapp.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.devicesilencingapp.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String KEY_APP_STATUS = "app_status";
    private static final String KEY_LOCATION_STATUS = "location_status";
    private static final String KEY_SCHEDULE_STATUS = "schedule_status";

    private SharedPreferences preference = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
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
                break;
            case KEY_LOCATION_STATUS:
                // TODO: Dừng service geofence
                break;
            case KEY_SCHEDULE_STATUS:
                // TODO: Dừng service alarm
                break;
            default:
                break;
        }
    }
}
