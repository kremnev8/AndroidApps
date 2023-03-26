package com.kremnev8.electroniccookbook.recipeview.itemviewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.ItemViewModel;
import com.kremnev8.electroniccookbook.recipe.model.Recipe;

public class RecipeViewMainItemViewModel extends ItemViewModel {

    public LiveData<Recipe> recipe;

    public RecipeViewMainItemViewModel(LiveData<Recipe> recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setItem(Object item) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_view_main;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
