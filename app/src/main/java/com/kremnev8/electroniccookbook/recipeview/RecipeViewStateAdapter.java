package com.kremnev8.electroniccookbook.recipeview;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kremnev8.electroniccookbook.recipeview.fragments.RecipeAboutFragment;
import com.kremnev8.electroniccookbook.recipeview.fragments.RecipeIngredientsFragment;
import com.kremnev8.electroniccookbook.recipeview.fragments.RecipeStepsFragment;
import com.kremnev8.electroniccookbook.recipeview.fragments.RecipeViewFragment;

public class RecipeViewStateAdapter extends FragmentStateAdapter {
    private final RecipeViewFragment recipeViewFragment;

    public RecipeViewStateAdapter(@NonNull RecipeViewFragment fragment) {
        super(fragment);
        this.recipeViewFragment = fragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position){
            case 0:
                return new RecipeAboutFragment();
            case 1:
                return new RecipeIngredientsFragment();
            default:
                return new RecipeStepsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
