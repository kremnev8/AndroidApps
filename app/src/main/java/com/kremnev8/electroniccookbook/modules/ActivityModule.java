package com.kremnev8.electroniccookbook.modules;

import android.content.Context;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.mifmif.common.regex.Main;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class ActivityModule {


    @Provides
    public IPhotoProvider bindPhotoProvider(){
        return MainActivity.Instance;
    }
}
