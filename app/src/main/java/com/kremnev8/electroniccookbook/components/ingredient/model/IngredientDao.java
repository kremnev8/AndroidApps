package com.kremnev8.electroniccookbook.components.ingredient.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM ingredients WHERE profileId = :profileId")
    LiveData<List<Ingredient>> getIngredients(int profileId);

    @Query("SELECT * FROM ingredients WHERE profileId = :profileId")
    Single<List<Ingredient>> getIngredientsOnce(int profileId);

    @Query("SELECT * FROM ingredients WHERE name = :name")
    Ingredient findIngredient(String name);

    @Upsert
    void insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Query("DELETE FROM ingredients WHERE id = :id")
    void deleteIngredientById(int id);

    @Delete
    void delete(Ingredient ingredient);
}
