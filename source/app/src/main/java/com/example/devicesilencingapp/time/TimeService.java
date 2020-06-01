package com.example.devicesilencingapp.time;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TimeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        TimeBroadcastReceiver.setAlarms(this);
        return super.onStartCommand(intent, flags, startId);
    }
}
