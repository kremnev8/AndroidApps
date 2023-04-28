package com.kremnev8.electroniccookbook.components.recipe.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.view.itemviewmodel.RecipeViewStepItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.view.viewmodel.RecipeViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeStepsBinding;

import java.util.ArrayList;

public class RecipeStepsFragment extends Fragment {

    private RecipeViewModel viewModel;
    private FragmentRecipeStepsBinding binding;
    private int scrollToStep = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeStepsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireParentFragment()).get(RecipeViewModel.class);

        binding.setLifecycleOwner(this);


        viewModel.getSteps().observe(getViewLifecycleOwner(), itemViewModels -> {
            if (itemViewModels.size() > 1 && scrollToStep > 0) {
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
            RecipeViewStepItemViewModel step = (RecipeViewStepItemViewModel)itemViewModels.get(i);
            if (step.step.step.id != scrollToStep) continue;

            targetIndex = i;
            break;
        }
        return targetIndex;
    }
}
