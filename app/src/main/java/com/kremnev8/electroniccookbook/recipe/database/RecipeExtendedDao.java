package com.kremnev8.electroniccookbook.recipe.database;

import androidx.lifecycle.LiveData;

import com.kremnev8.electroniccookbook.recipe.model.Recipe;

import java.util.List;

public interface RecipeExtendedDao extends RecipeDao {

    LiveData<List<Recipe>> getRecipesWithData();

    LiveData<Recipe> getRecipeWithData(int id);

    void insertWithData(Recipe recipe);
}
