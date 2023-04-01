package com.kremnev8.electroniccookbook.services;

import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

public interface ITimerService {
    void startTimer(RecipeStep step);
    void stopTimer(RecipeStep step);
    void togglePausedTimer(RecipeStep step);
    void listen(ITimerCallback callback);
    void stopListening(ITimerCallback callback);
}
