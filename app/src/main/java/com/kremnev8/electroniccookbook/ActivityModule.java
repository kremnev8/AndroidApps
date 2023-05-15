package com.kremnev8.electroniccookbook;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.timers.ITimerService;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IFragmentController;
import com.kremnev8.electroniccookbook.interfaces.ILoginSuccessCallback;
import com.kremnev8.electroniccookbook.interfaces.IMediaProvider;
import com.kremnev8.electroniccookbook.interfaces.IMediaRequestCallback;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class ActivityModule {

    @Provides
    @Singleton
    public IMediaProvider bindPhotoProvider(){
        return new IMediaProvider() {
            @Override
            public void requestPhoto(IMediaRequestCallback callback) {
                MainActivity.Instance.requestPhoto(callback);
            }

            @Override
            public void requestMedia(IMediaRequestCallback callback) {
                MainActivity.Instance.requestMedia(callback);
            }
        };
    }

    @Provides
    @Singleton
    public IDrawerController bindDrawerController(){
        return new IDrawerController() {
            @Override
            public void toggleDrawer(DrawerKind kind) {
                MainActivity.Instance.toggleDrawer(kind);
            }

            @Override
            public void closeDrawer(DrawerKind kind) {
                MainActivity.Instance.closeDrawer(kind);
            }
        };
    }

    @Provides
    @Singleton
    public IFragmentController bindFragmentController(){
        return new IFragmentController() {
            @Override
            public <T extends Fragment> void setFragment(Class<T> clazz, @Nullable Bundle args) {
                MainActivity.Instance.setFragment(clazz, args);
            }

            @Override
            public void showLoginDialog(ILoginSuccessCallback callback, Profile profile) {
                MainActivity.Instance.showLoginDialog(callback, profile);
            }
        };
    }

    @Provides
    public ITimerService bindTimerServiceProvider(){
        return MainActivity.Instance.timersService;
    }
}
