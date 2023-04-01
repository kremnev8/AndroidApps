package com.kremnev8.electroniccookbook.services;

import android.os.SystemClock;

import com.kremnev8.electroniccookbook.common.IPoolable;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

import org.checkerframework.checker.units.qual.s;

public class TimerData implements IPoolable {
    private int timerId;

    public long mMillisInFuture;
    public long mStopTimeInFuture;
    public boolean isRunning = false;
    public boolean isPaused = false;

    public int recipeId;
    public int stepId;
    public String stepName;

    private static TimerData empty = new TimerData();

    public void set(RecipeStep step){
        this.mMillisInFuture = step.timer * 1000;

        recipeId = step.recipe;
        stepId =  step.id;
        stepName = step.name;
    }

    public void cancel() {
        isRunning = false;
        isPaused = false;
        mStopTimeInFuture = 0;
    }

    public void start() {
        isRunning = true;
        isPaused = false;
        if (mMillisInFuture <= 0) {
            return;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
    }

    public void pause(){
        mMillisInFuture = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mStopTimeInFuture = 0;
        isPaused = true;
    }

    public long getTimeLeft(){
        if (isPaused) return mMillisInFuture / 1000;

        return (mStopTimeInFuture - SystemClock.elapsedRealtime()) / 1000;
    }


    public static TimerData emptyWith(int recipeId, int stepId){
        empty.recipeId = recipeId;
        empty.stepId = stepId;
        return empty;
    }

    @Override
    public int getId() {
        return timerId;
    }

    @Override
    public void setId(int id) {
        this.timerId = id;
    }

    @Override
    public void free() {
        mMillisInFuture = 0;
        mStopTimeInFuture = 0;
        isRunning = false;
    }
}
