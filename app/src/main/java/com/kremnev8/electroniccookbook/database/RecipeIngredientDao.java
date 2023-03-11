package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kremnev8.electroniccookbook.model.Recipe;
import com.kremnev8.electroniccookbook.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.model.RecipeStep;

import java.util.List;

@Dao
public interface RecipeIngredientDao {

    @Query("SELECT * FROM recipeIngredient WHERE recipe = :id")
    public LiveData<List<RecipeIngredient>> getRecipeIngredients(int id);

    @Insert
    public void insertAllIngredients(List<RecipeIngredient> ingredients);
}
