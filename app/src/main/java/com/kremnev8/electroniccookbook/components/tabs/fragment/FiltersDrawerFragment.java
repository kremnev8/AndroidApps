package com.kremnev8.electroniccookbook.components.tabs.fragment;

import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.components.tabs.viewmodel.FiltersDrawerViewModel;
import com.kremnev8.electroniccookbook.components.tabs.viewmodel.NavigationDrawerViewModel;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.databinding.FragmentFiltersDrawerBinding;
import com.kremnev8.electroniccookbook.databinding.FragmentNavigationDrawerBinding;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;
import com.kremnev8.electroniccookbook.interfaces.ISearchStateProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class FiltersDrawerFragment extends Fragment implements TextView.OnEditorActionListener {

    private FiltersDrawerViewModel viewModel;
    private FragmentFiltersDrawerBinding binding;

    @Inject
    IDrawerController drawerController;
    @Inject
    ISearchStateProvider searchStateProvider;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFiltersDrawerBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel = new ViewModelProvider(this).get(FiltersDrawerViewModel.class);
        viewModel.setSearchData(searchStateProvider.getSearchData());
        binding.setViewModel(viewModel);

        binding.searchField.setOnEditorActionListener(this);
        binding.withField.setOnEditorActionListener(this);
        binding.withoutField.setOnEditorActionListener(this);

        drawerController.addOnDrawerStateChangedListener(this::onDrawerStateChange);

        return binding.getRoot();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            viewModel.confirmSearch();
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        drawerController.removeListener(this::onDrawerStateChange);
    }

    public void onDrawerStateChange() {
        viewModel.notifyPropertyChanged(BR.isRecipeList);
    }
}