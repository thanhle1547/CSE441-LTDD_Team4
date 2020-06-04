package com.example.devicesilencingapp.time;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.example.devicesilencingapp.libs.SilentModeManagerUtil;

public class TimeService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("toi trong service","toois trong service");
        TimeManager.setAlarms(this);

        // Cập nhật thông báo và chuyển máy về chế độ yên lặng
        SilentModeManagerUtil.performAction(getApplicationContext(), true);
    }
}
