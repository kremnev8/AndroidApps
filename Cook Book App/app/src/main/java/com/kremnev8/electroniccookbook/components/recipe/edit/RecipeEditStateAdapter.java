package com.kremnev8.electroniccookbook.components.recipe.edit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditAboutFragment;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditFragment;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditIngredientsFragment;
import com.kremnev8.electroniccookbook.components.recipe.edit.fragment.RecipeEditStepsFragment;

public class RecipeEditStateAdapter extends FragmentStateAdapter {
    private final RecipeEditFragment recipeViewFragment;

    public RecipeEditStateAdapter(@NonNull RecipeEditFragment fragment) {
        super(fragment);
        this.recipeViewFragment = fragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position){
            case 0:
                return new RecipeEditAboutFragment();
            case 1:
                return new RecipeEditIngredientsFragment();
            default:
                return new RecipeEditStepsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}