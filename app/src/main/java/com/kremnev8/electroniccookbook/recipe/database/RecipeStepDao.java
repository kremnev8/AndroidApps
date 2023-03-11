package com.kremnev8.electroniccookbook.recipe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;

import java.util.List;

@Dao
public interface RecipeStepDao {

    @Query("SELECT * FROM recipeStep WHERE recipe = :id")
    LiveData<List<RecipeStep>> getRecipeSteps(int id);

    @Insert
    void insertAllSteps(List<RecipeStep> steps);
}
