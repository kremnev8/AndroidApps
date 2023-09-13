package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface RecipeStepDao {

    @Query("SELECT * FROM recipeStep WHERE recipe = :id ORDER BY stepNumber")
    LiveData<List<RecipeStep>> getRecipeSteps(int id);

    @Query("SELECT * FROM recipeStep WHERE recipe = :id ORDER BY stepNumber")
    Single<List<RecipeStep>> getRecipeStepsDirect(int id);

    @Upsert
    void upsertStep(RecipeStep step);

    @Upsert
    void upsertTwoSteps(RecipeStep step1, RecipeStep step2);

    @Upsert
    void upsertAllSteps(List<RecipeStep> steps);

    @Delete
    void deleteStep(RecipeStep step);
}
