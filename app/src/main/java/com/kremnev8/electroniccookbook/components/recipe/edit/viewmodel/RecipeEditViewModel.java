package com.kremnev8.electroniccookbook.components.recipe.edit.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.BR;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.FooterItemViewModel;
import com.kremnev8.electroniccookbook.common.SimpleViewModel;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModelHolder;
import com.kremnev8.electroniccookbook.components.recipe.edit.itemviewmodel.RecipeEditIngredientItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.edit.itemviewmodel.RecipeEditStepItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;
import com.kremnev8.electroniccookbook.interfaces.IMediaRequestCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipeEditViewModel extends SimpleViewModel<Recipe> implements IMediaRequestCallback {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final IMediaProvider photoProvider;

    protected ItemViewModelHolder<RecipeStep> stepsHolder;
    protected ItemViewModelHolder<RecipeIngredient> ingredientsHolder;

    public LiveData<ArrayList<ItemViewModel>> getSteps(){
        return stepsHolder.getViewModels();
    }

    public LiveData<ArrayList<ItemViewModel>> getIngredients(){
        return ingredientsHolder.getViewModels();
    }

    @Inject
    public RecipeEditViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor, IMediaProvider photoProvider) {
        super(databaseExecutor);
        this.photoProvider = photoProvider;

        stepsHolder = new ItemViewModelHolder<>(item -> new RecipeEditStepItemViewModel(item, photoProvider));
        stepsHolder.setFooter(new FooterItemViewModel(R.string.add_step_label, this::addStep));

        ingredientsHolder = new ItemViewModelHolder<>(RecipeEditIngredientItemViewModel::new);
        ingredientsHolder.setFooter(new FooterItemViewModel(R.string.add_ingredient_label, this::addIngredient));
    }

    @Override
    public void setData(Recipe recipe) {
        super.setData(recipe);
        stepsHolder.updateData(databaseExecutor.getRecipeSteps(recipe.id));
        stepsHolder.getLiveData().observeForever(this::updateOrder);
        ingredientsHolder.updateData(databaseExecutor.getRecipeIngredients(recipe.id));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stepsHolder.getLiveData().removeObserver(this::updateOrder);
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
            databaseExecutor.upsertAllSteps(newData);
        }
    }

    public void takePhotoButtonClicked(View view){
        handler.postDelayed(() -> photoProvider.requestPhoto(this), 100);
    }

    @Bindable
    public int getYield(){
        return model.yield;
    }

    public void setYield(int yield){
        model.yield = Math.max(0, yield);
        notifyPropertyChanged(BR.yield);
    }

    public void onIncreaseYieldClicked(View view){
        setYield(getYield() + 1);
    }

    public void onDecreaseYieldClicked(View view){
        setYield(getYield() - 1);
    }

    @Override
    public void onMediaSelected(String imageUri) {
        model.imageUri = imageUri;
        notifyPropertyChanged(BR.model);
    }

    public void saveData() {
        model.steps = stepsHolder.getLiveData();
        model.ingredients = ingredientsHolder.getLiveData();
        databaseExecutor.insertWithData(model);
    }

    public void removeStep(int index){
        List<RecipeStep> list = stepsHolder.getList();

        if (index >= 0 && index < list.size()){
            saveData();
            databaseExecutor.deleteStep(list.get(index));
        }
    }

    public void removeIngredient(int index){
        List<RecipeIngredient> list = ingredientsHolder.getList();

        if (index >= 0 && index < list.size()){
            saveData();
            databaseExecutor.deleteIngredient(list.get(index));
        }
    }

    public void onStepDrag(int fromPosition, int toPosition) {
        List<RecipeStep> list = stepsHolder.getList();

        toPosition = Math.min(toPosition, list.size() - 1);
        if (fromPosition == toPosition) return;

        saveData();
        var item1 = list.get(fromPosition);
        var item2 = list.get(toPosition);

        int oldNumber = item1.stepNumber;
        item1.stepNumber = item2.stepNumber;
        item2.stepNumber = oldNumber;

        databaseExecutor.upsertTwoSteps(item1, item2);
    }

    public void addStep() {
        saveData();
        RecipeStep step = new RecipeStep();
        step.recipe = model.id;
        step.stepNumber = stepsHolder.getList().size() + 1;
        databaseExecutor.upsertStep(step);
    }

    public void addIngredient() {
        saveData();
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.recipe = model.id;
        databaseExecutor.upsertIngredient(ingredient);
    }
}
