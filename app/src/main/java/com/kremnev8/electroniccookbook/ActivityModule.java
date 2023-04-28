package com.kremnev8.electroniccookbook;

import com.kremnev8.electroniccookbook.components.timers.ITimerService;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.IPhotoProvider;

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
    public IDrawerController bindDrawerController(){
        return MainActivity.Instance;
    }

    @Provides
    public IFragmentController bindFragmentController(){
        return MainActivity.Instance;
    }

    @Provides
    public ITimerService bindTimerServiceProvider(){
        return MainActivity.Instance.timersService;
    }
}
