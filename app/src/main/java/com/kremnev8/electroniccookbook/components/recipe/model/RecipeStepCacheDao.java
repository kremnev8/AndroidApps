package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStepCache;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewStepCache;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface RecipeStepCacheDao {

    @Transaction
    @Query( "SELECT c.* " +
            "FROM recipeStepCache AS c " +
            "JOIN recipeStep AS p ON p.recipe = c.recipeId AND p.id = c.stepId " +
            "WHERE c.recipeId = :recipeId " +
            "ORDER BY p.stepNumber")
    LiveData<List<RecipeViewStepCache>> getRecipeCache(int recipeId);

    @Query("SELECT (SELECT COUNT(*) FROM recipeStepCache WHERE recipeId = :recipeId) > 0")
    Single<Boolean> hasRecipeCache(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecipeStepCache recipe);

    @Update
    void update(RecipeStepCache recipe);

    @Query("DELETE FROM recipeStepCache WHERE recipeId = :recipeId")
    void clearRecipeCache(int recipeId);
}
