package com.kremnev8.electroniccookbook;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CookBookApplication extends Application {

    public static NotificationManager NotificationManager;
    public static InputMethodManager InputMethodManager;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        InputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
