package com.kremnev8.electroniccookbook.recipe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.recipeview.model.RecipeFullIngredient;

import java.util.List;

@Dao
public interface RecipeIngredientDao {

    @Transaction
    @Query("SELECT * FROM recipeIngredient WHERE recipe = :id")
    LiveData<List<RecipeIngredient>> getRecipeIngredients(int id);

    @Insert
    void insertAllIngredients(List<RecipeIngredient> ingredients);

    @Insert
    void insertIngredient(RecipeIngredient ingredient);
}
