package com.kremnev8.electroniccookbook.interfaces;

import com.kremnev8.electroniccookbook.components.profile.model.Profile;

import io.reactivex.rxjava3.core.Flowable;

public interface IProfileProvider {
    boolean loginIntoProfile(Profile profile, String password);
    Flowable<Profile> getCurrentProfile();

}
