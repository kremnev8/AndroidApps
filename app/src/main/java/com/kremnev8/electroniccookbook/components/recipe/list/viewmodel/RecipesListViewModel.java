package com.kremnev8.electroniccookbook.components.recipe.list.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.common.recycler.SimpleListViewModel;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditFragment;
import com.kremnev8.electroniccookbook.components.recipe.list.itemviewmodel.RecipeItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipesListViewModel extends SimpleListViewModel<Recipe> {

    @Inject
    RecipesListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        super(handle, databaseExecutor);
        rawData = databaseExecutor.getRecipesWithData();
        init();
    }

    public void editItem(int index) {
        var list = rawData.getValue();
        assert list != null;

        if (index >= 0 && index < list.size()) {
            Bundle args = new Bundle();
            args.putParcelable(RecipeEditFragment.TARGET_RECIPE, list.get(index));
            MainActivity.Instance.setFragment(RecipeEditFragment.class, args);
        }
    }

    public void deleteItem(int index) {
        var list = rawData.getValue();
        assert list != null;

        if (index >= 0 && index < list.size()) {
            databaseExecutor.delete(list.get(index));
        }
    }

    @Override
    public RecipeItemViewModel CreateInstance(Recipe item) {
        return new RecipeItemViewModel(item);
    }

    public String GetFragmentName() {
        return "Recipes";
    }
}