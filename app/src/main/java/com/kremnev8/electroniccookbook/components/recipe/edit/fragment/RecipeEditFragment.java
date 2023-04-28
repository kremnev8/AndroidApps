package com.kremnev8.electroniccookbook.components.recipe.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kremnev8.electroniccookbook.CookBookApplication;
import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.components.recipe.edit.RecipeEditStateAdapter;
import com.kremnev8.electroniccookbook.components.recipe.edit.viewmodel.RecipeEditViewModel;
import com.kremnev8.electroniccookbook.components.recipe.list.fragment.RecipesListFragment;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeEditBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeEditFragment
        extends Fragment
        implements IMenu, TabLayoutMediator.TabConfigurationStrategy, TabLayout.OnTabSelectedListener {

    public static final String TARGET_RECIPE = "targetRecipe";

    public RecipeEditViewModel viewModel;
    private FragmentRecipeEditBinding binding;
    private RecipeEditStateAdapter adapter;

    public static RecipeEditFragment newInstance() {
        return new RecipeEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(RecipeEditViewModel.class);

        if (getArguments() != null) {
            Recipe recipe = getArguments().getParcelable(TARGET_RECIPE);
            viewModel.setData(recipe);
        }

        adapter = new RecipeEditStateAdapter(this);
        binding.pager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.pager, this).attach();

        binding.tabLayout.addOnTabSelectedListener(this);

        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    @Override
    public int getMenuName() {
        return R.string.EditRecipeMenuName;
    }

    @Override
    public int getActionText() {
        return R.string.SaveButtonText;
    }

    @Override
    public int getActionImage() {
        return 0;
    }

    @Override
    public void onAction() {
        viewModel.saveData();
        MainActivity.Instance.setFragment(RecipesListFragment.class, null);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

        switch (position) {
            case 0:
                tab.setIcon(R.drawable.ic_about);
                tab.setText(R.string.AboutTabLabel);
                break;
            case 1:
                tab.setIcon(R.drawable.ic_ingredients);
                tab.setText(R.string.IngredientsTabLabel);
                break;
            case 2:
                tab.setIcon(R.drawable.ic_steps);
                tab.setText(R.string.StepsTabLabel);
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        CookBookApplication.InputMethodManager.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}