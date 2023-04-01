package com.kremnev8.electroniccookbook.recipeview.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.databinding.FragmentRecipeViewBinding;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipeview.viewmodels.RecipeViewModel;
import com.kremnev8.electroniccookbook.services.TimersService;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeViewFragment extends Fragment {

    public static final String TARGET_RECIPE = "targetRecipe";

    private RecipeViewModel viewModel;
    private FragmentRecipeViewBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        if (getArguments() != null) {
            Recipe step = getArguments().getParcelable(TARGET_RECIPE);
            viewModel.setData(step);
        }
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }
}