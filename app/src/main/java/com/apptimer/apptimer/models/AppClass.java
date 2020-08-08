package com.apptimer.apptimer.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppClass extends RealmObject{
    @PrimaryKey
    private String packageName;
    private boolean isLocked;
    private int category;

    public AppClass() {
    }


    public AppClass(String packageNAme,  int category,boolean isSelected) {
        this.packageName = packageNAme;
        this.isLocked = isSelected;
        this.category = category;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "AppClass{" +
                "packageName='" + packageName + '\'' +
                ", isLocked=" + isLocked +
                ", category=" + category +
                '}';
    }
}
