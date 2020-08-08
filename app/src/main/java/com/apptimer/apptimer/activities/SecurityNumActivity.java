package com.apptimer.apptimer.activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.R;

public class SecurityNumActivity extends AppCompatActivity {

    private PinLockView mPinLockView ;
    private IndicatorDots mIndicatorDots ;
    private String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_num);

        extra = getIntent().getStringExtra("extra");
        setupview();
    }

    private void setupview() {
        mPinLockView = findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

    }

    private String TAG ="pin_complet";
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            checkEnteredNum(pin);


        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

//            if (pinLength==4){
//
//            }
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };


    private void checkEnteredNum(String num) {
        if(num.equals(MyApplication.getPref().getSecurityNum())){
            if(extra != null && extra.equals(Constants.BLOCK_SETTINGS)){
                MyApplication.getPref().setBlockedSettings(false);
            }else if(extra != null && extra.equals(Constants.BLOCK_GOOGLE_PLAY)){
                MyApplication.getPref().setBlockGooglePlay(false);
            }
            MyApplication.getPref().setSecurityNumActivityStarted(false);
            finish();
        }
        else{
            Toast.makeText(this, R.string.wrong_num, Toast.LENGTH_SHORT).show();
        }
    }


//    @Override
//    public void onBackPressed() {
//        finish();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getPref().setSecurityNumActivityStarted(false);
        Log.d("SecurityNum: onDestroy",MyApplication.getPref().isSecurityNumActivityStarted()+"");
    }
}





