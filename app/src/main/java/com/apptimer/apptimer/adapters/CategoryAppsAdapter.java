package com.apptimer.apptimer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apptimer.apptimer.Others.Constants;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.Others.RealmDatabase;

import java.util.ArrayList;

public class CategoryAppsAdapter extends RecyclerView.Adapter<CategoryAppsAdapter.CategoryAppsViewHolder> {
    private ArrayList<ApplicationInfo> categoryApps;
    private Context context;
    private PackageManager packageManager;
    private RealmDatabase database;
    private int category;
    private boolean allLocked;
    private ArrayList<Boolean> appsLock;
    private OnSetAllCheckedListener listener;

    public CategoryAppsAdapter(Context context, ArrayList<ApplicationInfo> appsList, ArrayList<Boolean> appsLock, int category,RealmDatabase database) {
        this.categoryApps = appsList;
        this.context = context;
        packageManager = context.getPackageManager();
        this.database = database;
        this.category = category;
        this.appsLock = appsLock;

    }

    @Override
    public CategoryAppsAdapter.CategoryAppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.swipe_recycler_category_item, parent, false);
        return new CategoryAppsAdapter.CategoryAppsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CategoryAppsAdapter.CategoryAppsViewHolder holder, final int position) {
        final ApplicationInfo appInfo = categoryApps.get(position);
        holder.applicationName.setText(appInfo.loadLabel(packageManager));
        holder.icon.setImageDrawable(appInfo.loadIcon(packageManager));
        holder.switchView.setChecked(appsLock.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.switchView.performClick();
            }
        });

        if(category == Constants.OTHERS){
            holder.deleteApp.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return categoryApps == null ? 0 : categoryApps.size();
    }

    public boolean noLockedApps() {
        return !appsLock.contains(true);
    }

    public boolean isAllAppsLocked() {
        allLocked = !appsLock.isEmpty() && !appsLock.contains(false);
        return allLocked;
    }

    public void blockAll() {
        for (int i = 0; i < appsLock.size(); i++) {
            appsLock.set(i, true);
        }
        notifyDataSetChanged();
    }

    public void unBlockAll() {
        for (int i = 0; i < appsLock.size(); i++) {
            appsLock.set(i, false);
        }
        notifyDataSetChanged();
    }

    public class CategoryAppsViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private TextView applicationName;
        private CardView cardView;
        private ImageView icon;
        private CheckBox switchView;
        private ImageButton deleteApp;

        CategoryAppsViewHolder(View itemView) {
            super(itemView);
            this.mView=itemView;
            applicationName = itemView.findViewById(R.id.app_name);
            icon = itemView.findViewById(R.id.app_icon);
            switchView = itemView.findViewById(R.id.switch_lock);
            cardView = itemView.findViewById(R.id.apps_cardView);
            deleteApp=itemView.findViewById(R.id.btn_delete_app);

            switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        appsLock.set(getAdapterPosition(), true);
                        if(category == Constants.OTHERS){
                            database.lockAppOther(categoryApps.get(getAdapterPosition()).packageName);
                        }else {
                            database.lockApp(categoryApps.get(getAdapterPosition()).packageName);
                        }
                        if(isAllAppsLocked() && listener != null){
                            listener.setAllChecked(true);

                        }

                    } else {
                        if(category == Constants.OTHERS){
                            database.removeAppFromCategory(categoryApps.get(getAdapterPosition()).packageName);
                        }else {
                            database.unLockApp(categoryApps.get(getAdapterPosition()).packageName);
                        }
                        if (isAllAppsLocked() && listener != null) {
                            listener.setAllChecked(false);
                        }
                        appsLock.set(getAdapterPosition(), false);
                    }
                }
            });



            deleteApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.confirm_remove_app)

                                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        database.removeAppFromCategory(categoryApps.get(getAdapterPosition()).packageName);
                                        categoryApps.remove(getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.show();
                    }

            });
        }


    }

    public interface OnSetAllCheckedListener{
        void setAllChecked(boolean checked);
    }
    public void setOnAllCheckedListener(OnSetAllCheckedListener listener){
        this.listener=listener;
    }


}