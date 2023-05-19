package com.kremnev8.electroniccookbook.components.tabs.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.components.tabs.viewmodel.NavigationDrawerViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.databinding.FragmentNavigationDrawerBinding;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class NavigationDrawerFragment extends Fragment {

    private NavigationDrawerViewModel viewModel;
    private FragmentNavigationDrawerBinding binding;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Inject DatabaseExecutor executor;
    @Inject IProfileProvider profileProvider;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNavigationDrawerBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel = new ViewModelProvider(this).get(NavigationDrawerViewModel.class);
        binding.setViewModel(viewModel);

        profileProvider.getCurrentProfile()
                .subscribeOn(Schedulers.computation())
                .subscribe(profile -> mainHandler.post(() -> viewModel.setProfile(profile)),
                        throwable -> Log.e("App", "Error while getting profile", throwable));

        return binding.getRoot();
    }
}