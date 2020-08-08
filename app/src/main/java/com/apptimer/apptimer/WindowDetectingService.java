package com.apptimer.apptimer;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.Others.MySettingPreferences;
import com.apptimer.apptimer.Others.RealmDatabase;
import com.apptimer.apptimer.activities.BlockActivity;
import com.apptimer.apptimer.activities.SecurityNumActivity;
import com.apptimer.apptimer.models.AppClass;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.RealmList;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class WindowDetectingService extends AccessibilityService {
    private int category;
    private com.apptimer.apptimer.models.Timer timer;
    private java.util.Timer timeSchedule = new Timer();
    private String packageName = "";//com.facebook.orca
    private RealmDatabase database;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
            try {
//                ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
//                openedNow = !packageName.equals(event.getPackageName().toString());
                MyApplication.getPref().setLastPackage(packageName);
                packageName = event.getPackageName().toString();

                Log.d("MyService:if stmt", packageName + "/" + "setting blocked " + MyApplication.getPref().isBlockedSettings() +
                        "Activity Started " + MyApplication.getPref().isSecurityNumActivityStarted());
                if ((packageName.equals(Constants.SETTING_PACKAGE) && MyApplication.getPref().isBlockedSettings()
                        && !MyApplication.getPref().isSecurityNumActivityStarted())) {


                   openSecurityNumActivity(Constants.BLOCK_SETTINGS);

                    Log.d("MyService:packagea", "if stmt");
                } else if (packageName.equals(Constants.GOOGLE_PLAY_PACKAGE) && MyApplication.getPref().isGooglePlayBlocked()) {

                   openSecurityNumActivity(Constants.BLOCK_GOOGLE_PLAY);

                }

                if (isGooglePlayClosed()) {
                    //set Blocking
                    MyApplication.getPref().setBlockGooglePlay(true);
                }
                if (isSettingClosed()) {
                    //set Blocking
                    MyApplication.getPref().setBlockedSettings(true);
                }
                Log.d("MyService:package", "class name " + event.getClassName());
                Log.d("MyService:package", "packageName " + event.getPackageName());

                Log.d("MyService:packagea", MyApplication.getPref().isBlockedSettings() + "");

            } catch (Exception ex) {

                Log.d("MyService:catchAcces", ex.getMessage() + "");
            }

        }
    }

    private void openSecurityNumActivity(String blockSettings) {
        Intent lockActivity = new Intent(WindowDetectingService.this, SecurityNumActivity.class);
        lockActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        lockActivity.putExtra("extra", blockSettings);
        startActivity(lockActivity);
        MyApplication.getPref().setSecurityNumActivityStarted(true);
    }

    private boolean isSettingClosed() {
        try {
            return MyApplication.getPref().getLastPackage().equals(Constants.SETTING_PACKAGE) &&
                    !packageName.equals(Constants.SETTING_PACKAGE);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isGooglePlayClosed() {
        try {
            return MyApplication.getPref().getLastPackage().equals(Constants.GOOGLE_PLAY_PACKAGE) &&
                    !packageName.equals(Constants.GOOGLE_PLAY_PACKAGE);
        } catch (Exception ex) {
            return false;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService:onStartCom", "onStartCommand");


        timeSchedule.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    database = new RealmDatabase();
                    Log.d("MyService:package1", "packageName " + packageName);


                    if (packageName.equals(Constants.INPUT_METHOD_PACKAGE_NAME)) {
                        packageName = MyApplication.getPref().getLastPackage();
                    }
                    Log.d("MyService:package2", "packageName " + packageName);

                    AppClass app = database.getApp(packageName);

                    Log.d("MyService: App ", app.toString());
                    if (MyApplication.getPref().isTimerStarted(Constants.ALL_CATEGORIES) &&
                            app.isLocked()  /*&& !openedNow*/) { // app stored in the database
                        category = app.getCategory();
                        Log.d("MyService:category", category + "");
                        timer = database.getTimer(category);
                        Log.d("MyService: timer", timer.toString());
                        Log.d("MyService:remainedTime1", timer.getRemainedTime() + "");

                        if (timer.getRemainedTime() == 0) {
                            Intent lockActivity = new Intent(getApplicationContext(), BlockActivity.class);
                            lockActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(lockActivity);
                        } else {
                            if (timer.isDaysSpecified())
                                Log.d("MyService:Days", "isToday:" + isTodaySpecified(timer.getDays()));
                            else
                                Log.d("MyService:", "withinRange: Date" + isWithinDateRange(timer.getStartDate(), timer.getEndDate()));


                            if ((timer.isDaysSpecified() && isTodaySpecified(timer.getDays())) ||
                                    (!timer.isDaysSpecified() && isWithinDateRange(timer.getStartDate(), timer.getEndDate()))) {
                                double remainedTime = roundToOneDecimalDigit(timer.getRemainedTime() - 0.1);
                                database.decreaseTimer(category, remainedTime);
                                Log.d("MyService:remainedTime2", timer.getRemainedTime() + "");

                            }
                        }
                    }

                } catch (Exception ex) {
                    Log.d("MyService: catch", ex.getMessage() + "");
                } finally {
                    database.closeRealm();
                }

            }
        }, 0, 6000);


        return START_STICKY;
    }

    private double roundToOneDecimalDigit(double v) {

        int integerPart = (int) v;  // v= 1.5999999 , integerPart = 1
        double fractionPart = v - integerPart; // fractionPart =0.59999999
        int roundedFraction = (int) Math.round(fractionPart * 10); //roundedFraction = 6
        return integerPart + (roundedFraction / 10.0);  //result = 1 + 0.6 = 1.6
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onCreate");

    }

    private boolean isWithinDateRange(Date startDate, Date endDate) {
        Date currentDate = new Date();
        return currentDate.after(startDate) && currentDate.before(endDate);
    }

    private boolean isTodaySpecified(RealmList<Boolean> days) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        try {
            return days.get(today - 1);
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public void onInterrupt() {
        // Ignored
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestroy");

        if (timeSchedule != null) {
            timeSchedule.cancel();
            timeSchedule = null;
        }

    }
}
