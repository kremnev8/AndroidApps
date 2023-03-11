package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kremnev8.electroniccookbook.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.model.RecipeStep;

import java.util.List;

@Dao
public interface RecipeStepDao {

    @Query("SELECT * FROM recipeStep WHERE recipe = :id")
    public LiveData<List<RecipeStep>> getRecipeSteps(int id);

    @Insert
    public void insertAllSteps(List<RecipeStep> steps);
}
