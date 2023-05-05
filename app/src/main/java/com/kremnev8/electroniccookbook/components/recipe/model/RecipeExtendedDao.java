package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface RecipeExtendedDao extends RecipeDao {

    LiveData<List<Recipe>> getRecipesWithData(int profileId);

    LiveData<Recipe> getRecipeWithData(int id);

    void insertWithData(Recipe recipe);
}
