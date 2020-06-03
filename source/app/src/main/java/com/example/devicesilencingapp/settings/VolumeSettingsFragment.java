package com.example.devicesilencingapp.settings;

import android.media.AudioManager;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.libs.AudioManagerUtils;

public class VolumeSettingsFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.volume_preferences, rootKey);
		AudioManager audioManager = AudioManagerUtils.getInstance().getAudioManager();

		((SeekBarPreference) findPreference(AudioManagerUtils.VOLUME_RING)).setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));

		((SeekBarPreference) findPreference(AudioManagerUtils.VOLUME_MEDIA)).setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

		((SeekBarPreference) findPreference(AudioManagerUtils.VOLUME_ALARM)).setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));

		((SeekBarPreference) findPreference(AudioManagerUtils.VOLUME_NOTIFICATION)).setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));

		((SeekBarPreference) findPreference(AudioManagerUtils.VOLUME_SYSTEM)).setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
	}
}
