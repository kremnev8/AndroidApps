package com.kremnev8.electroniccookbook.components.ingredient.list.viewmodel;

import android.os.Handler;
import android.view.View;

import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.ItemViewModel;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.interfaces.IngredientClickHandler;

public class IngredientViewModel extends ItemViewModel {

    private final IngredientClickHandler clickHandler;
    public Ingredient ingredient;
    private final Handler handler = new Handler();


    public IngredientViewModel(Ingredient ingredient, IngredientClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        setItem(ingredient);
    }

    @Override
    public void setItem(Object item) {
        this.ingredient = (Ingredient) item;
    }

    @Override
    public long getItemId() {
        return ingredient.id;
    }

    public void onRemoveClicked(View view) {
        clickHandler.onRemoveIngredient(ingredient.id);
    }

    public void onEditClicked(View view) {
        handler.postDelayed(() -> clickHandler.openIngredientDetails(ingredient), 100);
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
