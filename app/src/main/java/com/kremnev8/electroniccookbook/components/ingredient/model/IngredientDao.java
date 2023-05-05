package com.kremnev8.electroniccookbook.components.ingredient.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM ingredients WHERE profileId = :profileId")
    LiveData<List<Ingredient>> getIngredients(int profileId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Query("DELETE FROM ingredients WHERE id = :id")
    void deleteIngredientById(int id);

    @Delete
    void delete(Ingredient ingredient);
}
