package com.kremnev8.electroniccookbook.components.recipe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    LiveData<List<Recipe>> getRecipes();

    @Query("SELECT * FROM recipe WHERE id = :id")
    LiveData<Recipe> getRecipe(int id);

    @Insert(entity = Recipe.class, onConflict = OnConflictStrategy.REPLACE)
    long insert(Recipe recipe);

    @Update(entity = Recipe.class)
    void update(Recipe recipe);

    @Query("DELETE FROM recipe WHERE id = :id")
    void deleteById(int id);

    @Delete(entity = Recipe.class)
    void delete(Recipe recipe);
}
