package com.kremnev8.electroniccookbook.components.profile.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile")
    LiveData<List<Profile>> getProfiles();

    @Query("SELECT * FROM profile WHERE id = :id")
    Flowable<Profile> getProfile(int id);

    @Query("SELECT EXISTS (SELECT * FROM profile WHERE id = :id)")
    boolean hasProfile(int id);

    @Insert(entity = Profile.class, onConflict = OnConflictStrategy.REPLACE)
    long insertOrUpdate(Profile profile);

    @Query("DELETE FROM profile WHERE id = :id")
    void deleteProfileById(int id);
}
