package com.kremnev8.electroniccookbook.interfaces;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public interface IFragmentController {
    <T extends Fragment> void setFragment(Class<T> clazz, @Nullable Bundle args);
}
