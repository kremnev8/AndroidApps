package com.kremnev8.electroniccookbook.recipeview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.databinding.FragmentRecipeIngredientsBinding;
import com.kremnev8.electroniccookbook.recipeview.viewmodels.RecipeViewModel;

public class RecipeIngredientsFragment extends Fragment {


    private RecipeViewModel viewModel;
    private FragmentRecipeIngredientsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeIngredientsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireParentFragment()).get(RecipeViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }
}
