package com.example.devicesilencingapp.time;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.devicesilencingapp.libs.SilentModeManagerUtil;

public class TimeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.e("toi trong service","toois trong service");
        TimeManager.setAlarms(this);

        // Cập nhật thông báo và chuyển máy về chế độ yên lặng
        SilentModeManagerUtil.performAction(this, true);

        return super.onStartCommand(intent, flags, startId);
    }
}
