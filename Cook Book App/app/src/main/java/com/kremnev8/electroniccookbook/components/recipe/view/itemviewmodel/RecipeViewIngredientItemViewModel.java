package com.kremnev8.electroniccookbook.components.recipe.view.itemviewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeViewIngredientCache;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;

public class RecipeViewIngredientItemViewModel extends ItemViewModel {

    public RecipeViewIngredientCache ingredient;
    private final DatabaseExecutor executor;
    private float currentRatio = 1;

    public RecipeViewIngredientItemViewModel(RecipeViewIngredientCache ingredient, DatabaseExecutor executor) {
        this.ingredient = ingredient;
        this.executor = executor;
    }

    @Bindable
    public boolean getUsed(){
        return ingredient.cache.ingredientUsed;
    }

    public void itemClicked(View view){
        ingredient.cache.ingredientUsed = !ingredient.cache.ingredientUsed;
        notifyChange();
        executor.upsert(ingredient.cache);
    }

    public String getAmountString(){
        return Ingredient.getAmountString(ingredient.ingredient.amount * currentRatio, ingredient.ingredient.units);
    }

    public void onYieldChanged(float ratio){
        currentRatio = ratio;
        notifyChange();
    }

    @Override
    public void setItem(Object item) {
    }

    @Override
    public long getItemId() {
        return ingredient.ingredient.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recipe_view_ingredient;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
