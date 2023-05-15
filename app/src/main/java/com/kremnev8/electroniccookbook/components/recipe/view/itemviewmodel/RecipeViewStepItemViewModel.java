package com.kremnev8.electroniccookbook.components.recipe.view.itemviewmodel;

import android.net.Uri;
import android.view.View;

import androidx.databinding.Bindable;

import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.IHasYouTubePlayer;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewStepCache;
import com.kremnev8.electroniccookbook.components.timers.ITimerService;
import com.kremnev8.electroniccookbook.components.timers.TimerData;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeViewStepItemViewModel extends ItemViewModel implements IHasYouTubePlayer {

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
        executor.upsert(step.cache);
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

    public boolean getIsVideo(){
        if (Strings.isNullOrEmpty(step.step.mediaUri)) return false;
        String videoId = getYouTubeVideoID();

        return !Strings.isNullOrEmpty(videoId);
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

    @Override
    public int getYouTubePlayerID() {
        return R.id.youtubeView;
    }

    @Override
    public String getYouTubeVideoID() {
        if (Strings.isNullOrEmpty(step.step.mediaUri)) return "";
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(step.step.mediaUri);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }
}
