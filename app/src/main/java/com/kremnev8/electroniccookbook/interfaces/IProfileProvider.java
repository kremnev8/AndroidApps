package com.kremnev8.electroniccookbook.interfaces;

import static com.kremnev8.electroniccookbook.CookBookApplication.dataStore;

import com.kremnev8.electroniccookbook.components.profile.model.Profile;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface IProfileProvider {
    boolean loginIntoProfile(Profile profile, String password);
    Flowable<Profile> getCurrentProfile();
    Flowable<Boolean> getIsFirstLoad();
    void setIsFirstLoad(boolean firstTime);

}
