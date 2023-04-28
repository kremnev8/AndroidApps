package com.kremnev8.electroniccookbook.components.recipe.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.components.recipe.edit.viewmodel.RecipeEditViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeEditAboutBinding;

public class RecipeEditAboutFragment extends Fragment {

    private RecipeEditViewModel viewModel;
    private FragmentRecipeEditAboutBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeEditAboutBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(requireParentFragment()).get(RecipeEditViewModel.class);

        binding.setViewModel(viewModel);

        return binding.getRoot();
    }
}
