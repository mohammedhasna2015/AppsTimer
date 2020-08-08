package com.apptimer.apptimer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.Others.RealmDatabase;
import com.apptimer.apptimer.models.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    private View view;
    private TextView time_games;
    private TextView remTime_games;
    private TextView time_entertainment;
    private TextView remTime_entertainment;
    private TextView time_social;
    private TextView remTime_social;
    private TextView time_others;
    private TextView remTime_others;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_dashboard, container, false);

//        game_category=view.findViewById(R.id.game_category);
//        entertainment_category=view.findViewById(R.id.entertainment_category);
//        social_category=view.findViewById(R.id.social_category);
//        others_category=view.findViewById(R.id.others_category);







        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        time_games=view.findViewById(R.id.time);
        remTime_games=view.findViewById(R.id.remTime);
        time_entertainment=view.findViewById(R.id.time_entertainment);
        remTime_entertainment=view.findViewById(R.id.remTime_entertainment);
        time_social=view.findViewById(R.id.time_social);
        remTime_social=view.findViewById(R.id.remTime_social);
        time_others=view.findViewById(R.id.time_others);
        remTime_others=view.findViewById(R.id.remTime_others);

        RealmDatabase database=new RealmDatabase();
        Timer gamesTimer=database.getTimer(Constants.GAMES);
        Timer entertainmentTimer=database.getTimer(Constants.ENTERTAINMENT);
        Timer socialTimer=database.getTimer(Constants.SOCIAL);
        Timer othersTimer=database.getTimer(Constants.OTHERS);

        if(gamesTimer != null){
            time_games.setText(gamesTimer.getTotalTime()+" "+getResources().getString(R.string.minutes));
            remTime_games.setText((int)(gamesTimer.getRemainedTime())+" "+getResources().getString(R.string.minutes));
        }

        if(entertainmentTimer != null){
            time_entertainment.setText(entertainmentTimer.getTotalTime()+" "+getResources().getString(R.string.minutes));
            remTime_entertainment.setText((int)(entertainmentTimer.getRemainedTime())+" "+getResources().getString(R.string.minutes));
        }

        if(socialTimer != null){
            time_social.setText(socialTimer.getTotalTime()+" "+getResources().getString(R.string.minutes));
            remTime_social.setText((int)(socialTimer.getRemainedTime())+" "+getResources().getString(R.string.minutes));
        }

        if(othersTimer != null){
            time_others.setText(othersTimer.getTotalTime()+" "+getResources().getString(R.string.minutes));
            remTime_others.setText((int)(othersTimer.getRemainedTime())+" "+getResources().getString(R.string.minutes));
        }


    }
}
