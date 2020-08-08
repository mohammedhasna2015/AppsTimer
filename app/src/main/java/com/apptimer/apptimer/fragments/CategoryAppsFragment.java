package com.apptimer.apptimer.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.Others.RealmDatabase;
import com.apptimer.apptimer.activities.AllApplicationsActivity;
import com.apptimer.apptimer.adapters.CategoryAppsAdapter;
import com.apptimer.apptimer.models.AppClass;
import com.apptimer.apptimer.utils.FragmentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryAppsFragment extends Fragment implements CategoryAppsAdapter.OnSetAllCheckedListener {


    private ArrayList<ApplicationInfo> appInfoList;
    private PackageManager packageManager;
    private RealmDatabase database;
    private ArrayList<AppClass> databaseList;
    private CategoryAppsAdapter adapter;
    private final ArrayList<Boolean> appsLock=new ArrayList<>();
    private CheckBox blockAll;
    private RecyclerView recyclerView;
    private int category;
    private ProgressDialog progress ;
    private RealmDatabase databaseAsyncTask;

    public CategoryAppsFragment() {
        // Required empty public constructor
    }

    public static CategoryAppsFragment newInstance(int category) {

        Bundle args = new Bundle();
        args.putInt("category",category);
        CategoryAppsFragment fragment = new CategoryAppsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category=getArguments().getInt("category");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_category_apps, container, false);
        packageManager=getActivity().getPackageManager();
        database= new RealmDatabase();

        Log.d("onCreate","onCreate");

        final Button setTimer=v.findViewById(R.id.btn_set_timer);
        setTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("noLockedApps",adapter.noLockedApps()+"");
                if(adapter.noLockedApps()){
                    Snackbar.make(setTimer,R.string.no_locked_apps,Snackbar.LENGTH_LONG).show();
                }else {
                    Bundle bundle=new Bundle();
                    bundle.putInt("category",category);
                    FragmentUtil.replaceFragmentWithBackStack(getActivity(), new SetTimerFragment(), R.id.frame, "category" ,bundle);
                }
            }
        });

        LinearLayout addAppsLinear=v.findViewById(R.id.add_apps_linear);
        addAppsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AllApplicationsActivity.class);
                intent.putExtra("category",category);
                startActivityForResult(intent, Constants.ADD_APPS_REQUEST_CODE);
            }
        });

        blockAll =v.findViewById(R.id.ch_block_all);
        blockAll.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(blockAll.isChecked()){
                    //update UI and database
                    adapter.blockAll();
                    if(category == Constants.OTHERS) {
                        for(ApplicationInfo info:appInfoList) {
                            database.lockAppOther(info.packageName);
                        }
                    }else {
                        database.blockAll(category);
                    }
                }else{
                    adapter.unBlockAll();
                    if(category == Constants.OTHERS){
                        for(ApplicationInfo info:appInfoList) {
                            database.removeAppFromCategory(info.packageName);
                        }
                    }else {
                        database.unBblockAll(category);
                    }
                }
            }


        });
        recyclerView = v.findViewById(R.id.category_apps_recycler);

        if(appInfoList == null) { // to prevent loading apps each time the view is created if it is already loaded

            appInfoList=new ArrayList<>();
            if(category != Constants.OTHERS) {
                addAppsLinear.setVisibility(View.VISIBLE);
                getAppsInCategory(category);

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new CategoryAppsAdapter(getActivity(), appInfoList, appsLock, category,database);
                adapter.setOnAllCheckedListener(CategoryAppsFragment.this);
                blockAll.setChecked(adapter.isAllAppsLocked());
                recyclerView.setAdapter(adapter);
            }else {
                // Others
                addAppsLinear.setVisibility(View.GONE);
                getOtherApps();


            }
        }
        else{
            if(category == Constants.OTHERS){
                addAppsLinear.setVisibility(View.GONE);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new CategoryAppsAdapter(getActivity(), appInfoList, appsLock, category,database);
            adapter.setOnAllCheckedListener(CategoryAppsFragment.this);
            blockAll.setChecked(adapter.isAllAppsLocked());
            recyclerView.setAdapter(adapter);

        }


        return v;
    }

    private void getOtherApps(){
        progress = ProgressDialog.show(getActivity(), null,
        "Loading application info...");
        new LoadApplications().execute();
    }

    private void getAppsInCategory(int category) {
        databaseList=database.getAppsInCategory(category);
        Log.d("databaseList",databaseList.toString());
        for(AppClass appClass: databaseList) {
            try {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(appClass.getPackageName(), 0);
                appInfoList.add(appInfo);
                appsLock.add(appClass.isLocked());

            } catch (PackageManager.NameNotFoundException e) {
                Toast toast = Toast.makeText(getActivity(), "error in getting icon", Toast.LENGTH_SHORT);
                toast.show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==Constants.ADD_APPS_REQUEST_CODE && resultCode == Constants.ADD_APPS_RESULT_CODE && data != null){
            ArrayList<ApplicationInfo> result=data.getParcelableArrayListExtra("checkedApps");
            appInfoList.addAll(result);
            Log.d("appInfoList",appInfoList.toString());
            for(int i=0; i<result.size();i++){
                appsLock.add(false);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       if(category != Constants.OTHERS)
            blockAll.setChecked(adapter.isAllAppsLocked());

    }

    @Override
    public void setAllChecked(boolean checked) {
        blockAll.setChecked(checked);
    }

    /////////////////////////// Other Apps ///////////////////////
    private ArrayList<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

        ArrayList<ApplicationInfo> applist = new ArrayList<>();
        AppClass app;

        for (ApplicationInfo info : list) {
            try {

                Log.d("apps included", info.loadLabel(packageManager).toString() +" "+!databaseAsyncTask.containsApp(info.loadLabel(packageManager).toString()));
                if (null != packageManager.getLaunchIntentForPackage(info.packageName) && !info.packageName.equals(Constants.APP_TIMER_PACKAGE_NAME)) {
                    if((app =databaseAsyncTask.getApp(info.packageName)) == null ){
                        // not added to any category and not locked in the "others"
                        applist.add(info);
                        appsLock.add(false);
                    } else if(app.getCategory() == Constants.OTHERS){
                        //not added to any category and locked in the "others"
                        applist.add(info);
                        appsLock.add(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            databaseAsyncTask= new RealmDatabase();

            appInfoList =  checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            Log.d("apps",appInfoList.toString());
            databaseAsyncTask.closeRealm();

            adapter = new CategoryAppsAdapter(getActivity(), appInfoList,appsLock, category,database);
            adapter.setOnAllCheckedListener(CategoryAppsFragment.this);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
//            database.closeRealm();
//            database= new RealmDatabase();

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            blockAll.setChecked(adapter.isAllAppsLocked());


            super.onPostExecute(result);
        }


        @Override
        protected void onPreExecute() {
//            progress = ProgressDialog.show(getActivity(), null,
//                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.closeRealm();
    }
}

