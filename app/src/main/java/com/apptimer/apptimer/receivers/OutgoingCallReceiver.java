package com.apptimer.apptimer.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.activities.SplashActivity;

public class OutgoingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (phoneNumber.equals(MyApplication.getPref().getSecurityNum())){
            unhideApp(context);
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(Constants.APP_TIMER_PACKAGE_NAME);
            context.startActivity( launchIntent );
//            Intent intent2 = new Intent();
//            intent2.setClass(context, MainActivity.class);
//            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);
        }
        Log.d(OutgoingCallReceiver.class.getSimpleName(), phoneNumber);
    }

    private void unhideApp(Context context){
        PackageManager p = context.getApplicationContext().getPackageManager();
        ComponentName componentName = new ComponentName(context.getApplicationContext(), SplashActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0);

    }
}
