package com.kremnev8.electroniccookbook.recipeview.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.recipeview.model.ViewCache;
import com.kremnev8.electroniccookbook.recipeview.model.ViewStepCache;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ViewCacheDao {

    //@Query("SELECT * FROM viewCache WHERE recipeId == :recipeId")
    @Transaction
    @Query( "SELECT c.* " +
            "FROM viewCache AS c " +
            "JOIN recipeStep AS p ON p.recipe = c.recipeId AND p.id = c.stepId " +
            "WHERE c.recipeId = :recipeId " +
            "ORDER BY p.stepNumber")
    LiveData<List<ViewStepCache>> getRecipeCache(int recipeId);

    @Query("SELECT (SELECT COUNT(*) FROM viewCache WHERE recipeId = :recipeId) > 0")
    Single<Boolean> hasCache(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ViewCache recipe);

    @Update
    void update(ViewCache recipe);

    @Query("DELETE FROM viewCache WHERE recipeId = :recipeId")
    void clearCache(int recipeId);
}
