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
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientListBinding;
import com.kremnev8.electroniccookbook.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.ingredient.viewmodel.IngredientListViewModel;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class IngredientListFragment extends Fragment implements IMenu {

    private FragmentIngredientListBinding binding;
    private IngredientListViewModel ingredientListViewModel;

    public IngredientListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIngredientListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        ingredientListViewModel = new ViewModelProvider(MainActivity.Instance).get(IngredientListViewModel.class);

        binding.setViewModel(ingredientListViewModel);
        binding.addButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putParcelable(IngredientEditFragment.InspectIngredient, new Ingredient());
            MainActivity.Instance.setFragment(IngredientEditFragment.class, args);
        });

        return binding.getRoot();
    }

    @Override
    public int getMenuName() {
        return R.string.IngredientsListMenuName;
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