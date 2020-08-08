package com.apptimer.apptimer.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.apptimer.apptimer.Others.RealmDatabase;

import java.util.ArrayList;

import android.content.pm.PackageManager;
import com.apptimer.apptimer.R;

public class InstalledAppsAdapter extends RecyclerView.Adapter<InstalledAppsAdapter.AppsViewHolder> {
    private final ArrayList<Boolean> checkedApps;
    private ArrayList<ApplicationInfo> unCategorizedInstalledApps ;
    private PackageManager packageManager;
    private RealmDatabase database;
    private int category;

    public InstalledAppsAdapter(Context context, ArrayList<ApplicationInfo> appsList, ArrayList<Boolean> checked, int category) {
        this.unCategorizedInstalledApps = appsList;
      //  this.context=context;
        packageManager=context.getPackageManager();
        this.database=new RealmDatabase();
        this.category =category;
        this.checkedApps=checked;
    }

    @Override
    public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_list_item,parent,false);
        return new AppsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AppsViewHolder holder,  int position) {
        final ApplicationInfo appInfo= unCategorizedInstalledApps.get(position);
        holder.applicationName.setText(appInfo.loadLabel(packageManager));
        holder.icon.setImageDrawable(appInfo.loadIcon(packageManager));
        holder.switchView.setChecked(checkedApps.get(position));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.switchView.performClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return unCategorizedInstalledApps == null ? 0: unCategorizedInstalledApps.size();
    }

    public class AppsViewHolder extends RecyclerView.ViewHolder {

        private TextView applicationName;
        CardView cardView;
        ImageView icon;
        Switch switchView;

        AppsViewHolder(View itemView) {
            super(itemView);
            applicationName = itemView.findViewById(R.id.app_name);
            icon = itemView.findViewById(R.id.app_icon);
            switchView = itemView.findViewById(R.id.switch_lock);
            cardView=itemView.findViewById(R.id.apps_cardView);
            switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    checkedApps.set(getAdapterPosition(),checked);
                    if(checked){
                        database.addAppToCategory(unCategorizedInstalledApps.get(getAdapterPosition()).packageName,category);
                    }else{
                        database.removeAppFromCategory(unCategorizedInstalledApps.get(getAdapterPosition()).packageName);

                    }
                }
            });


        }
    }
}
