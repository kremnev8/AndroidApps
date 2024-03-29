package com.kremnev8.electroniccookbook.components.profile;

import androidx.annotation.OptIn;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;

import com.kremnev8.electroniccookbook.ApplicationModule;
import com.kremnev8.electroniccookbook.common.Passwords;
import com.kremnev8.electroniccookbook.common.Util;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@InstallIn(SingletonComponent.class)
@Module
@OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
public class ProfileModule implements IProfileProvider {

    private DatabaseExecutor executor;


    private static final Preferences.Key<Integer> CURRENT_PROFILE = PreferencesKeys.intKey("currentProfile");
    private static final Preferences.Key<Boolean> FIRST_LOAD = PreferencesKeys.booleanKey("firstLoad");

    private static Integer getOrDefaultProfileId(Preferences prefs) {
        Integer profileId = prefs.get(CURRENT_PROFILE);
        if (profileId == null || profileId == 0) {
            profileId = 1;
            setCurrentProfileInternal(profileId);
        }
        return profileId;
    }

    private static void setCurrentProfileInternal(int id){
        ApplicationModule.dataStore.updateDataAsync(prefs1 -> {
            var mutPref = prefs1.toMutablePreferences();
            mutPref.set(CURRENT_PROFILE, id);
            return Single.just(mutPref);
        }).subscribe();
    }

    public boolean loginIntoProfile(Profile profile, String password) {
        if (!profile.passwordRequired) {
            setCurrentProfileInternal(profile.id);
            return true;
        }

        if (Passwords.isExpectedPassword(password, profile.passwordSalt, profile.passwordHash)){
            setCurrentProfileInternal(profile.id);
            return true;
        }
        return false;
    }

    @Override
    public Flowable<Profile> getCurrentProfile() {
        return ApplicationModule.dataStore.data()
                .map(ProfileModule::getOrDefaultProfileId)
                .flatMap(executor::getProfile);
    }

    @Override
    public Flowable<Boolean> getIsFirstLoad() {
        return ApplicationModule.dataStore.data()
                .map(preferences -> {
                    if (Util.isRunningTest())
                        return false;

                    var isFirstLoad = preferences.get(FIRST_LOAD);
                    if (isFirstLoad == null)
                        return true;
                    else
                        return isFirstLoad;
                });
    }

    @Override
    public void setIsFirstLoad(boolean firstTime){
        ApplicationModule.dataStore.updateDataAsync(prefs1 -> {
            var mutPref = prefs1.toMutablePreferences();
            mutPref.set(FIRST_LOAD, firstTime);
            return Single.just(mutPref);
        }).subscribe();
    }

    @Provides
    @Singleton
    public IProfileProvider provideProfileModule(DatabaseExecutor executor){
        this.executor = executor;
        return this;
    }
}
