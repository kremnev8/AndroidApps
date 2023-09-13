package com.kremnev8.electroniccookbook.common;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class MainThreadExecutor implements Executor {
    public static MainThreadExecutor Instance = new MainThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable r) {
        handler.post(r);
    }
}