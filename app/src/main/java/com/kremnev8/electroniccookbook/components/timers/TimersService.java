package com.kremnev8.electroniccookbook.components.timers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;

import com.kremnev8.electroniccookbook.CookBookApplication;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.components.recipe.model.ShowRecipeData;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
public class TimersService extends Service implements Runnable, ITimerService {

    private static final String MAIN_CHANNEL_ID = "TimersServiceChannel";
    private static final String TIMER_ELAPSED_CHANNEL_ID = "TimerElapsedChannel";
    private final static String TAG = "TimersService";
    private static final String COUNTDOWN_BR = "com.kremnev8.electroniccookbook";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new TimerBinder();
    private final TimerPool timers = new TimerPool(10);
    private boolean hasInitializedTimers = false;

    private NotificationCompat.Builder builder;
    private Thread updateThread = null;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final StringBuilder stringBuilder = new StringBuilder();
    private PendingIntent pendingIntent;

    private final List<ITimerCallback> callbacks = new ArrayList<>();

    private volatile boolean notificationCreated = false;
    private volatile boolean isRunning = false;

    public class TimerBinder extends Binder {
        public TimersService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return TimersService.this;
        }
    }

    public void startTimer(RecipeStep step) {
        Log.i(TAG, "Starting timer for step " + step.id);
        TimerData timer = timers.addTimer(step);
        timer.start();
        notify(timer);

        CookBookApplication.timerDataStore.updateDataAsync(timerList -> {
            var res = timerList.toBuilder()
                    .addTimers(timer.saveState())
                    .build();
            return Single.just(res);
        }).subscribe();
    }

    public void stopTimer(RecipeStep step) {
        Log.i(TAG, "Stopping timer for step " + step.id);
        notify(TimerData.emptyWith(step.recipe, step.id));
        removeTimer(step.recipe, step.id);
    }

    public void togglePausedTimer(RecipeStep step) {
        Log.i(TAG, "Pausing timer for step " + step.id);
        TimerData timer = timers.get(step.recipe, step.id);
        if (timer == null) return;

        if (timer.isPaused)
            timer.start();
        else
            timer.pause();
        notify(timer);
        CookBookApplication.timerDataStore.updateDataAsync(timerList -> {
            var index = findTimer(step.recipe, step.id, timerList);
            if (!index.isPresent())
                return Single.just(timerList);

            var res = timerList.toBuilder()
                    .setTimers(index.getAsInt(), timer.saveState())
                    .build();
            return Single.just(res);
        }).subscribe();
    }

    @NonNull
    private OptionalInt findTimer(int recipeId, int stepId, TimerList timerList) {
        var list = timerList.getTimersList();
        return IntStream
                .range(0, list.size())
                .filter(i -> {
                    return list.get(i).getRecipeId() == recipeId &&
                            list.get(i).getStepId() == stepId;
                })
                .findFirst();

    }

    private void removeTimer(int recipeId, int stepId){
        timers.removeTimer(recipeId, stepId);
        CookBookApplication.timerDataStore.updateDataAsync(timerList -> {
            var index = findTimer(recipeId, stepId, timerList);
            if (!index.isPresent())
                return Single.just(timerList);

            var res = timerList.toBuilder()
                    .removeTimers(index.getAsInt())
                    .build();
            return Single.just(res);
        }).subscribe();
    }

    public void listen(ITimerCallback callback) {
        if (!callbacks.contains(callback))
            callbacks.add(callback);
    }

    public void stopListening(ITimerCallback callback) {
        callbacks.remove(callback);
    }

    public void timerFinished(TimerData timer) {
        notifyUserTimerEnded(timer);
        notify(timer);
    }

    public void notify(TimerData timer) {
        for (var callback : callbacks) {
            callback.timerUpdated(timer);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (hasInitializedTimers)
            return;

        CookBookApplication.timerDataStore.data()
                .map(TimerList::getTimersList)
                .subscribeOn(Schedulers.computation())
                .subscribe(timerStates -> mainHandler.post(() -> {
                    for (var timerState : timerStates) {
                        timers.addTimer(timerState);
                    }
                }));
        hasInitializedTimers = true;
    }

    @Override
    public void onDestroy() {
        stop();
        timers.free();
        super.onDestroy();
        Log.i(TAG, "Timers service is destroyed!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Timers service is created!");
        /* Notification */
        createNotificationChannel();
        builder = new NotificationCompat.Builder(this, MAIN_CHANNEL_ID);
        /* NotificationBuilder */

        notificationCreated = false;
        Notification notification = updateNotification(null);
        startForeground(NOTIFICATION_ID, notification);

        start();

        return START_STICKY;
    }

    private Notification updateNotification(@Nullable String newMessage) {
        if (!notificationCreated) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            builder.setContentTitle("Cooking Timers")
                    .setContentText("Timers service is active")
                    .setSmallIcon(R.drawable.ic_timer)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true);

            notificationCreated = true;
            return builder.build();
        }

        if (newMessage != null)
            builder.setContentText(newMessage);

        Notification notification = builder.build();
        CookBookApplication.NotificationManager.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void notifyUserTimerEnded(TimerData timer) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notificationId = NotificationID.getID();
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SHOW_RECIPE_EXTRA, new ShowRecipeData(timer.recipeId, timer.stepId));
        intent.putExtra(MainActivity.NOTIFICATION_ID_EXTRA, notificationId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationIntent = PendingIntent.getActivity(this,
                uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        MainActivity.Instance.wakeScreen();
        Notification notification = new NotificationCompat.Builder(this, TIMER_ELAPSED_CHANNEL_ID)
                .setContentTitle("Timer is finished!")
                .setContentText("Timer " + timer.stepName + " is finished!")
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(notificationIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(alarmSound)
                .build();
        notification.flags |= Notification.FLAG_INSISTENT;

        CookBookApplication.NotificationManager.notify(notificationId, notification);
    }

    private void start() {
        isRunning = true;
        updateThread = new Thread(this);
        updateThread.start();
    }

    private void stop() {
        isRunning = false;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        try {
            while (isRunning) {
                update();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Log.w(TAG, e.toString());
        }
    }

    private void update() throws InterruptedException {
        stringBuilder.setLength(0);

        for (int i = 1; i < timers.getCapacity(); i++) {
            TimerData timer = timers.get(i);
            if (timer.getId() == i) {
                boolean isLockAcquired = timer.lock.tryLock(1, TimeUnit.SECONDS);
                if (isLockAcquired) {
                    updateTimer(timer);
                    gatherTimerInfo(stringBuilder, timer);
                    timer.lock.unlock();
                    notify(timer);
                }
            }
        }
    }

    private void updateTimer(TimerData timer) {
        if (!timer.isRunning || timer.isPaused) return;

        final long millisLeft = timer.mStopTimeInFuture - SystemClock.elapsedRealtime();

        if (millisLeft <= 0) {
            timerFinished(timer);
            removeTimer(timer.recipeId, timer.stepId);
        }
    }

    private void gatherTimerInfo(StringBuilder stringBuilder, TimerData timer) {
        if (!timer.isRunning || timer.isPaused) return;

        stringBuilder.append(timer.stepName);
        stringBuilder.append(" timer - ");
        long secondsLeft = (timer.mStopTimeInFuture - SystemClock.elapsedRealtime()) / 1000;
        stringBuilder.append(secondsLeft);
        stringBuilder.append(" seconds\n");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mainChannel = new NotificationChannel(
                    MAIN_CHANNEL_ID,
                    "Cookbook timers",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mainChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            NotificationChannel timerElapsedChannel = new NotificationChannel(
                    TIMER_ELAPSED_CHANNEL_ID,
                    "Cookbook alarms",
                    NotificationManager.IMPORTANCE_HIGH
            );
            timerElapsedChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            timerElapsedChannel.enableVibration(true);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build();
            timerElapsedChannel.setSound(alarmSound, att);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(mainChannel);
            manager.createNotificationChannel(timerElapsedChannel);
        }
    }

}
