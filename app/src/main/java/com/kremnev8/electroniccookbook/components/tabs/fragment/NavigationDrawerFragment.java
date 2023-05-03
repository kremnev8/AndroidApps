package com.kremnev8.electroniccookbook.components.tabs.fragment;

import static com.kremnev8.electroniccookbook.CookBookApplication.dataStore;

import androidx.annotation.OptIn;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.lifecycle.ViewModelProvider;

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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
@OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
public class NavigationDrawerFragment extends Fragment {

    private NavigationDrawerViewModel viewModel;
    private FragmentNavigationDrawerBinding binding;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Inject DatabaseExecutor executor;

    private static final Preferences.Key<Integer> CURRENT_PROFILE = PreferencesKeys.intKey("currentProfile");

    private static Integer getOrDefaultProfileId(Preferences prefs) {
        Integer profileId = prefs.get(CURRENT_PROFILE);
        if (profileId == null || profileId == 0) {
            profileId = 1;

            dataStore.updateDataAsync(prefs1 -> {
                var mutPref = prefs1.toMutablePreferences();
                mutPref.set(CURRENT_PROFILE, 1);
                return Single.just(mutPref);
            }).subscribe();
        }
        return profileId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNavigationDrawerBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(NavigationDrawerViewModel.class);
        binding.setViewModel(viewModel);

        dataStore.data()
                .map(NavigationDrawerFragment::getOrDefaultProfileId)
                .flatMap(id -> executor.getProfile(id))
                .subscribeOn(Schedulers.computation())
                .subscribe(profile -> mainHandler.post(() -> viewModel.setProfile(profile)),
                        throwable -> Log.e("App", "Error while getting profile", throwable));

        return binding.getRoot();
    }
}