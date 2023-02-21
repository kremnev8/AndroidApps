package com.kremnev8.electroniccookbook.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kremnev8.electroniccookbook.IngredientService;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.IngredientAdapter;
import com.kremnev8.electroniccookbook.databinding.FragmentIngredientsBinding;


public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding;
    private IngredientAdapter adapter;

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

        var manager = new LinearLayoutManager(getContext()); // LayoutManager
        adapter = new IngredientAdapter(); // Создание объекта
        adapter.setIngredients(new IngredientService().ingredients);// Заполнение данными

        binding.ingredientsList.setLayoutManager(manager); // Назначение LayoutManager для RecyclerView
        binding.ingredientsList.setAdapter(adapter); // Назначение адаптера для RecyclerView

        return binding.getRoot();
    }
}