package com.kremnev8.electroniccookbook.services;

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
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.kremnev8.electroniccookbook.CookBookApplication;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.recipeview.model.ShowRecipeData;

import java.util.ArrayList;
import java.util.List;

public class TimersService extends Service implements Runnable, ITimerService {

    private static final String MAIN_CHANNEL_ID = "TimersServiceChannel";
    private static final String TIMER_ELAPSED_CHANNEL_ID = "TimerElapsedChannel";
    private final static String TAG = "TimersService";
    private static final String COUNTDOWN_BR = "com.kremnev8.electroniccookbook";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new TimerBinder();
    private final TimerPool timers = new TimerPool(10);
    private NotificationCompat.Builder builder;
    private Thread updateThread = null;
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
    }

    public void stopTimer(RecipeStep step){
        Log.i(TAG, "Stopping timer for step " + step.id);
        timers.removeTimer(step.recipe, step.id);

        notify(TimerData.emptyWith(step.recipe, step.id));
    }

    public void togglePausedTimer(RecipeStep step){
        Log.i(TAG, "Pausing timer for step " + step.id);
        TimerData timer = timers.get(step.recipe, step.id);
        if (timer == null) return;

        if (timer.isPaused)
            timer.start();
        else
            timer.pause();
        notify(timer);
    }

    public void listen(ITimerCallback callback){
        if (!callbacks.contains(callback))
            callbacks.add(callback);
    }

    public void stopListening(ITimerCallback callback){
        callbacks.remove(callback);
    }

    public void timerFinished(TimerData timer) {
        notifyUserTimerEnded(timer);
        notify(timer);
    }

    public void notify(TimerData timer){
        for (var callback : callbacks){
            callback.timerUpdated(timer);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
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
                    .setContentText("No timers running.")
                    .setSmallIcon(R.drawable.ic_timer)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true);

            notificationCreated = true;
            return builder.build();
        }

        if (newMessage != null)
            builder.setContentText(newMessage);

        Notification notification = builder.build();
        CookBookApplication.mNotificationManager.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void notifyUserTimerEnded(TimerData timer){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notificationId = NotificationID.getID();
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SHOW_RECIPE_EXTRA, new ShowRecipeData(timer.recipeId, timer.stepId));
        intent.putExtra(MainActivity.NOTIFICATION_ID_EXTRA, notificationId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
         PendingIntent notificationIntent = PendingIntent.getActivity(this,
                 uniqueInt, intent,  PendingIntent.FLAG_UPDATE_CURRENT);

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

        CookBookApplication.mNotificationManager.notify(notificationId, notification);
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

    private void update() {
        stringBuilder.setLength(0);
        boolean anyTimersRunning = false;

        for (int i = 0; i < timers.getCapacity(); i++) {
            TimerData timer = timers.get(i);
            if (timer.getId() == i) {
                updateTimer(timer);
                gatherTimerInfo(stringBuilder, timer);
                anyTimersRunning = true;
                notify(timer);
            }
        }

        if (!anyTimersRunning){
            updateNotification("No timers running.");
        }else{
            updateNotification(stringBuilder.toString());
        }
    }

    private void updateTimer(TimerData timer) {
        if (!timer.isRunning || timer.isPaused) return;

        final long millisLeft = timer.mStopTimeInFuture - SystemClock.elapsedRealtime();

        if (millisLeft <= 0) {
            timerFinished(timer);
            timers.removeTimer(timer.getId());
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
