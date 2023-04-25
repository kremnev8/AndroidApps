package com.kremnev8.electroniccookbook.recipeview.itemviewmodel;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.recipeview.model.RecipeFullIngredient;

public class RecipeViewIngredientItemViewModel extends ItemViewModel {

    public RecipeIngredient ingredient;

    public RecipeViewIngredientItemViewModel(RecipeIngredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public void setItem(Object item) {
    }

    @Override
    public long getItemId() {
        return ingredient.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recipe_about;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
