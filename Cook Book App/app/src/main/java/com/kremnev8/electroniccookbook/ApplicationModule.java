package com.kremnev8.electroniccookbook;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.OptIn;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import androidx.work.WorkManager;

import com.kremnev8.electroniccookbook.common.Util;
import com.kremnev8.electroniccookbook.components.timers.TimerList;
import com.kremnev8.electroniccookbook.components.timers.TimerStateSerializer;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
@OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
public class ApplicationModule {

    private Application application;

    public static android.app.NotificationManager NotificationManager;
    public static android.view.inputmethod.InputMethodManager InputMethodManager;

    public static RxDataStore<Preferences> dataStore;
    public static RxDataStore<TimerList> timerDataStore;

    public static WorkManager manager;

    public ApplicationModule() {
    }

    public static void dispose(){
        if (dataStore != null)
            dataStore.dispose();
        if (timerDataStore != null)
            timerDataStore.dispose();
    }

    @Provides
    @Singleton
    public ApplicationModule provideApplicationModule(Application application){
        this.application = application;

        String settingsPath = Util.isRunningTest() ? "settings_test" : "settings";
        String timersPath = Util.isRunningTest() ? "timers_test.pb" : "timers.pb";

        NotificationManager = (android.app.NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        InputMethodManager = (android.view.inputmethod.InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        dataStore = new RxPreferenceDataStoreBuilder(application.getApplicationContext(),settingsPath).build();
        timerDataStore = new RxDataStoreBuilder<>(application.getApplicationContext(), timersPath, new TimerStateSerializer()).build();

        try {
            manager = WorkManager.getInstance(application.getApplicationContext());
        }catch (Exception e){
            // ignored
        }


        return this;
    }

}
