package com.apptimer.apptimer.Others;

import android.app.Application;
import android.content.SharedPreferences;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    private static MySettingPreferences sharedPrefInstance;

    public static SharedPreferences.Editor editor;
    @Override
    public void onCreate() {
        super.onCreate();

//        sharedpreferences = getSharedPreferences("Apptimer", Context.MODE_PRIVATE);
//        editor = sharedpreferences.edit();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("appTimer.realm") // Name of Database
                .schemaVersion(0) // Number of version
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        sharedPrefInstance=new MySettingPreferences(this);
    }

    public static MySettingPreferences getPref(){
        return sharedPrefInstance;
    }


}
