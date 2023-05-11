package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Upsert;

import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredientCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewIngredientCache;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface RecipeIngredientCacheDao {

    @Transaction
    @Query( "SELECT * FROM recipeIngredientCache WHERE recipeId = :recipeId ")
    LiveData<List<RecipeViewIngredientCache>> getIngredientCache(int recipeId);

    @Query("SELECT (SELECT COUNT(*) FROM recipeIngredientCache WHERE recipeId = :recipeId) > 0")
    Single<Boolean> hasIngredientCache(int recipeId);

    @Upsert
    long upsert(RecipeIngredientCache recipe);

    @Query("DELETE FROM recipeIngredientCache WHERE recipeId = :recipeId")
    void clearIngredientCache(int recipeId);
}
