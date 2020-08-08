package com.apptimer.apptimer.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Timer extends RealmObject{

    @PrimaryKey
    private int category;
    private int totalTime;
    private Date startDate;
    private Date endDate;
    private double remainedTime;
    private RealmList<Boolean> days;
    private boolean isDaysSpecified;

    public Timer() {
    }

    public Timer(int category, int totalTime, Date startDate, Date endDate, double remainedTime, RealmList<Boolean> days, boolean isDaysSpecified) {
        this.category = category;
        this.totalTime = totalTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainedTime = remainedTime;
        this.days = days;
        this.isDaysSpecified = isDaysSpecified;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getRemainedTime() {
        return remainedTime;
    }

    public void setRemainedTime(double remainedTime) {
        this.remainedTime = remainedTime;
    }

    public RealmList<Boolean> getDays() {
        return days;
    }

    public void setDays(RealmList<Boolean> days) {
        this.days = days;
    }

    public boolean isDaysSpecified() {
        return isDaysSpecified;
    }

    public void setDaysSpecified(boolean daysSpecified) {
        isDaysSpecified = daysSpecified;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "category=" + category +
                ", totalTime=" + totalTime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", remainedTime=" + remainedTime +
                ", days=" + days +
                ", isDaysSpecified=" + isDaysSpecified +
                '}';
    }
}
