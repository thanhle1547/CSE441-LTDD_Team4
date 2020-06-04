package com.example.devicesilencingapp.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.preference.PreferenceManager;

import com.example.devicesilencingapp.libs.AudioManagerUtils;

import java.util.HashMap;
import java.util.Map;

public class AudioManagerService extends JobIntentService {
	private static final String TAG = AudioManagerService.class.getSimpleName();

	public static final int ACTION_START = 0;
	public static final int ACTION_STOP = 1;

	public static final String ARG_ACTION = "action";
	private static final String KEY_SILENT_MODE_STATUS = "silent_mode_status";

	private AudioManager mAudioManager;
	private SharedPreferences mSharedPrefs;

	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (intent.getIntExtra(ARG_ACTION, 0) == ACTION_START) {
			if (!mSharedPrefs.getBoolean(KEY_SILENT_MODE_STATUS, false)) {
				startSilentMode();
				saveSilentModeStatus(true);
			}
		}
		else {
			if (mSharedPrefs.getBoolean(KEY_SILENT_MODE_STATUS, true)) {
				stopSilentMode();
				saveSilentModeStatus(false);
			}
		}
	}

	private void saveConfigAtNormalState() {
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putInt(AudioManagerUtils.SILENT_RINGER_MODE, mAudioManager.getRingerMode());

		for (Map.Entry e : AudioManagerUtils.getNormalConfig().entrySet())
			editor.putInt(
					e.getKey().toString(),
					mAudioManager.getStreamVolume(
							Integer.parseInt(e.getValue().toString())
			));

		editor.apply();
	}

	private void saveSilentModeStatus(boolean status) {
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putBoolean(KEY_SILENT_MODE_STATUS, status);
		editor.apply();
	}

	private void adjustVolume(HashMap<String, Integer> config) {
		for (Map.Entry e : config.entrySet())
			mAudioManager.setStreamVolume(
					(int) e.getValue(),
					mSharedPrefs.getInt(e.getKey().toString(), 0),
					0
			);
	}

	private void startSilentMode() {
		saveConfigAtNormalState();
		adjustVolume(AudioManagerUtils.getSilentConfig());
	}

	private void stopSilentMode() {
		int mode = mSharedPrefs.getInt(AudioManagerUtils.SILENT_RINGER_MODE, 0);

		// TH ban đầu không bật chế độ yên lặng thì chuyển về về chế độ đã được lưu để điều chỉnh âm lượng
		if (mode != AudioManager.RINGER_MODE_SILENT)
			mAudioManager.setRingerMode(mode);

		adjustVolume(AudioManagerUtils.getNormalConfig());

		// đặt lại về yên lặng nếu ban đầu được để như vậy
		if (mode == AudioManager.RINGER_MODE_SILENT)
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}
}
