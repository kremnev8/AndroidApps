package com.kremnev8.electroniccookbook.recipeview.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.services.ITimerCallback;
import com.kremnev8.electroniccookbook.services.ITimerService;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipeview.itemviewmodel.RecipeViewMainItemViewModel;
import com.kremnev8.electroniccookbook.recipeview.itemviewmodel.RecipeViewStepItemViewModel;
import com.kremnev8.electroniccookbook.recipeview.model.ViewStepCache;
import com.kremnev8.electroniccookbook.services.TimerData;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class RecipeViewModel extends ViewModel implements ITimerCallback {

    public LiveData<Recipe> recipe;

    Handler mainHandler = new Handler(Looper.getMainLooper());
    public final DatabaseExecutor databaseExecutor;
    private final ITimerService timers;
    private final HashMap<Pair<Integer, Integer>, Integer> itemsDict = new HashMap<>();

    protected LiveData<List<ViewStepCache>> stepsData;
    protected MutableLiveData<ArrayList<ItemViewModel>> viewModels = new MutableLiveData<>();

    public LiveData<ArrayList<ItemViewModel>> getViewModels() {
        return viewModels;
    }

    @Inject
    public RecipeViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, ITimerService timers) {
        this.databaseExecutor = databaseExecutor;
        this.timers = timers;
        recipe = new MutableLiveData<>();
    }

    public void setData(Recipe recipeIn) {
        this.recipe = databaseExecutor.getRecipeWithData(recipeIn.id);

        databaseExecutor.getOrCreateRecipeCache(recipeIn)
                .subscribeOn(Schedulers.computation())
                .subscribe((result, throwable) -> mainHandler.post(() -> {
                    stepsData = result;
                    init();
                }));
        timers.listen(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (stepsData != null)
            stepsData.removeObserver(this::updateViewData);
        timers.stopListening(this);
    }

    protected void init() {
        stepsData.observeForever(this::updateViewData);
        var viewModels = createViewData(stepsData);
        this.viewModels.postValue(viewModels);
    }

    public ArrayList<ItemViewModel> createViewData(LiveData<List<ViewStepCache>> data) {
        var dataValue = data.getValue();
        if (dataValue == null) {
            ArrayList<ItemViewModel> itemViewModels = new ArrayList<>(1);
            itemViewModels.add(new RecipeViewMainItemViewModel(recipe));
            return itemViewModels;
        }

        var viewData = new ArrayList<ItemViewModel>(dataValue.size() + 1);
        viewData.add(new RecipeViewMainItemViewModel(recipe));
        for (ViewStepCache item : dataValue) {
            itemsDict.put(item.getFullId(), viewData.size());
            viewData.add(new RecipeViewStepItemViewModel(item, databaseExecutor, timers));
        }
        return viewData;
    }

    public void updateViewData(List<ViewStepCache> newData) {
        var viewModelsList = viewModels.getValue();
        assert viewModelsList != null;


        viewModelsList.ensureCapacity(newData.size() + 1);
        itemsDict.clear();
        for (int i = 0; i < newData.size(); i++) {
            ViewStepCache item = newData.get(i);
            if (i + 1 < viewModelsList.size()) {
                viewModelsList.get(i + 1).setItem(item);
            } else {
                viewModelsList.add(new RecipeViewStepItemViewModel(item, databaseExecutor, timers));
            }
            itemsDict.put(item.getFullId(), i + 1);
        }

        viewModels.setValue(viewModelsList);
    }


    @Override
    public void timerUpdated(TimerData timer) {
        mainHandler.post(() -> timerUpdateInternal(timer));
    }

    @SuppressWarnings("ConstantConditions")
    private void timerUpdateInternal(TimerData timer) {
        var key = Pair.create(timer.recipeId, timer.stepId);
        if (itemsDict.containsKey(key)){
            int index = itemsDict.get(key);
            var item = (RecipeViewStepItemViewModel)viewModels.getValue().get(index);
            item.updateTimer(timer);
        }
    }
}