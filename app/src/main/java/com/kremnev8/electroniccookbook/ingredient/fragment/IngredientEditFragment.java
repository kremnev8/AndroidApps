package com.kremnev8.electroniccookbook.ingredient.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientEditBinding;
import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.ingredient.viewmodel.IngredientEditViewModel;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class IngredientEditFragment extends Fragment {

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
        binding.saveButton.setOnClickListener(v -> {
            viewModel.saveData();
            MainActivity.Instance.setFragment(IngredientListFragment.class, null);
        });

        binding.nameField.propertyNameText.setText(R.string.nameFieldLabel);
        binding.amountField.propertyNameText.setText(R.string.amountFieldLabel);

        return binding.getRoot();
    }
}