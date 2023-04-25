package com.kremnev8.electroniccookbook.recipeview.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.recipeview.model.RecipeIngredientCache;
import com.kremnev8.electroniccookbook.recipeview.model.RecipeViewIngredientCache;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface RecipeIngredientCacheDao {

    @Transaction
    @Query( "SELECT * FROM recipeIngredientCache WHERE recipeId = :recipeId ")
    LiveData<List<RecipeViewIngredientCache>> getIngredientCache(int recipeId);

    @Query("SELECT (SELECT COUNT(*) FROM recipeIngredientCache WHERE recipeId = :recipeId) > 0")
    Single<Boolean> hasIngredientCache(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecipeIngredientCache recipe);

    @Update
    void update(RecipeIngredientCache recipe);

    @Query("DELETE FROM recipeIngredientCache WHERE recipeId = :recipeId")
    void clearIngredientCache(int recipeId);
}
