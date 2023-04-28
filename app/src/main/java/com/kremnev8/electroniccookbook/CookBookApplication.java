package com.kremnev8.electroniccookbook;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CookBookApplication extends Application {

    public static NotificationManager NotificationManager;
    public static InputMethodManager InputMethodManager;

    public static RxDataStore<Preferences> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        InputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        dataStore = new RxPreferenceDataStoreBuilder(getApplicationContext(),"settings").build();
        
    }
}
