package com.kremnev8.electroniccookbook.components.profile.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile")
    LiveData<List<Profile>> getProfiles();

    @Query("SELECT * FROM profile")
    Single<List<Profile>> getProfilesBlocking();

    @Query("SELECT * FROM profile WHERE id = :id")
    Flowable<Profile> getProfile(int id);

    @Query("SELECT EXISTS (SELECT * FROM profile WHERE id = :id)")
    boolean hasProfile(int id);

    @Upsert(entity = Profile.class)
    long upsert(Profile profile);

    @Query("DELETE FROM profile WHERE id = :id")
    void deleteProfileById(int id);
}
