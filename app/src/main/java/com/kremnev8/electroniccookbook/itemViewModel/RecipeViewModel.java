package com.kremnev8.electroniccookbook.itemViewModel;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.model.Recipe;

public class RecipeViewModel extends ItemViewModel<Recipe> {
    public Recipe recipe;

    public RecipeViewModel(Recipe recipe) {
        setItem(recipe);
    }

    @Override
    public void setItem(Recipe item) {
        recipe = item;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
