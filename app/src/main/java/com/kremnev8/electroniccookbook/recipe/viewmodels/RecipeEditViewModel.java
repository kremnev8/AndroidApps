package com.kremnev8.electroniccookbook.recipe.viewmodels;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeEndEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeMainEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipeEditViewModel extends ViewModel implements IPhotoRequestCallback, IRecipePhotoAccess, IAddStepCallback {

    private final Handler handler = new Handler();
    private final IPhotoProvider photoProvider;

    private MutableLiveData<Recipe> recipe;

    public final DatabaseExecutor databaseExecutor;
    protected LiveData<List<RecipeStep>> rawData;
    protected MutableLiveData<ArrayList<ItemViewModel>> viewModels = new MutableLiveData<>();

    public LiveData<ArrayList<ItemViewModel>> getViewModels(){
        return viewModels;
    }
    public LiveData<Recipe> getRecipe(){
        return recipe;
    }

    @Inject
    public RecipeEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IPhotoProvider photoProvider) {
        this.photoProvider = photoProvider;
        this.databaseExecutor = databaseExecutor;

        recipe = new MutableLiveData<>();

    }

    public void setData(Recipe recipe) {
        this.recipe.setValue(recipe);
        rawData = databaseExecutor.getRecipeSteps(recipe.id);
        init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        rawData.removeObserver(this::updateViewData);
    }

    protected void init() {
        rawData.observeForever(this::updateViewData);
        var viewModels = createViewData(rawData);
        this.viewModels.postValue(viewModels);
    }

    public ArrayList<ItemViewModel> createViewData(LiveData<List<RecipeStep>> data) {
        var dataValue = data.getValue();
        if (dataValue == null) {
            ArrayList<ItemViewModel> itemViewModels = new ArrayList<>(2);
            itemViewModels.add(new RecipeMainEditItemViewModel(recipe, this));
            itemViewModels.add(new RecipeEndEditItemViewModel(this));
            return itemViewModels;
        }

        var viewData = new ArrayList<ItemViewModel>(dataValue.size() + 2);
        viewData.add(new RecipeMainEditItemViewModel(recipe, this));
        for (RecipeStep item : dataValue) {
            viewData.add(new RecipeEditItemViewModel(item));
        }
        viewData.add(new RecipeEndEditItemViewModel(this));
        return viewData;
    }

    public void updateViewData(List<RecipeStep> newData) {
        var viewModelsList = viewModels.getValue();
        assert viewModelsList != null;

        ItemViewModel endItem = viewModelsList.remove(viewModelsList.size() - 1);

        viewModelsList.ensureCapacity(newData.size() + 2);
        for (int i = 0; i < newData.size(); i++) {
            if (i + 1 < viewModelsList.size()) {
                viewModelsList.get(i + 1).setItem(newData.get(i));
            } else {
                viewModelsList.add(new RecipeEditItemViewModel(newData.get(i)));
            }
        }
        viewModelsList.add(endItem);

        viewModels.setValue(viewModelsList);
    }

    public void selectIconClicked() {
        handler.postDelayed(() -> photoProvider.requestPhoto(this), 100);
    }

    public void takePhotoClicked() {
        handler.postDelayed(() -> photoProvider.takePicture(this), 100);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        Recipe recipeValue = recipe.getValue();
        assert recipeValue != null;

        recipeValue.imageUri = imageUri;
        recipe.postValue(recipeValue);
    }

    public void saveData() {
        Recipe recipeValue = recipe.getValue();
        assert recipeValue != null;

        recipeValue.steps = rawData;
        databaseExecutor.insertWithData(recipeValue);
    }

    @Override
    public void addStep() {
        RecipeStep step = new RecipeStep();
        step.recipe = Objects.requireNonNull(recipe.getValue()).id;
        databaseExecutor.insertStep(step);
    }
}
