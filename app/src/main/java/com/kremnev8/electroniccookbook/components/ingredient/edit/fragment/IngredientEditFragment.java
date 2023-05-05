package com.kremnev8.electroniccookbook.components.ingredient.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.components.ingredient.edit.viewmodel.IngredientEditViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.list.fragment.IngredientListFragment;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientEditBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class IngredientEditFragment extends Fragment implements IMenu {

    public static final String InspectIngredient = "targetIngredient";

    private FragmentIngredientEditBinding binding;
    private IngredientEditViewModel viewModel;

    public IngredientEditFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("deprecated")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIngredientEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(MainActivity.Instance).get(IngredientEditViewModel.class);

        if (getArguments() != null) {
            Ingredient ingredient = getArguments().getParcelable(InspectIngredient);
            viewModel.setData(ingredient);
        }

        binding.setViewModel(viewModel);

        return binding.getRoot();
    }
    @Override
    public int getMenuName() {
        return R.string.edit_ingredient_label;
    }

    @Override
    public int getActionText() {
        return R.string.save_label;
    }

    @Override
    public int getActionImage() {
        return 0;
    }

    @Override
    public void onAction() {
        viewModel.saveData();
        MainActivity.Instance.setFragment(IngredientListFragment.class, null);
    }
}