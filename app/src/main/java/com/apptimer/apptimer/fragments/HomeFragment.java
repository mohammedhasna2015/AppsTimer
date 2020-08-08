package com.apptimer.apptimer.fragments;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.activities.CategoryActivity;
import com.apptimer.apptimer.receivers.AlarmReceiver;
import com.apptimer.apptimer.Others.MySettingPreferences;
import com.apptimer.apptimer.WindowDetectingService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private LinearLayout  linearOthers;
    private LinearLayout linearEntertainment;
    private LinearLayout linearGames;
    private LinearLayout linearSocial;
    public HomeFragment() {
        // Required empty public constructor
    }
    private FrameLayout activate_btn,deactivate_btn;
    private MySettingPreferences pref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        pref= MyApplication.getPref();
         linearGames=view.findViewById(R.id.linear_games);
         linearEntertainment=view.findViewById(R.id.linear_entertainment);
         linearSocial=view.findViewById(R.id.linear_social);
         linearOthers=view.findViewById(R.id.linear_others);

         linearEntertainment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(getActivity(), CategoryActivity.class);
                 intent.putExtra("category", Constants.ENTERTAINMENT);
                 startActivity(intent);
             }
         });

        linearGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("category", Constants.GAMES);
                startActivity(intent);
            }
        });
        linearSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("category", Constants.SOCIAL);
                startActivity(intent);
            }
        });

        linearOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("category", Constants.OTHERS);
                startActivity(intent);
            }
        });


        activate_btn=view.findViewById(R.id.activate_timer);
        deactivate_btn=view.findViewById(R.id.deActivate_timer);

        if(pref.isTimerStarted(Constants.ALL_CATEGORIES)){
            startTimerUI();
        }else{

            stopTimerUI();
        }
        activate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pref.isAccessibilityDialogChecked()) {
                    showCheckAccessibilityDialog();
                }
                else{
                    activateTimer();
                }
            }
        });
        deactivate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimerUI();
                cancelAlarmManager();
                pref.stopTimer(Constants.ALL_CATEGORIES);
                Log.d("deactivate_btn",pref.isTimerStarted(Constants.ALL_CATEGORIES)+"");
//                Intent serviceIntent=new Intent(getActivity(), WindowDetectingService.class);
//                getActivity().stopService(serviceIntent);
            }
        });

        return view;
    }

    private void activateTimer() {
        startTimerUI();
        setAlarmToRefresh();
        pref.startTimer(Constants.ALL_CATEGORIES);
        Log.d("activate_btn",pref.isTimerStarted(Constants.ALL_CATEGORIES)+"");
        Intent serviceIntent=new Intent(getActivity(), WindowDetectingService.class);
        //serviceIntent.putExtra("ActivatedCategories",Constants.ALL_CATEGORIES);
        getActivity().startService(serviceIntent);
    }

    private void showCheckAccessibilityDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.check_accessability_dialog);
        dialog.setCancelable(false);
        CheckBox checkBox=dialog.findViewById(R.id.ch_accessibility);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    pref.setAccessibilityDialogChecked(true);
                }else{
                    pref.setAccessibilityDialogChecked(false);
                }
            }
        });
        dialog.findViewById(R.id.btn_turn_accessibility_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                activateTimer();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setAlarmToRefresh() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        pref.setAlarmFromReceivers(false);

        AlarmManager alarmManager=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private void cancelAlarmManager(){
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        AlarmManager alarmManager=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }
    private void startTimerUI(){
        deactivate_btn.setVisibility(View.VISIBLE);
        activate_btn.setVisibility(View.GONE);
    }

    private void stopTimerUI(){
        deactivate_btn.setVisibility(View.GONE);
        activate_btn.setVisibility(View.VISIBLE);
    }
}
