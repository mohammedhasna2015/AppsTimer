package com.apptimer.apptimer.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.MailTo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.fragments.DashboardFragment;
import com.apptimer.apptimer.fragments.HomeFragment;
import com.apptimer.apptimer.fragments.SettingsFragment;
import com.apptimer.apptimer.utils.FragmentUtil;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         String lang= MyApplication.getPref().getLang();
         if (lang.equals("")){
             String defaultLang=Locale.getDefault().getLanguage();
             setLocale(defaultLang);
             if(defaultLang.equals("ar")){
                 MyApplication.getPref().setLang(Constants.ARABIC);
             }else if(defaultLang.equals("en")){
                 MyApplication.getPref().setLang(Constants.ENGLISH);

             }
         }
         else {
             setLocale(lang);
         }

        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if(savedInstanceState == null){
            FragmentUtil.addFragment(this,new HomeFragment() ,R.id.main_frame);
        }

        if(MyApplication.getPref().getSecurityNum().equals("")) {
            showAddSecurityNumDialog();
        }

    }

    private void showAddSecurityNumDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_security_num_dialog);
        dialog.setCancelable(false);
        final EditText et_num =dialog.findViewById(R.id.et_security_num);
        dialog.findViewById(R.id.btn_add_security_num).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_num.getText().toString().trim().equals("")){
                    et_num.setError(getResources().getString(R.string.empty_error));
                    return;
                }
                MyApplication.getPref().setSecurityNum(et_num.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getFragmentManager().popBackStack();
    }

//    Intent refresh = new Intent(this, MainActivity.class);
//    startActivity(refresh);
//    finish();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentUtil.replaceFragment(MainActivity.this,new HomeFragment(),R.id.main_frame);
                    return true;
                case R.id.navigation_dashboard:
                    FragmentUtil.replaceFragment(MainActivity.this,new DashboardFragment(),R.id.main_frame);
                    return true;
                case R.id.navigation_settings:
                    FragmentUtil.replaceFragment(MainActivity.this,new SettingsFragment(),R.id.main_frame);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onDestroy() {
        MyApplication.getPref().setBlockedSettings(true);
        super.onDestroy();
        if(MyApplication.getPref().isDestroyedFromLangChange()){
            MyApplication.getPref().setDestroyedFromLangChange(false);
        }else {
            hideAppLauncher();
        }
    }

    private void hideAppLauncher(){
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(getApplication(), SplashActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.confirm_exit));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}
