package com.apptimer.apptimer.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.Others.RealmDatabase;
import com.apptimer.apptimer.adapters.InstalledAppsAdapter;

import java.util.ArrayList;
import java.util.List;
import com.apptimer.apptimer.R;

public class AllApplicationsActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private ArrayList<ApplicationInfo> applist;
    private InstalledAppsAdapter listAdaptor;
    private final ArrayList<Boolean>checkedApps=new ArrayList<>();
    private RecyclerView appsRecyclerView;
    private int category;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progress;
    private RealmDatabase databaseAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_applications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.installed_apps);
        if (getIntent().hasExtra("category")) {
            category = getIntent().getIntExtra("category",0);
        }

        appsRecyclerView = findViewById(R.id.all_apps_recycler);
        layoutManager=new LinearLayoutManager(this);
        appsRecyclerView.setHasFixedSize(true);
        packageManager = getPackageManager();

        progress = ProgressDialog.show(AllApplicationsActivity.this, null,
                "Loading application info...");
        new LoadApplications().execute();

    }


    private ArrayList<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

        ArrayList<ApplicationInfo> applist = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {

                Log.d("apps included", info.loadLabel(packageManager).toString() +" "+!databaseAsyncTask.containsApp(info.loadLabel(packageManager).toString()));
                if (null != packageManager.getLaunchIntentForPackage(info.packageName) && !info.packageName.equals(Constants.APP_TIMER_PACKAGE_NAME) && !databaseAsyncTask.containsApp(info.packageName) ) {
                    applist.add(info);
                    checkedApps.add(false);
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
             databaseAsyncTask=new RealmDatabase();

            applist =  checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            Log.d("apps",applist.toString());

            databaseAsyncTask.closeRealm();

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            listAdaptor = new InstalledAppsAdapter(AllApplicationsActivity.this, applist,checkedApps, category);
            appsRecyclerView.setLayoutManager(layoutManager);
            appsRecyclerView.setAdapter(listAdaptor);
            listAdaptor.notifyDataSetChanged();

            progress.dismiss();
            super.onPostExecute(result);
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_apps_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.done){
          appsSelectionFinished();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void appsSelectionFinished() {
        ArrayList<ApplicationInfo> resultCheckedApps=new ArrayList<>();
        for(int i=0;i<checkedApps.size();i++){
            if(checkedApps.get(i)) {
                resultCheckedApps.add(applist.get(i));
            }
        }
        Intent i=new Intent();
        i.putExtra("checkedApps",resultCheckedApps);
        setResult(Constants.ADD_APPS_RESULT_CODE,i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
            appsSelectionFinished();
            super.onBackPressed();

    }
}