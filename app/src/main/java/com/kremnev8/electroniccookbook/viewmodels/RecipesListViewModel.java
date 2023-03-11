package com.kremnev8.electroniccookbook.viewmodels;

import androidx.lifecycle.SavedStateHandle;

import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.itemViewModel.RecipeViewModel;
import com.kremnev8.electroniccookbook.model.Recipe;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecipesListViewModel extends SimpleListViewModel<Recipe, RecipeViewModel> {

    @Inject
    RecipesListViewModel(SavedStateHandle handle, DatabaseExecutor databaseExecutor) {
        super(handle, databaseExecutor);
        rawData = databaseExecutor.getRecipesWithData();
        init();
    }

    @Override
    public RecipeViewModel CreateNewItemViewModel(Recipe item) {
        return new RecipeViewModel(item);
    }

    public String GetFragmentName(){
        return "Recipes";
    }
}