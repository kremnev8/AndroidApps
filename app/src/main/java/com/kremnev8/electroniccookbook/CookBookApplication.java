package com.kremnev8.electroniccookbook;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.kremnev8.electroniccookbook.components.timers.TimerList;
import com.kremnev8.electroniccookbook.components.timers.TimerStateSerializer;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CookBookApplication extends Application implements Configuration.Provider {

    public static NotificationManager NotificationManager;
    public static InputMethodManager InputMethodManager;

    public static RxDataStore<Preferences> dataStore;
    public static RxDataStore<TimerList> timerDataStore;

    public static WorkManager manager;

    @Inject
    HiltWorkerFactory workerFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        InputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        dataStore = new RxPreferenceDataStoreBuilder(getApplicationContext(),"settings").build();
        timerDataStore = new RxDataStoreBuilder<>(getApplicationContext(), "timers.pb", new TimerStateSerializer()).build();

        manager = WorkManager.getInstance(getApplicationContext());
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
}
