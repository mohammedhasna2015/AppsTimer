package com.apptimer.apptimer.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.activities.SplashActivity;
import com.apptimer.apptimer.Others.MySettingPreferences;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

      private LinearLayout linearLayoutchangelanguge;

    private View rootView;
    private MySettingPreferences pref;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView= inflater.inflate(R.layout.fragment_settings, container, false);
         return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        pref = MyApplication.getPref();


        TextView selectedLang =rootView.findViewById(R.id.tv_lang);
        selectedLang.setText(getResources().getStringArray(R.array.lang_list)[pref.getLangIndex()]);
        selectedLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLanguageDialog();
            }
        });

        TextView changeNum=rootView.findViewById(R.id.tv_security_num);
        changeNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSecurityNumDialog();
            }
        });

        CheckBox blockSettings=rootView.findViewById(R.id.ch_block_device_settings);
        blockSettings.setChecked(pref.isBlockedSettings());
        blockSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                pref.setBlockedSettings(checked);
            }
        });



        TextView rate=rootView.findViewById(R.id.tv_rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }
        });


        TextView privacy=rootView.findViewById(R.id.tv_privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse("http://aqarati.zennoo.com/privacy_policy.html"));
                startActivity(i);
            }
        });
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }
    }

    private void changeSecurityNumDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.change_password_dialog);
//        dialog.setTitle(getResources().getString(R.string.change_security_num));
        dialog.setCancelable(false);

        final EditText currentNum = dialog.findViewById(R.id.current_num);
        final EditText newNum = dialog.findViewById(R.id.new_num);
        final EditText confirmNewNum = dialog.findViewById(R.id.confirm_new_num);

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentNumber = currentNum.getText().toString().trim();
                String newNumber = newNum.getText().toString().trim();
                String confirmNumber = confirmNewNum.getText().toString().trim();

                if (TextUtils.isEmpty(currentNum.getText())) {
                    currentNum.setError(getResources().getString(R.string.empty_error));
                    return;
                }
                if (TextUtils.isEmpty(newNum.getText())) {
                    newNum.setError(getResources().getString(R.string.empty_error));
                    return;
                }
                if (TextUtils.isEmpty(confirmNewNum.getText())) {
                    confirmNewNum.setError(getResources().getString(R.string.empty_error));
                    return;
                }

                  if(currentNumber.equals(pref.getSecurityNum())){
                    if (newNumber.equals(confirmNumber)) {
                        pref.setSecurityNum(newNumber);
                        dialog.dismiss();
                    }
                    else{
                        newNum.setError(getResources().getString(R.string.no_match));
                        confirmNewNum.setError(getResources().getString(R.string.no_match));

                    }
                }
                else{
                    currentNum.setError(getResources().getString(R.string.wrong_num));
                  }
            }
        });

        dialog.show();
    }

    private void selectLanguageDialog() {
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_language);
        builder.setSingleChoiceItems(R.array.lang_list,pref.getLangIndex(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(pref.getLangIndex() == i) return;

                pref.setLang(i);
                pref.setDestroyedFromLangChange(true);
                switch(i){
                    case Constants.ARABIC:
                      setLocale("ar");
                    case Constants.ENGLISH:
                      setLocale("en");
                }
                startActivity(new Intent(getActivity(), SplashActivity.class));
                getActivity().finish();


            }
        }).show();
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
