package com.kremnev8.electroniccookbook.components.recipe.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe WHERE profileId = :profileId")
    Flowable<List<Recipe>> getRecipes(int profileId);

    @Query("SELECT * FROM recipe WHERE id = :id")
    Flowable<Recipe> getRecipe(int id);

    @Upsert(entity = Recipe.class)
    long upsert(Recipe recipe);

    @Update(entity = Recipe.class)
    void update(Recipe recipe);

    @Query("DELETE FROM recipe WHERE id = :id")
    void deleteRecipeById(int id);

    @Delete(entity = Recipe.class)
    void delete(Recipe recipe);
}
