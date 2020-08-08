package com.apptimer.apptimer.Others;

import android.util.Log;

import com.apptimer.apptimer.models.AppClass;
import com.apptimer.apptimer.models.Timer;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmDatabase {


    private static RealmDatabase instance;
    private Realm realm;

    public static RealmDatabase getInstance() {
        if (instance == null) {
            instance = new RealmDatabase();
        }
        return instance;
    }


    public RealmDatabase() {
//        Realm.getDefaultConfiguration();
        //OR
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfig);
    }


    public void addAppToCategory(final String packageNAme, final int category){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AppClass app=new AppClass(packageNAme,category,false);
                realm.copyToRealmOrUpdate(app);
            }
        });
    }

    public void removeAppFromCategory(final String packageNAme){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<AppClass> apps = realm.where(AppClass.class)
                        .equalTo("packageName",packageNAme)
                        .findAll();

                apps.deleteAllFromRealm();

            }
        });
    }

    public boolean containsApp(String packageName){
        return realm.where(AppClass.class).equalTo("packageName",packageName).findFirst() != null ;
    }


    public ArrayList<AppClass> getAppsInCategory(final int category){
        final ArrayList<AppClass> result=new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<AppClass> apps = realm.where(AppClass.class)
                        .equalTo("category",category)
                        .findAll();

                result.addAll(realm.copyFromRealm(apps));

            }
        });

        return result;
    }

    public void lockApp(final String packageNAme) {
         realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    AppClass app=realm.where(AppClass.class).equalTo("packageName",packageNAme).findFirst();
                    if(app != null) app.setLocked(true);
                }
            });
        }


    public void lockAppOther(final String packageName) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(new AppClass(packageName,Constants.OTHERS,true));
            }
        });
    }

    public void unLockApp(final String packageNAme) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AppClass app=realm.where(AppClass.class).equalTo("packageName",packageNAme).findFirst();
                if(app != null) app.setLocked(false);
            }
        });
    }

    public void setTimer(final Timer timer) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(timer);
            }
        });
    }

    public Timer getTimer(int category) {
            return realm.where(Timer.class).equalTo("category",category).findFirst() ;

    }

    public void blockAll(final int category) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<AppClass> notificationOrders = realm
                        .where(AppClass.class)
                        .equalTo("category",category)
                        .findAll();
                for(AppClass order : notificationOrders) {
                    order.setLocked(true);
                }
            }
        });
    }

    public void unBblockAll(final int category) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<AppClass> notificationOrders = realm
                            .where(AppClass.class)
                            .equalTo("category",category)
                            .findAll();
                    for(AppClass order : notificationOrders) {
                        order.setLocked(false);
                    }
                }
            });

    }

    public AppClass getApp(String packageName) {
        return realm.where(AppClass.class).like("packageName",packageName).findFirst();
    }

    public void decreaseTimer(final int category, final double remainedTime) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Timer timer=getTimer(category);
                timer.setRemainedTime(remainedTime);
//                Log.d("remainedTime2",timer.getRemainedTime()+"");

            }
        });
    }

    public void resetRemainedTime() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Timer> timers = realm
                        .where(Timer.class)
                        .findAll();
                for(Timer timer : timers) {
                    timer.setRemainedTime(timer.getTotalTime());
                    Log.d("ResetTimer",timer.toString());
                }
            }
        });

    }


    public ArrayList<Timer> getAllTimers() {
        final ArrayList<Timer> result = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Timer> timers = realm
                        .where(Timer.class)
                        .findAll();
                result.addAll(realm.copyFromRealm(timers));
            }
        });
        return result;
    }

    public void closeRealm(){
        if(realm != null)
         realm.close();

    }



}
