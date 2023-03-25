package com.kremnev8.electroniccookbook.recipe.viewmodels;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.common.SimpleListViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.recipe.itemviewmodel.RecipeItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipesListViewModel extends SimpleListViewModel<Recipe, RecipeItemViewModel> {

    @Inject
    RecipesListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        super(handle, databaseExecutor);
        rawData = databaseExecutor.getRecipesWithData();
        init();
    }

    @Override
    public RecipeItemViewModel CreateNewItemViewModel(Recipe item) {
        return new RecipeItemViewModel(item);
    }

    public String GetFragmentName(){
        return "Recipes";
    }
}