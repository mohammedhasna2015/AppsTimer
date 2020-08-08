package com.apptimer.apptimer.Others;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptimer.apptimer.Others.Constants;

public class MySettingPreferences {



    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    public MySettingPreferences(Context context){

            sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.apply();

    }

    public void startTimer(int category){
        editor.putBoolean("category"+category, true).commit();
    }


    public void stopTimer(int category){
        editor.putBoolean("category"+category, false).commit();
    }

    public boolean isTimerStarted(int category){
        return sharedPreferences.getBoolean("category"+category ,false);
    }

    public void setAlarmFromReceivers(boolean value){
        editor.putBoolean("fromReceiver",value).apply();
    }

    public boolean isAlarmFromReceivers(){
        return sharedPreferences.getBoolean("fromReceiver",false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean("isFirstTimeLaunch", isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean("isFirstTimeLaunch", true);
    }

    public String getLastPackage() {
        return sharedPreferences.getString("LastPackage", "");

    }
    public void setLastPackage(String packageName) {
        editor.putString("LastPackage", packageName);
        editor.commit();
    }

    public String getLang(){
         int lang = sharedPreferences.getInt("lang", -1);

         switch (lang){
             case Constants.ARABIC: return "ar";
             case Constants.ENGLISH: return "en";
             default:return "";
         }
    }

    public void setLang(int lang){
        editor.putInt("lang", lang).apply();
    }

    public int getLangIndex() {
        int lang = sharedPreferences.getInt("lang", -1);
        if(lang == -1){
            lang = Constants.ENGLISH;
        }
        return lang;
    }

    public String getSecurityNum() {
        return sharedPreferences.getString("securityNum","");
    }

    public void setSecurityNum(String num) {
         editor.putString("securityNum",num).apply();
    }

    public boolean isBlockedSettings() {
        return sharedPreferences.getBoolean("blockedSettings",true);
    }

    public void setBlockedSettings(boolean checked) {
        editor.putBoolean("blockedSettings",checked).commit();
    }

    public void setDestroyedFromLangChange(boolean b) {
        editor.putBoolean("LangChange",b).commit();
    }

    public boolean isDestroyedFromLangChange() {
        return sharedPreferences.getBoolean("LangChange",false);
    }

    public void setAccessibilityDialogChecked(boolean b) {
        editor.putBoolean("accessibilityDialogChecked",b).apply();
    }

    public boolean isAccessibilityDialogChecked() {
        return sharedPreferences.getBoolean("accessibilityDialogChecked",false);
    }

    public  boolean isSecurityNumActivityStarted() {
        return sharedPreferences.getBoolean("SecurityNumActivityStarted",false);
    }

    public  void setSecurityNumActivityStarted(boolean b) {
        editor.putBoolean("SecurityNumActivityStarted",b).commit();
    }

    public void setBlockGooglePlay(boolean checked) {
        editor.putBoolean("BlockGooglePlay",checked).commit();
    }

    public boolean isGooglePlayBlocked() {
        return sharedPreferences.getBoolean("BlockGooglePlay",true);
    }
}
