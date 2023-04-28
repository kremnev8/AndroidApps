package com.kremnev8.electroniccookbook.components.recipe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface RecipeIngredientDao {

    @Transaction
    @Query("SELECT * FROM recipeIngredient WHERE recipe = :id")
    LiveData<List<RecipeIngredient>> getRecipeIngredients(int id);

    @Query("SELECT * FROM recipeIngredient WHERE recipe = :id")
    Single<List<RecipeIngredient>> getRecipeIngredientsDirect(int id);

    @Insert
    void insertAllIngredients(List<RecipeIngredient> ingredients);

    @Insert
    void insertIngredient(RecipeIngredient ingredient);

    @Delete
    void deleteIngredient(RecipeIngredient ingredient);
}
