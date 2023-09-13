package com.kremnev8.electroniccookbook.interfaces;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;

public interface IFragmentController {
    <T extends Fragment> void setFragment(Class<T> clazz, @Nullable Bundle args);
    void showLoginDialog(ILoginSuccessCallback callback, Profile profile);
    Fragment getCurrentFragment();
    boolean isViewingInFullScreen();
}
