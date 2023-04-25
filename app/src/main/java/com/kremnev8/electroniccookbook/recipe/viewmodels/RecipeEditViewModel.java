package com.kremnev8.electroniccookbook.recipe.viewmodels;

import android.os.Handler;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.common.ItemViewModelHolder;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.interfaces.IPhotoRequestCallback;
import com.kremnev8.electroniccookbook.common.FooterItemViewModel;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeEditIngredientItemViewModel;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeEditItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipeEditViewModel extends ViewModel implements IPhotoRequestCallback {

    private final Handler handler = new Handler();
    private final IPhotoProvider photoProvider;

    private final MutableLiveData<Recipe> recipe;

    public final DatabaseExecutor databaseExecutor;
    protected ItemViewModelHolder<RecipeStep> stepsHolder;
    protected ItemViewModelHolder<RecipeIngredient> ingredientsHolder;

    public LiveData<ArrayList<ItemViewModel>> getSteps(){
        return stepsHolder.getViewModels();
    }

    public LiveData<ArrayList<ItemViewModel>> getIngredients(){
        return ingredientsHolder.getViewModels();
    }

    public LiveData<Recipe> getRecipe(){
        return recipe;
    }

    @Inject
    public RecipeEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IPhotoProvider photoProvider) {
        this.photoProvider = photoProvider;
        this.databaseExecutor = databaseExecutor;

        recipe = new MutableLiveData<>();
        stepsHolder = new ItemViewModelHolder<>(item -> new RecipeEditItemViewModel(item, photoProvider));
        stepsHolder.setFooter(new FooterItemViewModel(R.string.addStepDesc, this::addStep));

        ingredientsHolder = new ItemViewModelHolder<>(RecipeEditIngredientItemViewModel::new);
        ingredientsHolder.setFooter(new FooterItemViewModel(R.string.AddIngredientText, this::addIngredient));
    }

    public void setData(Recipe recipe) {
        this.recipe.setValue(recipe);
        stepsHolder.init(databaseExecutor.getRecipeSteps(recipe.id));
        stepsHolder.getData().observeForever(this::updateOrder);
        ingredientsHolder.init(databaseExecutor.getRecipeIngredients(recipe.id));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stepsHolder.getData().removeObserver(this::updateOrder);
        stepsHolder.onCleared();
        ingredientsHolder.onCleared();
    }

    public void updateOrder(List<RecipeStep> newData){
        boolean allInOrder = true;

        for (int i = 0; i < newData.size(); i++) {
            if (newData.get(i).stepNumber != i + 1){
                newData.get(i).stepNumber = i + 1;
                allInOrder = false;
            }
        }

        if (!allInOrder){
            databaseExecutor.updateAllSteps(newData);
        }
    }

    public void takePhotoButtonClicked(View view){
        handler.postDelayed(() -> photoProvider.requestPhoto(this), 100);
    }

    public void onIncreaseYieldClicked(View view){
        Recipe recipeValue = recipe.getValue();
        assert recipeValue != null;
        recipeValue.yield += 1;
        recipe.setValue(recipeValue);
    }

    public void onDecreaseYieldClicked(View view){
        Recipe recipeValue = recipe.getValue();
        assert recipeValue != null;
        recipeValue.yield -= 1;
        recipe.setValue(recipeValue);
    }

    @Override
    public void onPhotoSelected(String imageUri) {
        Recipe recipeValue = recipe.getValue();
        assert recipeValue != null;

        recipeValue.imageUri = imageUri;
        recipe.setValue(recipeValue);
    }

    public void saveData() {
        Recipe recipeValue = recipe.getValue();
        assert recipeValue != null;

        recipeValue.steps = stepsHolder.getData();
        recipeValue.ingredients = ingredientsHolder.getData();
        databaseExecutor.insertWithData(recipeValue);
    }

    public void addStep() {
        saveData();
        RecipeStep step = new RecipeStep();
        step.recipe = Objects.requireNonNull(recipe.getValue()).id;
        databaseExecutor.insertStep(step);
    }

    public void addIngredient() {
        saveData();
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.recipe = Objects.requireNonNull(recipe.getValue()).id;
        databaseExecutor.insertIngredient(ingredient);
    }
}
