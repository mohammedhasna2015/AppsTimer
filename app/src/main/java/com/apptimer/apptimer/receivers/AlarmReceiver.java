package com.apptimer.apptimer.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.Others.RealmDatabase;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "AlarmReceiver", Toast.LENGTH_SHORT).show();
        Log.d("Receiver","AlarmReceiver");
//        Toast.makeText(context, "Hour ="+c.get(Calendar.HOUR_OF_DAY), Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, "receiver ="+MyApplication.getPref().isAlarmFromReceivers(), Toast.LENGTH_SHORT).show();
        Log.d("MyService","receiver ="+MyApplication.getPref().isAlarmFromReceivers());
        if(!MyApplication.getPref().isAlarmFromReceivers()){
            RealmDatabase database = new RealmDatabase();
            database.resetRemainedTime();
            database.closeRealm();
            showSmallNotification(context,(int)System.currentTimeMillis(),"App Timer","Timer is refreshed");
        }
        else{
            MyApplication.getPref().setAlarmFromReceivers(false);
        }

    }

    private void showSmallNotification(Context context, int id, String title, String message) {

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,channelId);
        Notification notification;
        notification = mBuilder
                .setSmallIcon(R.mipmap.icon)//هان حطينا الصورة الصغيرة
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon))
                .setAutoCancel(true)
                .setTicker(title).setWhen(System.currentTimeMillis())//هاد عنوان
                .setContentTitle(title)
                .setSound(defaultSoundUri)
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id, notification);
    }

}
