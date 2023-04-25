package com.kremnev8.electroniccookbook.recipeview.itemviewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.recipeview.model.RecipeViewStepCache;
import com.kremnev8.electroniccookbook.services.ITimerService;
import com.kremnev8.electroniccookbook.services.TimerData;

import java.util.Locale;

public class RecipeViewStepItemViewModel extends ItemViewModel {

    public RecipeViewStepCache step;
    private final DatabaseExecutor executor;
    private final ITimerService timers;

    private long currentTimeLeft = -1;
    private boolean isRunning;
    private boolean isPaused;

    public RecipeViewStepItemViewModel(RecipeViewStepCache step, DatabaseExecutor executor, ITimerService timers) {
        this.step = step;
        this.executor = executor;
        this.timers = timers;
    }

    @Bindable
    public boolean getComplete(){
        return step.cache.stepComplete;
    }

    public void toggleComplete(View view){
        step.cache.stepComplete = !step.cache.stepComplete;
        notifyChange();
        executor.update(step.cache);
    }

    public void updateTimer(TimerData timer){
        currentTimeLeft = timer.getTimeLeft();
        isRunning = timer.isRunning;
        isPaused = timer.isPaused;
        notifyChange();
    }

    @Bindable
    public String getTimerText(){
        if (currentTimeLeft > 0){
            return formatTime(currentTimeLeft);
        }else{
            return formatTime(step.step.timer);
        }
    }

    @Bindable
    public boolean getHasMedia(){
        return !Strings.isNullOrEmpty(step.step.mediaUri);
    }

    private String formatTime(long timeLeft){
        int hours = (int) Math.floor(timeLeft / 3600f);
        timeLeft = timeLeft - hours * 3600L;
        int minutes = (int) Math.floor(timeLeft / 60f);
        int seconds = (int)(timeLeft - minutes * 60L);

        if (hours == 0 && minutes == 0)
            return String.format(Locale.ENGLISH,"%02d", seconds);

        if (hours == 0)
            return String.format(Locale.ENGLISH,"%d:%02d", minutes, seconds);

        return String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void startClicked(View view){
        timers.startTimer(step.step);
    }

    public void stopClicked(View view){
        timers.stopTimer(step.step);
    }

    public void pauseClicked(View view){
        timers.togglePausedTimer(step.step);
    }

    @Bindable
    public boolean isRunning() {
        return isRunning;
    }

    @Bindable
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void setItem(Object item) {
        step = (RecipeViewStepCache)item;
    }

    @Override
    public long getItemId() {
        return step.step.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_view_step;
    }

    @Override
    public int getViewType() {
        return 2;
    }
}
