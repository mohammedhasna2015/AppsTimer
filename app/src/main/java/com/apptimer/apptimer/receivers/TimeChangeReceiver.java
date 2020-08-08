package com.apptimer.apptimer.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.apptimer.apptimer.Others.MyApplication;

import java.util.Calendar;

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent arg1) {

        Log.d("Receiver","TimeChangeReceiver");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra("receiver",true);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        MyApplication.getPref().setAlarmFromReceivers(true);

        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}