package com.kremnev8.electroniccookbook.components.timers;

import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;

public interface ITimerService {
    void startTimer(RecipeStep step);
    void stopTimer(RecipeStep step);
    void togglePausedTimer(RecipeStep step);
    void listen(ITimerCallback callback);
    void stopListening(ITimerCallback callback);
}
