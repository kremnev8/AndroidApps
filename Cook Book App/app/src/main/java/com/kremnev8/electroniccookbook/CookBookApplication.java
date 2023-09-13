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

    @Inject
    HiltWorkerFactory workerFactory;

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
}
