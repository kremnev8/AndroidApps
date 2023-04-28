package com.kremnev8.electroniccookbook.components.recipe.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.common.recycler.IContextMenuPositionProvider;
import com.kremnev8.electroniccookbook.components.recipe.edit.viewmodel.RecipeEditViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeEditIngredientsBinding;

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

        registerForContextMenu(binding.stepsList);

        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    public int getPosition(){
        return ((IContextMenuPositionProvider) binding.stepsList.getAdapter()).getMenuPosition();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = getPosition();
        viewModel.removeIngredient(position);
        return true;
    }
}
