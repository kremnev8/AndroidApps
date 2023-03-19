package com.kremnev8.electroniccookbook.recipe.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeEditBinding;
import com.kremnev8.electroniccookbook.ingredient.fragment.IngredientListFragment;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.recipe.viewmodels.RecipeEditViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeEditFragment extends Fragment {

    public static final String TARGET_RECIPE = "targetRecipe";

    private RecipeEditViewModel recipeEditViewModel;
    private FragmentRecipeEditBinding binding;

    public static RecipeEditFragment newInstance() {
        return new RecipeEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        recipeEditViewModel = new ViewModelProvider(this).get(RecipeEditViewModel.class);

        if (getArguments() != null) {
            Recipe step = getArguments().getParcelable(TARGET_RECIPE);
            recipeEditViewModel.setData(step);
        }
        binding.setViewModel(recipeEditViewModel);
        binding.saveButton.setOnClickListener(v -> {
            recipeEditViewModel.saveData();
            MainActivity.Instance.setFragment(RecipesListFragment.class, null);
        });

        return binding.getRoot();
    }

}