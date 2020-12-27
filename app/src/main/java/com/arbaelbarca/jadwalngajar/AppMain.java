package com.arbaelbarca.jadwalngajar;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

public class AppMain extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        reminderNotifEvent();
    }

    private void reminderNotifEvent() {
        Hawk.init(this)
//                .setEncryption(HawkBuilder.EncryptiognfMethod.MEDIUM)
//                .setStorage(HawkBuilder.newSharedPrefStorage(this))
//                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }
}
