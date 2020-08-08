package com.apptimer.apptimer.utils;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;



public class FragmentUtil {

    public static void addFragment(Context context, Fragment fragment, int layout){

        FragmentManager manager = ((FragmentActivity)context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(layout,fragment);
        transaction.commit();
    }

    public static void replaceFragment(Context context, Fragment fragment, int layout){
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(layout,fragment).commit();
    }

    public static void replaceFragmentWithBackStack(Context context, Fragment fragment, int layout,String name, Bundle bundle){
        fragment.setArguments(bundle);
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(layout,fragment).addToBackStack(name).commit();
    }


//        public static void replaceFragmentWithSlideAnimationWithStack(Context context,Fragment fragment, int layout,String name){
//            FragmentTransaction transaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
//            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
//            transaction.replace(layout, fragment).addToBackStack(name).commit();
//        }
}
