package com.kremnev8.electroniccookbook.recipeview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.databinding.FragmentRecipeAboutBinding;
import com.kremnev8.electroniccookbook.recipe.viewmodels.RecipeEditViewModel;
import com.kremnev8.electroniccookbook.recipeview.viewmodels.RecipeViewModel;

public class RecipeAboutFragment extends Fragment {

    private RecipeViewModel viewModel;
    private FragmentRecipeAboutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeAboutBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireParentFragment()).get(RecipeViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

}
