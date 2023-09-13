package com.kremnev8.electroniccookbook.components.recipe.importPage.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.components.recipe.importPage.viewmodel.ImportRecipeViewModel;
import com.kremnev8.electroniccookbook.components.recipe.list.fragment.RecipesListFragment;
import com.kremnev8.electroniccookbook.databinding.FragmentImportRecipeBinding;
import com.kremnev8.electroniccookbook.interfaces.IDrawerController;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ImportRecipeFragment extends Fragment implements IMenu {

    private ImportRecipeViewModel recipesListViewModel;
    private FragmentImportRecipeBinding binding;

    public static RecipesListFragment newInstance() {
        return new RecipesListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentImportRecipeBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        recipesListViewModel = new ViewModelProvider(this).get(ImportRecipeViewModel.class);
        binding.setViewModel(recipesListViewModel);

        return binding.getRoot();
    }

    @Override
    public int getMenuName() {
        return R.string.import_recipe_label;
    }

    @Override
    public int getActionText() {
        return 0;
    }

    @Override
    public int getActionImage() {
        return 0;
    }

    @Override
    public void onAction() {
    }
}