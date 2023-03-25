package com.kremnev8.electroniccookbook.recipeview.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipeview.model.ViewCache;

import java.util.List;

@Dao
public interface ViewCacheDao {

    @Query("SELECT * FROM viewCache WHERE recipeId == :recipeId")
    LiveData<List<ViewCache>> getRecipeCache(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ViewCache recipe);

    @Update
    void update(ViewCache recipe);

    @Query("DELETE FROM viewCache WHERE recipeId = :recipeId")
    void clearCache(int recipeId);
}
