package com.kremnev8.electroniccookbook.recipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.databinding.FragmentRecipeEditIngredientsBinding;
import com.kremnev8.electroniccookbook.recipe.viewmodels.RecipeEditViewModel;

public class RecipeEditIngredientsFragment extends Fragment {

    private RecipeEditViewModel viewModel;
    private FragmentRecipeEditIngredientsBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeEditIngredientsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireParentFragment()).get(RecipeEditViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }
}
