package com.apptimer.apptimer.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.WindowDetectingService;
import com.apptimer.apptimer.Others.MySettingPreferences;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent arg1) {
//        Toast.makeText(context, "BootReceiver", Toast.LENGTH_SHORT).show();
        Log.d("Receiver","BootReceiver");


//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//
//        // With setInexactRepeating(), you have to use one of the AlarmManager interval
//        // constants--in this case, AlarmManager.INTERVAL_DAY.
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
//


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra("receiver",true);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        MySettingPreferences pref= MyApplication.getPref();
        pref.setAlarmFromReceivers(true);

        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);


        Log.d("ReceiverBoot",pref.isTimerStarted(Constants.ALL_CATEGORIES)+"");

        if(pref.isTimerStarted(Constants.ALL_CATEGORIES)){
            Intent serviceIntent=new Intent(context, WindowDetectingService.class);
            //serviceIntent.putExtra("ActivatedCategories",Constants.ALL_CATEGORIES);
            context.startService(serviceIntent);
        }
    }
}