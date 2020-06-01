package com.example.devicesilencingapp.libs;

import android.content.Context;
import android.media.AudioManager;

public class AudioManagerUtils {
	public static final String VOLUME_RING = "volume_ring";
	public static final String VOLUME_MEDIA = "volume_media";
	public static final String VOLUME_ALARM = "volume_alarm";
	public static final String VOLUME_NOTIFICATION = "volume_notification";
	public static final String VOLUME_SYSTEM = "volume_system";

	private static AudioManagerUtils mInstance;
	private AudioManager mAudioManager;

	public AudioManagerUtils() {
		mAudioManager = (AudioManager) App.self().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	}

	public static AudioManagerUtils getInstance() {
		if (mInstance == null)
			mInstance = new AudioManagerUtils();
		return mInstance;
	}

	public AudioManager getAudioManager() {
		return mAudioManager;
	}
}
