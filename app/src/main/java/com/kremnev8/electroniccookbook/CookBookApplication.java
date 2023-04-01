package com.kremnev8.electroniccookbook;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CookBookApplication extends Application {

    public static NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
