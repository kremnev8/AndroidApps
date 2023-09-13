package com.kremnev8.electroniccookbook.components.timers;

import android.os.SystemClock;

import com.kremnev8.electroniccookbook.common.IPoolable;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimerData implements IPoolable {
    private int timerId;

    public long mMillisInFuture;
    public long mStopTimeInFuture;
    public boolean isRunning = false;
    public boolean isPaused = false;

    public int recipeId;
    public int stepId;
    public String stepName;
    public Lock lock = new ReentrantLock();

    private static final TimerData empty = new TimerData();

    public void set(RecipeStep step){
        this.mMillisInFuture = step.timer * 1000 * 60;

        recipeId = step.recipe;
        stepId =  step.id;
        stepName = step.text;
    }

    public void cancel() {
        lock.lock();
        isRunning = false;
        isPaused = false;
        mStopTimeInFuture = 0;
        lock.unlock();
    }

    public void start() {
        lock.lock();
        isRunning = true;
        isPaused = false;
        if (mMillisInFuture <= 0) {
            lock.unlock();
            return;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        lock.unlock();
    }

    public void pause(){
        lock.lock();
        mMillisInFuture = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mStopTimeInFuture = 0;
        isPaused = true;
        lock.unlock();
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

    public TimerList.TimerState saveState(){
        return TimerList.TimerState
                .newBuilder()
                .setMStopTimeInFuture(mStopTimeInFuture)
                .setMMillisInFuture(mMillisInFuture)
                .setRecipeId(recipeId)
                .setStepId(stepId)
                .setIsRunning(isRunning)
                .setIsPaused(isPaused)
                .setStepName(stepName)
                .build();
    }

    public void readState(TimerList.TimerState state){
        lock.lock();
        mStopTimeInFuture = state.getMStopTimeInFuture();
        mMillisInFuture = state.getMMillisInFuture();
        recipeId = state.getRecipeId();
        stepId = state.getStepId();
        isRunning = state.getIsRunning();
        isPaused = state.getIsPaused();
        stepName = state.getStepName();
        lock.unlock();
    }
}
