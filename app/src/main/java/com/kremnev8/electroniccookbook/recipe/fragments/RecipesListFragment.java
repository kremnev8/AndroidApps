package com.kremnev8.electroniccookbook.recipe.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.databinding.FragmentRecipesListBinding;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.viewmodels.RecipesListViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipesListFragment extends Fragment {

    private RecipesListViewModel recipesListViewModel;
    private FragmentRecipesListBinding binding;

    public static RecipesListFragment newInstance() {
        return new RecipesListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipesListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        recipesListViewModel = new ViewModelProvider(this).get(RecipesListViewModel.class);
        binding.setViewModel(recipesListViewModel);

        binding.addButton.setOnClickListener(v -> {
            Recipe recipe = new Recipe();
            recipe.name = "My New Recipe";
            recipe.description = "This is the most amazing recipe out there!";
            recipesListViewModel.databaseExecutor.insert(recipe);
        });

        return binding.getRoot();
    }


}