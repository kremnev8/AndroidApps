package com.kremnev8.electroniccookbook.recipe.viewmodels;

import android.os.Bundle;
import android.view.View;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.fragments.RecipeEditFragment;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;

public class RecipeViewModel extends ItemViewModel {
    public Recipe recipe;

    public RecipeViewModel(Recipe recipe) {
        setItem(recipe);
    }

    @Override
    public void setItem(Object item) {
        recipe = (Recipe) item;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    public boolean onLongTap(View view){
        Bundle args = new Bundle();
        args.putParcelable(RecipeEditFragment.TARGET_RECIPE, recipe);
        MainActivity.Instance.setFragment(RecipeEditFragment.class, args);
        return false;
    }
}
