package com.kremnev8.electroniccookbook.components.recipe.view.fragment;

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
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditFragment;
import com.kremnev8.electroniccookbook.components.recipe.view.RecipeViewStateAdapter;
import com.kremnev8.electroniccookbook.components.recipe.view.viewmodel.RecipeViewModel;
import com.kremnev8.electroniccookbook.databinding.FragmentRecipeViewBinding;
import com.kremnev8.electroniccookbook.interfaces.IMenu;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecipeViewFragment
        extends Fragment
        implements TabLayoutMediator.TabConfigurationStrategy, IMenu, TabLayout.OnTabSelectedListener {

    public static final String RECIPE_ID = "RecipeId";
    public static final String STEP_ID = "StepId";

    public RecipeViewModel viewModel;
    private FragmentRecipeViewBinding binding;
    private RecipeViewStateAdapter adapter;
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
            if (arguments.containsKey(STEP_ID)) {
                scrollToStep = arguments.getInt(STEP_ID);
            }
        }

        adapter = new RecipeViewStateAdapter(this);
        binding.pager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.pager, this).attach();
        binding.tabLayout.addOnTabSelectedListener(this);

        binding.setViewModel(viewModel);

        return binding.getRoot();
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
    public int getMenuName() {
        return R.string.recipesName;
    }

    @Override
    public int getActionText() {
        return R.string.EditMenuText;
    }

    @Override
    public int getActionImage() {
        return 0;
    }

    @Override
    public void onAction() {
        if (viewModel.recipe.getValue() == null) return;

        Bundle args = new Bundle();
        args.putParcelable(RecipeEditFragment.TARGET_RECIPE, viewModel.recipe.getValue());
        MainActivity.Instance.setFragment(RecipeEditFragment.class, args);
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