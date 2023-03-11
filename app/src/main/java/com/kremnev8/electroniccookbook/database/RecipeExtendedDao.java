package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;

import com.kremnev8.electroniccookbook.model.Recipe;

import java.util.List;

public interface RecipeExtendedDao extends RecipeDao {

    public LiveData<List<Recipe>> getRecipesWithData();

    public LiveData<Recipe> getRecipeWithData(int id);

    public void insertWithData(Recipe recipe);
}
