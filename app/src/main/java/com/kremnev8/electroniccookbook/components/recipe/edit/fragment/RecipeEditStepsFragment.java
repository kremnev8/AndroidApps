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

import com.kremnev8.electroniccookbook.common.ContextMenuKind;
import com.kremnev8.electroniccookbook.common.recycler.ItemView;
import com.kremnev8.electroniccookbook.components.recipe.edit.viewmodel.RecipeEditViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeEditStepsBinding;

public class RecipeEditStepsFragment extends Fragment {


    private RecipeEditViewModel viewModel;
    private FragmentRecipeEditStepsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeEditStepsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireParentFragment()).get(RecipeEditViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        registerForContextMenu(binding.stepsList);

        return binding.getRoot();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ItemView.ItemExtraInfo extra = (ItemView.ItemExtraInfo)item.getMenuInfo();
        if (extra.menuKind != ContextMenuKind.RECIPE_STEP) return false;

        viewModel.removeStep(extra.index);
        return true;
    }
}
