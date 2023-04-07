package com.kremnev8.electroniccookbook.recipeview.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeViewBinding;
import com.kremnev8.electroniccookbook.recipeview.itemviewmodel.RecipeViewStepItemViewModel;
import com.kremnev8.electroniccookbook.recipeview.viewmodels.RecipeViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeViewFragment extends Fragment {

    public static final String RECIPE_ID = "RecipeId";
    public static final String STEP_ID = "StepId";

    private RecipeViewModel viewModel;
    private FragmentRecipeViewBinding binding;
    private int scrollToStep = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        Bundle arguments = getArguments();
        if (arguments != null) {
            int recipeId = arguments.getInt(RECIPE_ID);
            viewModel.setData(recipeId);
            if (arguments.containsKey(STEP_ID)){
                scrollToStep = arguments.getInt(STEP_ID);
            }
        }
        viewModel.getViewModels().observe(getViewLifecycleOwner(), itemViewModels -> {
            if (itemViewModels.size() > 1 && scrollToStep > 0){
                int targetIndex = getTargetIndex(itemViewModels);
                binding.stepsList.scrollToPosition(targetIndex);
                scrollToStep = -1;
            }
        });
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    private int getTargetIndex(ArrayList<ItemViewModel> itemViewModels) {
        int targetIndex = -1;
        for (int i = 0; i < itemViewModels.size(); i++) {
            var model = itemViewModels.get(i);
            if (model instanceof RecipeViewStepItemViewModel){
                var step = (RecipeViewStepItemViewModel)model;
                if (step.step.step.id != scrollToStep) continue;

                targetIndex = i;
                break;
            }
        }
        return targetIndex;
    }


}