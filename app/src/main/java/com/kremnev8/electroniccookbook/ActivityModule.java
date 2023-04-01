package com.kremnev8.electroniccookbook;

import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;
import com.kremnev8.electroniccookbook.services.ITimerService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class ActivityModule {

    @Provides
    public IPhotoProvider bindPhotoProvider(){
        return MainActivity.Instance;
    }

    @Provides
    public ITimerService bindTimerServiceProvider(){
        return MainActivity.Instance.timersService;
    }
}
