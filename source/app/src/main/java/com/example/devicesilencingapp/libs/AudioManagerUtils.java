package com.example.devicesilencingapp.libs;

import android.content.Context;
import android.media.AudioManager;

import com.example.devicesilencingapp.App;

import java.util.HashMap;

public class AudioManagerUtils {
	public static final String VOLUME_RING = "volume_ring";
	public static final String VOLUME_MEDIA = "volume_media";
	public static final String VOLUME_ALARM = "volume_alarm";
	public static final String VOLUME_NOTIFICATION = "volume_notification";
	public static final String VOLUME_SYSTEM = "volume_system";

	private static final String NORMAL_STATE = "ns_";
	private static final String SILENT_STATE = "ss_";

	public static final String SILENT_RINGER_MODE = SILENT_STATE + "ringer_mode";

	/**
	 * Một biến static final mà không được khởi tạo tại thời điểm khai báo thì đó là biến static final trống.
	 * Nó chỉ có thể được khởi tạo trong khối static.
	 *
	 * @see <a href="https://viettuts.vn/java/tu-khoa-final-trong-java">Từ khóa final trong java</a>
 	 */
	private static final HashMap<String, Integer> normal_config, silent_config;

	/**
	 * static block (static clause) used for static initializations of a class
	 *
	 * This code inside static block is executed only once:
	 * the first time you make an object of that class
	 * or the first time you access a static member of that class (even if you never make an object of that class).
	 *
	 * @see <a href="https://www.geeksforgeeks.org/g-fact-79/">Static blocks in Java</a>
 	 */
	static {
		normal_config = new HashMap<>();
		silent_config = new HashMap<>();

		normal_config.put(NORMAL_STATE + VOLUME_RING, AudioManager.STREAM_RING);
		normal_config.put(NORMAL_STATE + VOLUME_MEDIA, AudioManager.STREAM_MUSIC);
		normal_config.put(NORMAL_STATE + VOLUME_ALARM, AudioManager.STREAM_ALARM);
		normal_config.put(NORMAL_STATE + VOLUME_NOTIFICATION, AudioManager.STREAM_NOTIFICATION);
		normal_config.put(NORMAL_STATE + VOLUME_SYSTEM, AudioManager.STREAM_SYSTEM);

		silent_config.put(SILENT_STATE + VOLUME_RING, AudioManager.STREAM_RING);
		silent_config.put(SILENT_STATE + VOLUME_MEDIA, AudioManager.STREAM_MUSIC);
		silent_config.put(SILENT_STATE + VOLUME_ALARM, AudioManager.STREAM_ALARM);
		silent_config.put(SILENT_STATE + VOLUME_NOTIFICATION, AudioManager.STREAM_NOTIFICATION);
		silent_config.put(SILENT_STATE + VOLUME_SYSTEM, AudioManager.STREAM_SYSTEM);
	}

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

	public static HashMap<String, Integer> getNormalConfig() {
		return normal_config;
	}

	public static HashMap<String, Integer> getSilentConfig() {
		return silent_config;
	}
}
