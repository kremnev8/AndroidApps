package com.kremnev8.electroniccookbook.recipeview.viewmodels;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeEndEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeMainEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.recipeview.itemviewmodel.RecipeViewMainItemViewModel;
import com.kremnev8.electroniccookbook.recipeview.itemviewmodel.RecipeViewStepItemViewModel;
import com.kremnev8.electroniccookbook.recipeview.model.ViewStepCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class RecipeViewModel extends ViewModel {

    public LiveData<Recipe> recipe;

    Handler mainHandler = new Handler(Looper.getMainLooper());
    public final DatabaseExecutor databaseExecutor;

    protected LiveData<List<ViewStepCache>> stepsData;
    protected MutableLiveData<ArrayList<ItemViewModel>> viewModels = new MutableLiveData<>();

    public LiveData<ArrayList<ItemViewModel>> getViewModels() {
        return viewModels;
    }

    @Inject
    public RecipeViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        this.databaseExecutor = databaseExecutor;

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
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (stepsData != null)
            stepsData.removeObserver(this::updateViewData);
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
            viewData.add(new RecipeViewStepItemViewModel(item, databaseExecutor));
        }
        return viewData;
    }

    public void updateViewData(List<ViewStepCache> newData) {
        var viewModelsList = viewModels.getValue();
        assert viewModelsList != null;


        viewModelsList.ensureCapacity(newData.size() + 1);
        for (int i = 0; i < newData.size(); i++) {
            if (i + 1 < viewModelsList.size()) {
                viewModelsList.get(i + 1).setItem(newData.get(i));
            } else {
                viewModelsList.add(new RecipeViewStepItemViewModel(newData.get(i), databaseExecutor));
            }
        }

        viewModels.setValue(viewModelsList);
    }

}