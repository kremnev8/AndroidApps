package com.kremnev8.electroniccookbook.itemViewModel;

import android.os.Handler;
import android.view.View;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.adapters.ItemViewModel;
import com.kremnev8.electroniccookbook.model.Ingredient;
import com.kremnev8.electroniccookbook.viewmodels.IngredientClickHandler;

public class IngredientViewModel extends ItemViewModel<Ingredient> {

    private final IngredientClickHandler clickHandler;
    public Ingredient ingredient;
    private Handler handler = new Handler();


    public IngredientViewModel(Ingredient ingredient, IngredientClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        setItem(ingredient);
    }

    @Override
    public void setItem(Ingredient item) {
        this.ingredient = item;
    }

    public void onRemoveClicked(View view) {
        clickHandler.onRemoveIngredient(ingredient.id);
    }

    public void onEditClicked(View view) {
        handler.postDelayed(() -> {
            clickHandler.openIngredientDetails(ingredient);
        }, 100);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_ingredient;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
