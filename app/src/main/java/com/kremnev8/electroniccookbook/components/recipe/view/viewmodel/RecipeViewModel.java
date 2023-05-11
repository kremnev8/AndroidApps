package com.kremnev8.electroniccookbook.components.recipe.view.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModelHolder;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewStepCache;
import com.kremnev8.electroniccookbook.components.recipe.view.itemviewmodel.RecipeViewIngredientItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.view.itemviewmodel.RecipeViewStepItemViewModel;
import com.kremnev8.electroniccookbook.components.timers.ITimerCallback;
import com.kremnev8.electroniccookbook.components.timers.ITimerService;
import com.kremnev8.electroniccookbook.components.timers.TimerData;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class RecipeViewModel extends ViewModel implements ITimerCallback {

    public LiveData<Recipe> recipe;

    Handler mainHandler = new Handler(Looper.getMainLooper());
    public final DatabaseExecutor databaseExecutor;
    private final ITimerService timers;

    protected ItemViewModelHolder<RecipeViewStepCache> stepsModelsHolder;
    protected ItemViewModelHolder<RecipeViewIngredientCache> ingredientsModelsHolder;

    public LiveData<ArrayList<ItemViewModel>> getSteps() {
        return stepsModelsHolder.getViewModels();
    }

    public LiveData<ArrayList<ItemViewModel>> getIngredients() {
        return ingredientsModelsHolder.getViewModels();
    }

    @Inject
    public RecipeViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, ITimerService timers) {
        this.databaseExecutor = databaseExecutor;
        this.timers = timers;
        recipe = new MutableLiveData<>();
        stepsModelsHolder = new ItemViewModelHolder<>(item -> new RecipeViewStepItemViewModel(item, databaseExecutor, timers));
        ingredientsModelsHolder = new ItemViewModelHolder<>(item -> new RecipeViewIngredientItemViewModel(item, databaseExecutor));
    }

    public void setData(int recipeId) {
        this.recipe = databaseExecutor.getRecipeWithData(recipeId);

        databaseExecutor.getOrCreateRecipeCache(recipeId)
                .subscribeOn(Schedulers.computation())
                .subscribe((result, throwable) -> mainHandler.post(() -> stepsModelsHolder.updateData(result)));

        databaseExecutor.getOrCreateIngredientCache(recipeId)
                .subscribeOn(Schedulers.computation())
                .subscribe((result, throwable) -> mainHandler.post(() -> ingredientsModelsHolder.updateData(result)));
        timers.listen(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stepsModelsHolder.onCleared();
        ingredientsModelsHolder.onCleared();
        timers.stopListening(this);
    }

    @Override
    public void timerUpdated(TimerData timer) {
        mainHandler.post(() -> timerUpdateInternal(timer));
    }

    private void timerUpdateInternal(TimerData timer) {
        LiveData<ArrayList<ItemViewModel>> viewModels = stepsModelsHolder.getViewModels();
        if (viewModels.getValue() == null) return;

        for (ItemViewModel model : viewModels.getValue()) {
            RecipeViewStepItemViewModel item = (RecipeViewStepItemViewModel)model;
            RecipeStep step = item.step.step;
            if (step.recipe == timer.recipeId &&
                    step.id == timer.stepId) {
                item.updateTimer(timer);
                break;
            }
        }
    }
}