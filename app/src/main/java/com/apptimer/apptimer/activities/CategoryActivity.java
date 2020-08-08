package com.apptimer.apptimer.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.apptimer.apptimer.fragments.CategoryAppsFragment;
import com.apptimer.apptimer.utils.FragmentUtil;
import com.apptimer.apptimer.R;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        RealmDatabase database=RealmDatabase.getInstance();

        if( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int category=getIntent().getIntExtra("category",0);

//        Log.d("category apps", "category "+ category+" apps "+database.getAppsInCategory(category).toString());
        toolbar.setTitle(getResources().getStringArray(R.array.categories)[category]);



        if(savedInstanceState == null){
            FragmentUtil.addFragment(this, CategoryAppsFragment.newInstance(category),R.id.frame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }

    @Override
    public void onBackPressed() {
        Log.d("count stack", getSupportFragmentManager().getBackStackEntryCount()+"");
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.changes_discarded)

                    .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           getSupportFragmentManager().popBackStack();

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();



        }else{
            super.onBackPressed();

        }
    }
}
