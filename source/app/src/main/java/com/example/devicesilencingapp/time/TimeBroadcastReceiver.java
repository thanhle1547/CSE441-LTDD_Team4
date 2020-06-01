package com.example.devicesilencingapp.time;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.devicesilencingapp.db.DBHelper;

import java.util.Calendar;
import java.util.List;

public class TimeBroadcastReceiver extends BroadcastReceiver {


    public static final String ID = "id";
    public static final String TIME_HOUR = "timeHour";
    public static final String TIME_MINUTE = "timeMinute";

    String TAG = this.getClass().getSimpleName();
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("chay ok","pk toi ok chay");
        setAlarms(context);
    }

    public static void setAlarms(Context context) {
        //Hủy tất cả lịch đã cài đặt
        //Khởi tạo lại với tất cả alarm đang bật
        //Kiểm tra ngày của alram và update lại
        cancelAlarms(context);

        DBHelper dbHelper = new DBHelper(context);

        List<timeModel> alarms = dbHelper.getAlarms();

        for (timeModel alarm:alarms) {
            if (alarm.isEnabled) {
                PendingIntent pIntent = createPendingIntent(context, alarm);

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, alarm.timeHour);
                calendar.set(Calendar.MINUTE, alarm.timeMinute);
                calendar.set(Calendar.SECOND, 00);

                final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
                boolean alarmSet = false;


                //kiem tra neu ngày hiện tại mà báo thức chưa đến giờ thì alarmset = true
                if ((alarm.timeHour > nowHour) | ((alarm.timeHour == nowHour) && (alarm.timeMinute > nowMinute))){
                    calendar.set(Calendar.DAY_OF_WEEK, nowDay);
                    setAlarm(context, calendar, pIntent);
                    alarmSet = true;
                } else {
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++ dayOfWeek) {
                        if ( alarm.getRepeatingDay(dayOfWeek -1 ) && dayOfWeek >= nowDay && !(dayOfWeek == nowDay && alarm.timeHour < nowHour)
                                && !(dayOfWeek == nowDay && alarm.timeHour == nowHour && alarm.timeMinute <= nowMinute)) {
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                            Log.i("abc123", "set Alarmed: " + nowDay + " : " + alarm.timeHour + " : " + alarm.timeMinute);

                            setAlarm(context, calendar, pIntent);
                            alarmSet = true;
                            break;
                        }
                    }
                }

                if (!alarmSet) {
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++ dayOfWeek) {
                        if (alarm.getRepeatingDay(dayOfWeek -1 ) && dayOfWeek <= nowDay) {
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                            //calendar.add(Calendar.WEEK_OF_YEAR, 1);

                            setAlarm(context, calendar, pIntent);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
//		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
//		Toast.makeText(this, "Alarm scheduled for tomorrow", Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarms(Context context) {
        DBHelper dbHelper = new DBHelper(context);

        List<timeModel> alarms = dbHelper.getAlarms();

        if (alarms != null)
        {
            for (timeModel alarm:alarms) {
                if (alarm.isEnabled) {
                    PendingIntent pIntent = createPendingIntent(context, alarm);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarmManager.cancel(pIntent);
                }
            }
        }
    }

    private static PendingIntent createPendingIntent(Context context, timeModel model) {
        Intent intent = new Intent(context, TimeService.class);
        intent.putExtra(ID, model.id);
        intent.putExtra(TIME_HOUR, model.timeHour);
        intent.putExtra(TIME_MINUTE, model.timeMinute);
        return PendingIntent.getService(context, (int) model.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
