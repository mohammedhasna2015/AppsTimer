package com.apptimer.apptimer.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.Others.RealmDatabase;
import com.apptimer.apptimer.models.Timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetTimerFragment extends Fragment {

    private RadioButton rb_days,rb_date;
    private TextView tv_startDate,tv_endDate;
    private ToggleButton tg_sat,tg_sun,tg_mon,tg_tues,tg_wed,tg_thurs,tg_fri;
    private final  int START_DATE=0;
    private final  int END_DATE=1;
    private int requiredDate;

    private LinearLayout daysLinear;
    private Date startDate,endDate;
    private Button btn_done;
    private EditText et_hours,et_min;
    private int hrs,mins;
    private boolean isDaySpecified;
    private RealmList<Boolean> selectedDays;
    private int category;
    private RealmDatabase database;
    public SetTimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_set_timer, container, false);


        database= new RealmDatabase();
        category = getArguments().getInt("category",0);

        selectedDays=new RealmList<>();
        for(int i=0; i<7 ;i++){
            selectedDays.add(false);
        }

        rb_date=v.findViewById(R.id.rb_date_range);
        rb_days=v.findViewById(R.id.rb_days);
        tv_startDate=v.findViewById(R.id.tv_start_date);
        tv_endDate=v.findViewById(R.id.tv_end_date);
        btn_done=v.findViewById(R.id.btn_timer_done);
        et_hours=v.findViewById(R.id.et_hr);
        et_min=v.findViewById(R.id.et_min);

        tg_wed=v.findViewById(R.id.wed);
        tg_tues=v.findViewById(R.id.tues);
        tg_thurs=v.findViewById(R.id.thur);
        tg_tues=v.findViewById(R.id.tues);
        tg_sun=v.findViewById(R.id.sun);
        tg_sat=v.findViewById(R.id.sat);
        tg_mon=v.findViewById(R.id.mon);
        tg_fri=v.findViewById(R.id.fri);

        setUIActions();

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_hours.setError(null);
                et_min.setError(null);
                closeKeyboard();
                String hr=et_hours.getText().toString().trim();
                String min=et_min.getText().toString().trim();

                if(hr.equals("") && min.equals("")) {
                    et_hours.setError("");
                    et_min.setError("");
                    Snackbar.make(btn_done,R.string.set_time,Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(!hr.equals("")){
                    hrs=Integer.parseInt(hr);
                }
                if(!min.equals("")){
                    mins=Integer.parseInt(min);
                }

                isDaySpecified = rb_days.isChecked();

                if(!isDaySpecified && (TextUtils.isEmpty(tv_startDate.getText()) || TextUtils.isEmpty(tv_endDate.getText()))){
                    Snackbar.make(btn_done,R.string.missing_date,Snackbar.LENGTH_LONG).show();
                    return;
                }
                Log.d("timer selectedDays",selectedDays.toString());

                int totalTime = mins+hrs*60;
                Timer timer=new Timer(category, totalTime , startDate, endDate, totalTime,selectedDays, isDaySpecified );
               Log.d("timer",timer.toString());
                database.setTimer(timer);
                getActivity().getSupportFragmentManager().popBackStack();


            }
        });

        setTimerData();
        return v;
    }


    private void setTimerData() {
        Timer timer= database.getTimer(category);
        if(timer != null){
            Log.d("timer setData", timer.toString());

            int hr= timer.getTotalTime() / 60;
            int min=timer.getTotalTime() % 60;
            et_hours.setText(String.valueOf(hr));
            et_min.setText(String.valueOf(min));
            Log.d("timer isDaysSpecified",timer.isDaysSpecified()+"");

            if(timer.isDaysSpecified()){
                enableDays();
                Log.d("testSelectedDays1",selectedDays.toString());
                selectedDays.clear();
                selectedDays.addAll(timer.getDays());
                Log.d("testSelectedDays2",selectedDays.toString());
                tg_fri.setChecked(timer.getDays().get(Constants.FRI));
                tg_mon.setChecked(timer.getDays().get(Constants.MON));
                tg_sat.setChecked(timer.getDays().get(Constants.SAT));
                tg_sun.setChecked(timer.getDays().get(Constants.SUN));
                tg_tues.setChecked(timer.getDays().get(Constants.TUE));
                tg_thurs.setChecked(timer.getDays().get(Constants.THUR));
                tg_wed.setChecked(timer.getDays().get(Constants.WED));



            }else{
                enableDate();
                startDate=timer.getStartDate();
                endDate=timer.getEndDate();
                tv_startDate.setText(toStringDate(timer.getStartDate()));
                tv_endDate.setText(toStringDate(timer.getEndDate()));

            }


        }
    }

    private void setUIActions() {
        tg_sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.SUN,b);
            }
        });

        tg_mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.MON,b);
            }
        });

        tg_tues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.TUE,b);
            }
        });

        tg_wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.WED,b);
            }
        });

        tg_thurs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.THUR,b);
            }
        });
        tg_fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.FRI,b);
            }
        });
        tg_sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedDays.set(Constants.SAT,b);
            }
        });
        rb_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDaySpecified=true;
                enableDays();

            }
        });

        rb_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDaySpecified=false;
                enableDate();
            }
        });

        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requiredDate=START_DATE;
                showDatePicker();
            }
        });
        tv_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requiredDate= END_DATE;
                showDatePicker();
            }
        });
    }

    private void enableDate() {
        rb_days.setChecked(false);
        rb_date.setChecked(true);
        disableToggles();
        tv_endDate.setEnabled(true);
        tv_startDate.setEnabled(true);
    }

    private void enableDays() {
        rb_date.setChecked(false);
        rb_days.setChecked(true);
        enableToggles();
        tv_endDate.setEnabled(false);
        tv_startDate.setEnabled(false);
    }

    private void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception ex){

        }
    }

    private void disableToggles() {
        tg_fri.setEnabled(false);
        tg_mon.setEnabled(false);
        tg_sat.setEnabled(false);
        tg_sun.setEnabled(false);
        tg_thurs.setEnabled(false);
        tg_tues.setEnabled(false);
        tg_wed.setEnabled(false);
    }

    private void enableToggles() {
        tg_fri.setEnabled(true);
        tg_mon.setEnabled(true);
        tg_sat.setEnabled(true);
        tg_sun.setEnabled(true);
        tg_thurs.setEnabled(true);
        tg_tues.setEnabled(true);
        tg_wed.setEnabled(true);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog DatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (requiredDate == START_DATE) {
                    startDate=calendar.getTime();
                    tv_startDate.setText(toStringDate(startDate));
                }

                else if (requiredDate == END_DATE) {
                    endDate=calendar.getTime();
                    tv_endDate.setText(toStringDate(endDate));
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        DatePicker.show();

    }

    private String toStringDate(Date date){
        SimpleDateFormat DATE_AND_TIME_WITH_SECONDS_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return DATE_AND_TIME_WITH_SECONDS_FORMAT.format(date);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//
//
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//


    @Override
    public void onDestroy() {
        super.onDestroy();
        database.closeRealm();
    }

}
