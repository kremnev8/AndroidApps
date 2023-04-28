package com.kremnev8.electroniccookbook.components.recipe.list.itemviewmodel;

import android.os.Bundle;
import android.view.View;

import com.kremnev8.electroniccookbook.MainActivity;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.IHasContextMenu;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.view.fragment.RecipeViewFragment;

public class RecipeItemViewModel extends ItemViewModel implements IHasContextMenu {
    public Recipe recipe;

    public RecipeItemViewModel(Recipe recipe) {
        setItem(recipe);
    }

    @Override
    public void setItem(Object item) {
        recipe = (Recipe) item;
    }

    @Override
    public long getItemId() {
        return recipe.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe;
    }

    @Override
    public int getViewType() {
        return 1;
    }

    public void onClick(View view){
        Bundle args = new Bundle();
        args.putInt(RecipeViewFragment.RECIPE_ID, recipe.id);
        MainActivity.Instance.setFragment(RecipeViewFragment.class, args);
    }

    @Override
    public int getMenuResId() {
        return R.menu.edit_menu;
    }
}
