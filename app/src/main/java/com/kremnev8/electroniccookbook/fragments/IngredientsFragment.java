package com.kremnev8.electroniccookbook.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.IngredientDataProvider;
import com.kremnev8.electroniccookbook.viewmodels.IngredientListViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientsBinding;


public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding;
    private IngredientListViewModel ingredientListViewModel;

    public IngredientsFragment() {
        // Required empty public constructor
    }


    public static IngredientsFragment newInstance() {
        return new IngredientsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        ingredientListViewModel = new IngredientListViewModel(new IngredientDataProvider());
        binding.setViewModel(ingredientListViewModel);

        return binding.getRoot();
    }
}