package com.kremnev8.electroniccookbook.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.model.Ingredient;
import com.kremnev8.electroniccookbook.model.Recipe;
import com.kremnev8.electroniccookbook.model.RecipeWithData;

import java.util.List;

@Dao
public interface RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipe")
    LiveData<List<RecipeWithData>> getRecipes();

    @Insert(entity = Recipe.class, onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Update(entity = Recipe.class)
    void update(Recipe recipe);

    @Query("DELETE FROM recipe WHERE id = :id")
    void deleteById(int id);

    @Delete(entity = Recipe.class)
    void delete(Recipe recipe);
}
