package com.kremnev8.electroniccookbook.recipe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface RecipeStepDao {

    @Query("SELECT * FROM recipeStep WHERE recipe = :id ORDER BY stepNumber")
    LiveData<List<RecipeStep>> getRecipeSteps(int id);

    @Query("SELECT * FROM recipeStep WHERE recipe = :id ORDER BY stepNumber")
    Single<List<RecipeStep>> getRecipeStepsDirect(int id);

    @Insert
    void insertStep(RecipeStep step);

    @Insert
    void insertAllSteps(List<RecipeStep> steps);

    @Update
    void updateAllSteps(List<RecipeStep> steps);
}
